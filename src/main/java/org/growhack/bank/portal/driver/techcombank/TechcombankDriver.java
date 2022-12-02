package org.growhack.bank.portal.driver.techcombank;

import org.growhack.bank.portal.driver.BankDriver;
import org.growhack.bank.portal.driver.shb.SHBSelenium;
import org.growhack.bank.portal.model.Transaction;
import com.lvtech.rd.common.config.Configuration;
import org.poc.spider.phantom.core.SpiderPhantomJsDriver;
import org.poc.spider.phantom.exception.DriverInterruptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TechcombankDriver extends BankDriver {
    public static Logger LOG = LoggerFactory.getLogger(TechcombankDriver.class);

    private TechcombankSelenium techcombankSelenium;

    private String account;
    private String password;

    public TechcombankDriver(Configuration configuration, String account,
                             String password){
        this.bankName = "TechcomBank";
        this.account = account;
        this.password = password;
        this.configuration = configuration;
        this.keepProfiles = false;
        createDriver();
        techcombankSelenium = new TechcombankSelenium(spiderPhantomJsDriver);

    }

    @Override
    public void createDriver() {
        super.createDriver();
        techcombankSelenium = new TechcombankSelenium(spiderPhantomJsDriver);
    }

    public boolean isLogged() throws DriverInterruptException{
        return techcombankSelenium.isLogin();
    }

    public void login() throws DriverInterruptException {
        LOG.info("Start login process ... ");
        techcombankSelenium.visitHomePage();
        techcombankSelenium.login(account, password);
    }

    public List<Transaction> scanTransactions() throws DriverInterruptException{
        return techcombankSelenium.scan();
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
