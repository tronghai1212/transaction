package org.growhack.bank.portal.driver.seabank;

import org.growhack.bank.portal.driver.BankDriver;
import org.growhack.bank.portal.driver.mbbank.MBSelenium;
import org.growhack.bank.portal.model.Transaction;
import com.lvtech.rd.common.config.Configuration;
import org.poc.spider.phantom.core.SpiderPhantomJsDriver;
import org.poc.spider.phantom.exception.DriverInterruptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SeabankDriver extends BankDriver {

    public static Logger LOG = LoggerFactory.getLogger(SeabankDriver.class);

    private SeabankSelenium seabankSelenium;

    private String account;
    private String password;

    public SeabankDriver(Configuration configuration, String account,
                     String password){
        this.bankName = "SeaBank";
        this.account = account;
        this.password = password;
        this.configuration = configuration;
        createDriver();
        seabankSelenium = new SeabankSelenium(spiderPhantomJsDriver);
    }

    @Override
    public void createDriver() {
        super.createDriver();
        seabankSelenium = new SeabankSelenium(spiderPhantomJsDriver);
    }


    public boolean isLogged() throws DriverInterruptException{
        return seabankSelenium.isLogin();
    }

    public void login() throws DriverInterruptException {
        LOG.info("Start login process ... ");
        seabankSelenium.visitHomePage();
        seabankSelenium.login(account, password);
    }

    public List<Transaction> scanTransactions() throws DriverInterruptException{
        return seabankSelenium.scan();
    }


    public SpiderPhantomJsDriver getSpiderPhantomJsDriver() {
        return spiderPhantomJsDriver;
    }

    public void setSpiderPhantomJsDriver(SpiderPhantomJsDriver spiderPhantomJsDriver) {
        this.spiderPhantomJsDriver = spiderPhantomJsDriver;
    }

    public void logout() throws DriverInterruptException{

    }

}
