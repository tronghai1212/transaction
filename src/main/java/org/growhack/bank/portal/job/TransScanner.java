package org.growhack.bank.portal.job;

import org.growhack.bank.portal.config.AdminConfig;
import org.growhack.bank.portal.dao.DBConnection;
import org.growhack.bank.portal.dao.TransDB;
import org.growhack.bank.portal.driver.BankDriver;
import org.growhack.bank.portal.entity.TransactitonBankEntity;
import org.growhack.bank.portal.model.Transaction;
import org.growhack.bank.portal.service.BotService;
import org.growhack.bank.portal.service.UpdateTransactionService;
import com.lvtech.rd.common.config.Configuration;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionNotFoundException;
import org.poc.spider.phantom.exception.DriverInterruptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

@Data
@NoArgsConstructor
public abstract class TransScanner implements Runnable {

    protected String bankName;
    protected Configuration configuration;
    protected BankDriver bankDriver;
    protected AdminConfig adminConfig;
    protected String bankCode;

    final static int MAX_LOGIN_FAIL_CONTINUE = 3;
    final static long TIME_SLEEP = 1 * 60 * 1000l;
    final static long TIME_SLEEP_ON_FAIL = 5 * 60 * 1000l;
    final static int RESTART_AFTER_ROUND = 60;

    public static Logger LOG = LoggerFactory.getLogger(TransScanner.class);


    @Deprecated
    public TransScanner(String bankName,
                        Configuration configuration,
                        BankDriver bankDriver) {
        this.bankName = bankName;
        this.configuration = configuration;
        this.bankDriver = bankDriver;
    }

    public TransScanner(String bankName, String bankCode,
                        Configuration configuration,
                        BankDriver bankDriver, AdminConfig adminConfig) {
        this.bankName = bankName;
        this.configuration = configuration;
        this.bankDriver = bankDriver;
        this.adminConfig = adminConfig;
        this.bankCode = bankCode;
    }

    public void run() {

        boolean isInActiveState = true;
        int numRoundLoginFail = 0;
        int numberRound = 0;
        while (true) {
            try {
                boolean newState = adminConfig.getActiveStatusOfBank(bankCode);
                numberRound++;
                if (newState == true) {

                    LOG.info("New loop state ....");

                    // initial
                    if (bankDriver.getSpiderPhantomJsDriver() == null
                            || bankDriver.getSpiderPhantomJsDriver().getWebDriver() == null
                            || ((RemoteWebDriver) bankDriver
                            .getSpiderPhantomJsDriver()
                            .getWebDriver())
                            .getSessionId() == null)
                        bankDriver.createDriver();


                    try {
                        if (!bankDriver.isLogged())
                            bankDriver.login();
                        if (!bankDriver.isLogged()) {
                            LOG.info("Bank " + bankName + " can't login in web driver");
                            BotService botService = new BotService();
                            botService.reportBanks(bankName);
                            numRoundLoginFail++;
                        } else {
                            LOG.info("Login success, next scan table data");
                            List<Transaction> rs = bankDriver.scanTransactions();
                            updateTransaction(rs);
                        }
                    } catch (Exception e) {
                        if (e != null && e.getMessage().contains("SessionNotFoundException")) {
                            bankDriver.createDriver();
                            LOG.info("Renew new bank driver");
                        } else
                            LOG.info("Can't execute ", e);
                    }
                }

                if (numRoundLoginFail >= MAX_LOGIN_FAIL_CONTINUE) {
                    LOG.info("Bank " + bankName + " login fail : " + numberRound + " turn");
                    BotService botService = new BotService();
                    botService.reportBanks(bankName, numRoundLoginFail);
                    Thread.sleep(TIME_SLEEP_ON_FAIL);
                    bankDriver.closeDriver();
                    bankDriver.createDriver();
                    numRoundLoginFail = 0;
                    continue;
                }

                if (numberRound > RESTART_AFTER_ROUND) {
                    bankDriver.closeDriver();
                    bankDriver.createDriver();
                    numberRound = 0;
                }


                if (newState == false && isInActiveState == true) {
                    try {
                        // logout first
                        bankDriver.logout();
                    } catch (DriverInterruptException e) {
                        LOG.info("Driver logout has exception ", e);
                    }
                    numberRound = 0;
                    bankDriver.closeDriver();
                }

                isInActiveState = newState;

                Thread.sleep(TIME_SLEEP);
            } catch (Exception e) {
                LOG.info("Thread execute error", e);
                try {
                    Thread.sleep(TIME_SLEEP_ON_FAIL);
                    bankDriver.closeDriver();
                    bankDriver.createDriver();
                } catch (Exception ex) {
                    LOG.info("Thread sleep fail ", ex);
                }
            }

        }

    }

    public void scan() {
        List<Transaction> rs = null;
        try {
            bankDriver.login();
            if (!bankDriver.isLogged()) {
                LOG.info("Login failed bank " + bankName);
                BotService botService = new BotService();
                botService.reportBanks(bankName);
            } else {
                rs = bankDriver.scanTransactions();
                bankDriver.logout();
            }
        } catch (Exception e) {
            LOG.warn("Driver execute error ", e);
        } finally {
            bankDriver.closeDriver();
        }

        if (rs != null) {
            updateTransaction(rs);
        }
    }

    public void updateTransaction(List<Transaction> rs) {
        TransDB transDB = new TransDB(bankName);
        transDB.load();
        for (Transaction t : rs) {
            if (t.getTransId() != null) {

                if (t.getAmount().startsWith("-")) {
                    String bankSenderName = t.getBankOfReceiver();
                    String bankSenderAcc = t.getReceiverAccount();
                    String bankSenderId = t.getReceiverId();
                    t.setBankOfReceiver(t.getBankOfSender());
                    t.setReceiverAccount(t.getSendAccount());
                    t.setReceiverId(t.getSenderId());
                    t.setSendAccount(bankSenderAcc);
                    t.setBankOfSender(bankSenderName);
                    t.setSenderId(bankSenderId);
                }


                t.setBankOfReceiver(bankName);
                LOG.info("The transaction is : " + t.toString());
                if (transDB.isCollected(t.getTransId())) {
                    LOG.info("Transaction is collected : " + t.getTransId());
                } else {
                    transDB.updateTransaction(t);
//                    UpdateTransactionService.updateTransaction(t);
                    TransactitonBankEntity entity = new TransactitonBankEntity();
                    entity.convertToTransactionBank(t);
                    DBConnection connection = new DBConnection();
                    connection.updateAccount(entity);
                    LOG.info("Add new transaction: " + t);
                }
            }
        }
        try {
            transDB.save();
        } catch (IOException e) {
            LOG.warn("Update data into db fail: ", e);
        }
    }
}
