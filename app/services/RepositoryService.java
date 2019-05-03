package services;

import com.google.inject.ImplementedBy;
import dto.AccountDto;
import models.Account;
import models.Transfer;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * Service for creating/updating accounts and transfer money between.
 */

@ImplementedBy(RepositoryServiceImpl.class)
public interface RepositoryService {

    /**
     * Lists all accounts
     */
    Collection<Account> listAccounts();

    /**
     * Creates or updates account by given data.
     */
    Account upsertAccount(AccountDto accountDto);


    /** Transfer money between given accounts.
     * Currencies are expected to be the same on both accounts. If currencies are different exception is thrown
     * @return transfer unique id
     */
    Transfer transferMoney(Account accountFrom, Account accountTo, BigDecimal amount);

    /**
     * List all transfers
     */
    Collection<Transfer> listTransfers();
}
