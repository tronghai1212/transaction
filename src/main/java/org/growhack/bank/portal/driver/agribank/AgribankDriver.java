package org.growhack.bank.portal.driver.agribank;

import org.growhack.bank.portal.driver.BankDriver;
import org.growhack.bank.portal.model.Transaction;
import com.lvtech.rd.common.config.Configuration;
import org.poc.spider.phantom.core.SpiderPhantomJsDriver;
import org.poc.spider.phantom.exception.DriverInterruptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AgribankDriver extends BankDriver {
    public static Logger LOG = LoggerFactory.getLogger(AgribankDriver.class);

    private AgribankSelenium agribankSelenium;

    private String account;
    private String password;

    public AgribankDriver(Configuration configuration, String account,
                            String password){

        this.bankName = "AgriBank";
        this.account = account;
        this.password = password;
        this.configuration = configuration;
        this.keepProfiles = false;
        createDriver();
    }

    @Override
    public void createDriver() {
        super.createDriver();
        agribankSelenium = new AgribankSelenium(spiderPhantomJsDriver);
    }

    public boolean isLogged() throws DriverInterruptException{
        return agribankSelenium.isLogin();
    }

    public void login() throws Exception {
        LOG.info("Start login process ... ");
        agribankSelenium.visitHomePage();
        agribankSelenium.login(account, password);
    }

    public List<Transaction> scanTransactions() throws DriverInterruptException{
        return agribankSelenium.scan();
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
