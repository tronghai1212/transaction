package org.growhack.bank.portal.driver.vib;

import org.growhack.bank.portal.driver.BankDriver;
import org.growhack.bank.portal.driver.techcombank.TechcombankSelenium;
import org.growhack.bank.portal.model.Transaction;
import com.lvtech.rd.common.config.Configuration;
import org.poc.spider.phantom.core.SpiderPhantomJsDriver;
import org.poc.spider.phantom.exception.DriverInterruptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class VIBDriver extends BankDriver {

    public static Logger LOG = LoggerFactory.getLogger(VIBDriver.class);

    private VIBSelenium vibSelenium;

    private String account;
    private String password;

    public VIBDriver(Configuration configuration, String account,
                     String password){
        this.bankName = "VibBank";
        this.account = account;
        this.password = password;
        this.configuration = configuration;
        createDriver();
        vibSelenium = new VIBSelenium(spiderPhantomJsDriver);

    }

    @Override
    public void createDriver() {
        super.createDriver();
        vibSelenium = new VIBSelenium(spiderPhantomJsDriver);
    }

    public boolean isLogged() throws DriverInterruptException{
        return vibSelenium.isLogin();
    }

    public void login() throws DriverInterruptException {
        LOG.info("Start login process ... ");
        vibSelenium.visitHomePage();
        vibSelenium.login(account, password);
    }

    public List<Transaction> scanTransactions() throws DriverInterruptException{
        return vibSelenium.scan();
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
