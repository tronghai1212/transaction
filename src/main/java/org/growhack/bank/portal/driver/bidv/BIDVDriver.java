package org.growhack.bank.portal.driver.bidv;

import org.growhack.bank.portal.driver.BankDriver;
import org.growhack.bank.portal.driver.acb.AcbSelenium;
import org.growhack.bank.portal.model.Transaction;
import com.lvtech.rd.common.config.Configuration;
import org.poc.spider.phantom.core.SpiderPhantomJsDriver;
import org.poc.spider.phantom.exception.DriverInterruptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class BIDVDriver extends BankDriver {

    public static Logger LOG = LoggerFactory.getLogger(BIDVDriver.class);

    private BIDVSelenium bidvSelenium;

    private String account;
    private String password;

    public BIDVDriver(Configuration configuration, String account,
                      String password) {
        this.bankName = "Bidv";
        this.account = account;
        this.password = password;
        this.configuration = configuration;
        createDriver();
        bidvSelenium = new BIDVSelenium(spiderPhantomJsDriver);

    }

    @Override
    public void createDriver() {
        super.createDriver();
        bidvSelenium = new BIDVSelenium(spiderPhantomJsDriver);
    }

    public boolean isLogged() throws DriverInterruptException {
        return bidvSelenium.isLogin();
    }

    public void login() throws Exception {
        LOG.info("Start login process ... ");
//        loadCookie();
        bidvSelenium.visitHomePage();
        bidvSelenium.login(account, password);
//        try {
//            saveCookie();
//        } catch (IOException e) {
//            LOG.warn("Save cookie error", e);
//        }
    }

    public List<Transaction> scanTransactions() throws DriverInterruptException {
        return bidvSelenium.scan();
    }


//    public void saveCookie() throws IOException {
//        Set<Cookie> cookies = spiderPhantomJsDriver.getWebDriver().manage().getCookies();
//        JsonArray jsonArray = new JsonArray();
//        Gson gson = new Gson();
//        for (Cookie c : cookies) {
//            jsonArray.add(gson.toJsonTree(c));
//        }
//        FileUtils.write(new File("cookie/BIDV.json"), jsonArray.getAsString(), "utf-8");
//    }
//
//    public void loadCookie() {
//        try {
//            JsonParser jsonParser = new JsonParser();
//            Gson gson = new Gson();
//            String content = FileUtils.readFileToString(new File("cookie/BIDV.json"), "utf-8");
//            JsonArray jsonArray = jsonParser.parse(content).getAsJsonArray();
//            for (JsonElement e : jsonArray) {
//                Cookie c = gson.fromJson(e, Cookie.class);
//                LOG.info("Add cookie " + c);
//                spiderPhantomJsDriver.getWebDriver().manage().addCookie(c);
//            }
//        } catch (Exception e) {
//            LOG.info("Can't load cookie profile");
//        }
//    }


    public SpiderPhantomJsDriver getSpiderPhantomJsDriver() {
        return spiderPhantomJsDriver;
    }

    public void setSpiderPhantomJsDriver(SpiderPhantomJsDriver spiderPhantomJsDriver) {
        this.spiderPhantomJsDriver = spiderPhantomJsDriver;
    }

    public void logout() throws DriverInterruptException {

    }

}
