package org.growhack.bank.portal.driver.sacombank;

import org.growhack.bank.portal.driver.BankDriver;
import org.growhack.bank.portal.model.Transaction;
import com.lvtech.rd.common.config.Configuration;
import org.poc.spider.phantom.core.SpiderPhantomJsDriver;
import org.poc.spider.phantom.exception.DriverInterruptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SacombankDriver extends BankDriver {

    public static Logger LOG = LoggerFactory.getLogger(SacombankDriver.class);

    private SacombankSelenium sacombankSelenium;

    private String account;
    private String password;

    public SacombankDriver(Configuration configuration, String account,
                      String password){
        this.bankName = "SacomBank";
        this.account = account;
        this.password = password;
        this.configuration = configuration;
        this.keepProfiles = false;
        createDriver();
        sacombankSelenium = new SacombankSelenium(spiderPhantomJsDriver);

    }



    public boolean isLogged() throws DriverInterruptException{
        return sacombankSelenium.isLogin();
    }

    public void login() throws Exception {
        LOG.info("Start login process ... ");
        sacombankSelenium.visitHomePage();
        sacombankSelenium.login(account, password);
    }

    public List<Transaction> scanTransactions() throws DriverInterruptException{
        return sacombankSelenium.scan();
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
