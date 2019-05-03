package services;

import dto.AccountDto;
import dto.TransferModelMapper;
import helpers.TransferMoneyException;
import io.ebean.Ebean;
import models.Account;
import models.Transfer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @see RepositoryService
 */
public class RepositoryServiceImpl implements RepositoryService {

    private final Logger log = LoggerFactory.getLogger(RepositoryServiceImpl.class);

    private Lock lock = new ReentrantLock();
    private Condition sufficientFundsCondition = lock.newCondition();

    @Override
    public Collection<Account> listAccounts() {
        List<Account> allAccounts = Account.find.all();
        return allAccounts;
    }

    @Override
    public Account upsertAccount(AccountDto accountDto) {
        Account account = TransferModelMapper.upsertAccountDetails(accountDto.getId() == null ? new Account() :
                Account.find.byId(accountDto.getId()), accountDto);
        account.save();
        return account;
    }

    @Override
    public Transfer transferMoney(@Nonnull Account accountFrom, @Nonnull Account accountTo, @Nonnull BigDecimal amount) {

        log.debug("[{}] will transfer money from {} to {} amount {}", Thread.currentThread().getId(), accountFrom.getId(), accountTo.getId(), amount.doubleValue());

        Transfer transfer;

        //lock transfer operation on both accounts
        lock.lock();

        //perform operation in single transaction
        Ebean.beginTransaction();
        try {

            //checks (and waits) for available funds
            checkForSufficientFunds(accountFrom, amount);

            accountFrom.withdraw(amount);
            accountTo.deposit(amount);
            accountFrom.save();
            accountTo.save();

            transfer = new Transfer(accountFrom, accountTo, amount);
            transfer.save();

            //wake up all threads waiting for sufficient fund
            sufficientFundsCondition.signalAll();

            //commit transaction for withdrawal and deposit
            Ebean.commitTransaction();

            log.debug("[{}] money transferred ", Thread.currentThread().getId());

        } catch (InterruptedException e) {
            throw new TransferMoneyException("insufficient funds");
        } finally {
            Ebean.endTransaction();
            lock.unlock();
        }

        return transfer;
    }

    /**
     * Checks for sufficient funds.
     * If funds are not sufficient will wait few iterations for funds to be deposited
     */
    private void checkForSufficientFunds(@Nonnull Account accountFrom, @Nonnull BigDecimal amount) throws InterruptedException {
        int retryCounter = 0;

        while (amount.compareTo(accountFrom.getBalance()) == 1) {
            if (retryCounter > 2) {
                log.debug("[{}] retry limit reached", Thread.currentThread().getId());
                break;
            }
            log.debug("[{}] insufficient funds, will wait for deposit", Thread.currentThread().getId());
            sufficientFundsCondition.await(10, TimeUnit.SECONDS);
            accountFrom.refresh();
            log.debug("[{}] waited, will try to check again", Thread.currentThread().getId());
            ++retryCounter;
        }
        if (retryCounter > 2) {
            throw new TransferMoneyException("insufficient funds");
        }
    }

    @Override
    public Collection<Transfer> listTransfers() {
        return Transfer.find.all();
    }
}