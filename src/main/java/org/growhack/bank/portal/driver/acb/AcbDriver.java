package org.growhack.bank.portal.driver.acb;

import org.growhack.bank.portal.driver.BankDriver;
import org.growhack.bank.portal.model.Transaction;
import com.lvtech.rd.common.config.Configuration;
import org.poc.spider.phantom.core.SpiderPhantomJsDriver;
import org.poc.spider.phantom.exception.DriverInterruptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AcbDriver extends BankDriver {
    public static Logger LOG = LoggerFactory.getLogger(AcbDriver.class);

    private AcbSelenium acbSelenium;

    private String account;
    private String password;

    public AcbDriver(Configuration configuration, String account,
                          String password){

        this.bankName = "ACB";
        this.account = account;
        this.password = password;
        this.configuration = configuration;
        createDriver();
    }

    @Override
    public void createDriver() {
        super.createDriver();
        acbSelenium = new AcbSelenium(spiderPhantomJsDriver);
    }

    public boolean isLogged() throws DriverInterruptException{
        return acbSelenium.isLogin();
    }

    public void login() throws Exception {
        LOG.info("Start login process ... ");
        acbSelenium.visitHomePage();
        acbSelenium.login(account, password);
    }

    public List<Transaction> scanTransactions() throws DriverInterruptException{
        return acbSelenium.scan();
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
