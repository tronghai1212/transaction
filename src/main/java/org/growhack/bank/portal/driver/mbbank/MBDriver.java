package org.growhack.bank.portal.driver.mbbank;

import org.growhack.bank.portal.driver.BankDriver;
import org.growhack.bank.portal.driver.agribank.AgribankSelenium;
import org.growhack.bank.portal.model.Transaction;
import com.lvtech.rd.common.config.Configuration;
import org.poc.spider.phantom.core.SpiderPhantomJsDriver;
import org.poc.spider.phantom.exception.DriverInterruptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MBDriver extends BankDriver {
    public static Logger LOG = LoggerFactory.getLogger(MBDriver.class);

    private MBSelenium mbSelenium;

    private String account;
    private String password;

    public MBDriver(Configuration configuration, String account,
                     String password){
        this.bankName = "MbBank";
        this.account = account;
        this.password = password;
        this.configuration = configuration;
        createDriver();

    }

    @Override
    public void createDriver() {
        super.createDriver();
        mbSelenium = new MBSelenium(spiderPhantomJsDriver);
    }


    public boolean isLogged() throws DriverInterruptException{
        return mbSelenium.isLogin();
    }

    public void login() throws Exception {
        LOG.info("Start login process ... ");
        mbSelenium.visitHomePage();
        mbSelenium.login(account, password);
    }

    public List<Transaction> scanTransactions() throws DriverInterruptException{
        return mbSelenium.scan();
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
