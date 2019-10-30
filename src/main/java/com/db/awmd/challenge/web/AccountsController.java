package com.db.awmd.challenge.web;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.NegativeBalance;
import com.db.awmd.challenge.exception.OverdraftException;
import com.db.awmd.challenge.service.AccountsService;
import com.db.awmd.challenge.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/accounts")

public class AccountsController {


    private final AccountsService accountsService;
    private Logger log = LoggerFactory.getLogger("AccountsController");

    private NotificationService notificationService;

    @Autowired
    public AccountsController(AccountsService accountsService) {
        this.accountsService = accountsService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createAccount(@RequestBody @Valid Account account) {
        log.info("Creating account {}", account);
        try {
            this.accountsService.createAccount(account);
        } catch (DuplicateAccountIdException daie) {
            return new ResponseEntity<>(daie.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping(path = "/{accountId}")
    public Account getAccount(@PathVariable String accountId) {
        log.info("Retrieving account for id {}", accountId);

        return this.accountsService.getAccount(accountId);
    }

    /**
     * added moneyTransfer method
     *
     * @param accountFrom - from which account we have to transfer the amount
     * @param accountTo   - to which account we have to transfer the amount
     * @param amount      - amount to be transered
     * @return - return response entity object
     */
    @PostMapping(path = "/moneyTransfer", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> moneyTransfer(@RequestBody String accountFrom, @RequestBody String accountTo, @RequestBody Float amount) {
        log.info("from accountFrom {}", accountFrom);
        log.info("from accountTo {}", accountTo);
        log.info("from amount {}", amount);
        try {
            this.accountsService.transferMoney(accountFrom, accountTo, amount);
            Account fromAccount = this.accountsService.getAccount(accountFrom);
            Account toAccount = this.accountsService.getAccount(accountTo);
            //added notification to transfer amount
            notificationService.notifyAboutTransfer(fromAccount, "Amount :" + amount + " transferred to " + accountTo);
            //added notification for credit information
            notificationService.notifyAboutTransfer(toAccount, "Amount :" + amount + " get credited from account : " + fromAccount.getAccountId());
        } catch (DuplicateAccountIdException daie) {
            return new ResponseEntity<>(daie.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (NegativeBalance nb) {
            return new ResponseEntity<>(nb.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (OverdraftException oe) {
            return new ResponseEntity<>(oe.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
}
