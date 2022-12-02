package org.growhack.bank.portal.driver.vietcombank;

import org.growhack.bank.portal.driver.BankDriver;
import org.growhack.bank.portal.driver.vib.VIBSelenium;
import org.growhack.bank.portal.model.Transaction;
import com.lvtech.rd.common.config.Configuration;
import org.poc.spider.phantom.core.SpiderPhantomJsDriver;
import org.poc.spider.phantom.exception.DriverInterruptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class VietcombankDriver extends BankDriver {

    public static Logger LOG = LoggerFactory.getLogger(VietcombankDriver.class);

    private VietcombankSelenium VietcombankSelenium;

    private String account;
    private String password;

    public VietcombankDriver(Configuration configuration, String account,
                             String password) {
        this.account = account;
        this.password = password;
        this.bankName = "VietComBank";
        this.configuration = configuration;
        this.keepProfiles = false;
        createDriver();
    }

    @Override
    public void createDriver() {
        super.createDriver();
        VietcombankSelenium = new VietcombankSelenium(spiderPhantomJsDriver);
    }


    public boolean isLogged() throws DriverInterruptException {
        return VietcombankSelenium.isLogin();
    }

    public void login() throws Exception {
        LOG.info("Start login process ... ");
        VietcombankSelenium.visitHomePage();
        VietcombankSelenium.login(account, password);
    }

    public List<Transaction> scanTransactions() throws DriverInterruptException {
        return VietcombankSelenium.scan();
    }


    public SpiderPhantomJsDriver getSpiderPhantomJsDriver() {
        return spiderPhantomJsDriver;
    }

    public void setSpiderPhantomJsDriver(SpiderPhantomJsDriver spiderPhantomJsDriver) {
        this.spiderPhantomJsDriver = spiderPhantomJsDriver;
    }

    public void logout() throws DriverInterruptException {

    }
}
