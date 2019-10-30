package com.db.awmd.challenge.repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.NegativeBalance;
import com.db.awmd.challenge.exception.OverdraftException;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class AccountsRepositoryInMemory implements AccountsRepository {

    private final Map<String, Account> accounts = new ConcurrentHashMap<>();

    @Override
    public void createAccount(Account account) throws DuplicateAccountIdException {
        Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);
        if (previousAccount != null) {
            throw new DuplicateAccountIdException(
                    "Account id " + account.getAccountId() + " already exists!");
        }
    }

    @Override
    public Account getAccount(String accountId) {
        return accounts.get(accountId);
    }

    @Override
    public void clearAccounts() {
        accounts.clear();
    }

    public void transferMoeny(String fromAccountId, String toAccountId, float amount) {
        Account fromAccount = accounts.get(fromAccountId);
        Account toAccount = accounts.get(toAccountId);
        //should be positive balance
        if (fromAccount.getBalance().intValue() > 0) {
            //can not be overdraft
            if ((fromAccount.getBalance().intValue() - amount) > 0) {
                // transfer money
                BigDecimal transferAmount = new BigDecimal(0);
                transferAmount.add(toAccount.getBalance());
                transferAmount.add(new BigDecimal(amount));
                toAccount.setBalance(transferAmount);
            } else {
                //throw amount not overdraft
                throw new OverdraftException("Amount is not enough to transfer balance");
            }
        } else {
            // throw amount can't be negative
            throw new NegativeBalance("Amount is not negative to transfer the balance");
        }
    }
}
