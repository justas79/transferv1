package dto;

import helpers.TransferMoneyException;
import models.Account;

public class TransferModelMapper {

    /**
     * copies values from Account dto to Account model object
     */
    public static Account upsertAccountDetails(Account account, AccountDto accountDto) {
        if (account == null) {
            throw new TransferMoneyException("could not find an Account by given id");
        }

        //update balance only if it's new account
        if (account.getId() == null && accountDto.getBalance() != null) {
            account.setBalance(accountDto.getBalance());
        }

        //update currency only if it's new account
        if (account.getId() == null && accountDto.getCurrencyCode() != null) {
            account.setCurrencyCode(accountDto.getCurrencyCode());
        }

        if (accountDto.getName() != null) {
            account.setName(accountDto.getName());
        }

        if (accountDto.getActive() != null) {
            account.setActive(accountDto.isActive());
        }

        return account;
    }
}
