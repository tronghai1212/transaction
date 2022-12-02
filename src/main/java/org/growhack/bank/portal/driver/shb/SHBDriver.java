package org.growhack.bank.portal.driver.shb;

import org.growhack.bank.portal.driver.BankDriver;
import org.growhack.bank.portal.driver.seabank.SeabankSelenium;
import org.growhack.bank.portal.model.Transaction;
import com.lvtech.rd.common.config.Configuration;
import org.poc.spider.phantom.core.SpiderPhantomJsDriver;
import org.poc.spider.phantom.exception.DriverInterruptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SHBDriver extends BankDriver {

    public static Logger LOG = LoggerFactory.getLogger(SHBDriver.class);

    private SHBSelenium shbSelenium;

    private String account;
    private String password;

    public SHBDriver(Configuration configuration, String account,
                     String password){
        this.bankName = "ShbBank";
        this.account = account;
        this.password = password;
        this.configuration = configuration;
        createDriver();
        shbSelenium = new SHBSelenium(spiderPhantomJsDriver);

    }

    @Override
    public void createDriver() {
        super.createDriver();
        shbSelenium = new SHBSelenium(spiderPhantomJsDriver);
    }



    public boolean isLogged() throws DriverInterruptException{
        return shbSelenium.isLogin();
    }

    public void login() throws DriverInterruptException {
        LOG.info("Start login process ... ");
        shbSelenium.visitHomePage();
        shbSelenium.login(account, password);
    }

    public List<Transaction> scanTransactions() throws DriverInterruptException{
        return shbSelenium.scan();
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
