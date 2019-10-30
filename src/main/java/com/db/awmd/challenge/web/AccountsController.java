package com.db.awmd.challenge.web;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.NegativeBalance;
import com.db.awmd.challenge.exception.OverdraftException;
import com.db.awmd.challenge.service.AccountsService;
import javax.validation.Valid;

import com.db.awmd.challenge.service.NotificationService;
import org.apache.tomcat.util.http.fileupload.MultipartStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@RequestMapping("/v1/accounts")

public class AccountsController {

  private Logger log = Logger.getLogger("AccountsController");

  private final AccountsService accountsService;

  @Autowired
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



  @PostMapping(path="/moneyTransfer", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Object> moneyTransfer(@RequestBody String accountFrom, @RequestBody String accountTo, @RequestBody String amount) {
    log.info("from accountFrom {}", accountFrom);
    log.info("from accountTo {}", accountTo);
    log.info("from amount {}", amount);
    try {
      this.accountsService.transferMoney(accountFrom, accountTo, Float.parseFloat(amount));
      Account fromAccount = this.accountsService.getAccount(accountFrom);
      Account toAccount = this.accountsService.getAccount(accountTo);
      //added notification to transfer amount
      notificationService.notifyAboutTransfer(fromAccount, "Amount :" + amount + " transferred to " + accountTo);
      //added notification for credit information
      notificationService.notifyAboutTransfer(toAccount, "Amount :" + amount + " get credited from account : " + fromAccount.getAccountId());
    } catch (DuplicateAccountIdException daie) {
      return new ResponseEntity<>(daie.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (NegativeBalance nb ) {
      return new ResponseEntity<>(nb.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (OverdraftException oe) {
      return new ResponseEntity<>(oe.getMessage(), HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

}
