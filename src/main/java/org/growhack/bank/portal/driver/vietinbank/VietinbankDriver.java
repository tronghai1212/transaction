package org.growhack.bank.portal.driver.vietinbank;

import org.growhack.bank.portal.driver.BankDriver;
import org.growhack.bank.portal.driver.vietcombank.VietcombankSelenium;
import org.growhack.bank.portal.model.Transaction;
import com.lvtech.rd.common.config.Configuration;
import org.poc.spider.phantom.core.SpiderPhantomJsDriver;
import org.poc.spider.phantom.exception.DriverInterruptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class VietinbankDriver extends BankDriver {
    public static Logger LOG = LoggerFactory.getLogger(VietinbankDriver.class);

    private VietinbankSelenium vietinbankSelenium;

    private String account;
    private String password;

    public VietinbankDriver(Configuration configuration, String account,
                     String password){
        this.bankName = "ViettinBank";
        this.account = account;
        this.password = password;
        this.configuration = configuration;
        createDriver();
        vietinbankSelenium = new VietinbankSelenium(spiderPhantomJsDriver);

    }

    @Override
    public void createDriver() {
        super.createDriver();
        vietinbankSelenium = new VietinbankSelenium(spiderPhantomJsDriver);
    }

    public boolean isLogged() throws DriverInterruptException{
        return vietinbankSelenium.isLogin();
    }

    public void login() throws Exception {
        LOG.info("Start login process ... ");
        vietinbankSelenium.visitHomePage();
        vietinbankSelenium.login(account, password);
    }

    public List<Transaction> scanTransactions() throws DriverInterruptException{
        return vietinbankSelenium.scan();
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
