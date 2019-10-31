package com.db.awmd.challenge.web;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.MoneyTransfer;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.NegativeBalance;
import com.db.awmd.challenge.exception.OverdraftException;
import com.db.awmd.challenge.service.AccountsService;
import com.db.awmd.challenge.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
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
     * added for money transfer method
     * @param moneyTransfer - added model for map JSON inputs
     * @return return response entity obj
     */
    @PostMapping(path = "/moneyTransfer", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> moneyTransfer(@RequestBody MoneyTransfer moneyTransfer) {
        log.info("from accountFrom {}", moneyTransfer.getAccountFrom());
        log.info("from accountTo {}", moneyTransfer.getAccountTo());
        log.info("from amount {}", moneyTransfer.getAmount());
        try {
            this.accountsService.transferMoney(moneyTransfer.getAccountFrom(), moneyTransfer.getAccountTo(), moneyTransfer.getAmount().floatValue());
            Account fromAccount = this.accountsService.getAccount(moneyTransfer.getAccountFrom());
            Account toAccount = this.accountsService.getAccount(moneyTransfer.getAccountTo());
            //added notification to transfer amount
            log.info(fromAccount + ", Amount :" + moneyTransfer.getAmount() + " transferred to " + moneyTransfer.getAccountTo());
            log.info(toAccount + ", Amount :" + moneyTransfer.getAmount() + " get credited from account : " + fromAccount.getAccountId());
            // notificationService.notifyAboutTransfer(fromAccount, "Amount :" + moneyTransfer.getAmount() + " transferred to " + moneyTransfer.getAccountTo());
            //added notification for credit information
            // notificationService.notifyAboutTransfer(toAccount, "Amount :" + moneyTransfer.getAmount() + " get credited from account : " + fromAccount.getAccountId());
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
