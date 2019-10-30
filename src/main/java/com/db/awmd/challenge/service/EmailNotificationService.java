package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class EmailNotificationService implements NotificationService {

    //private Logger log = Logger.getLogger("EmailNotificationService");
    private static final Logger log = LoggerFactory.getLogger("EmailNotificationService");

    @Override
    public void notifyAboutTransfer(Account account, String transferDescription) {
        //THIS METHOD SHOULD NOT BE CHANGED - ASSUME YOUR COLLEAGUE WILL IMPLEMENT IT
        log.info("Sending notification to owner of {}: {}", account.getAccountId(), transferDescription);
    }

}
