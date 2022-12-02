package org.growhack.bank.portal.driver.shb;

import org.growhack.bank.portal.model.Transaction;
import com.google.gson.JsonObject;
import com.lvtech.rd.common.config.Configuration;
import com.lvtech.rd.common.config.YamlConfiguration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.poc.spider.phantom.commons.Action;
import org.poc.spider.phantom.commons.SbyStep;
import org.poc.spider.phantom.core.SpiderPhantomExecutor;
import org.poc.spider.phantom.core.SpiderPhantomJsDriver;
import org.poc.spider.phantom.exception.DriverInterruptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SHBSelenium {

    public static Logger LOG = LoggerFactory.getLogger(SHBSelenium.class);

    public static Configuration configuration = YamlConfiguration.YamConfigFactory.load(Configuration.MODE.PRODUCT);

    public static final String HOME_URL = "https://ibanking.shb.com.vn/account/login";

    public static final String XPATH_LOGIN_USER = ".//*[@id='UserName']";

    public static final String XPATH_LOGIN_PASS = ".//*[@id='Password']";
    public static final String XPATH_LOGIN_BUTTON = ".//*[@id='btnlogin']";

    public static final String LOGGED_URL = "https://ibanking.shb.com.vn/";

    private SpiderPhantomJsDriver driver;

    public SHBSelenium(SpiderPhantomJsDriver driver) {
        this.driver = driver;
    }

    public void visitHomePage() throws DriverInterruptException {
        visit(HOME_URL);
    }

    public void visit(String url) throws DriverInterruptException {
        Action.DirectLinkAction visitPage = new Action.DirectLinkAction(url);
        SbyStep sbyStep = new SbyStep();
        sbyStep.addAction(visitPage);
        getExecutor().run(driver, sbyStep);
    }

    public boolean isExistElement(String xpath) {

        try {
            WebElement webElement = driver.getWebDriver().findElement(By.xpath(xpath));
            if (webElement != null) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }

        return false;

    }

    public boolean isLogin() throws DriverInterruptException {
        String xpath = "//div[@class='profilebox-custname']";
        if (isExistElement(xpath) == true) {
            return true;
        }

        return false;

    }


    public void login(String account, String pass) throws DriverInterruptException {
        Action.MultipleParamSubmitAction loginAction = new Action.MultipleParamSubmitAction();
        loginAction.addParam(new Action.ActionParam(XPATH_LOGIN_USER,
                account, "Account"));
        loginAction.addParam(new Action.ActionParam(XPATH_LOGIN_PASS,
                pass, "Pass"));
        loginAction.setClickXpath(XPATH_LOGIN_BUTTON);
        SbyStep sbyStep = new SbyStep();
        sbyStep.addAction(loginAction);
        getExecutor().run(driver, sbyStep);
    }

    public void logout() throws DriverInterruptException {
        Action.DirectLinkAction directLinkAction = new Action.DirectLinkAction(LOGGED_URL);
        SbyStep sbyStep = new SbyStep();
        sbyStep.addAction(directLinkAction);
        getExecutor().run(driver, sbyStep);
        Action.SleepAction sleepAction = new Action.SleepAction();
        sleepAction.setTimeSleep(3000);
        sbyStep.addAction(sleepAction);
        Action.ClickAction clickAction = new Action.ClickAction();
        clickAction.setXPathNext(".//a[@class='logout-menu']");
        sbyStep.addAction(clickAction);
        sbyStep.addAction(sleepAction);
        Action.ClickAction clickAction1 = new Action.ClickAction();
        clickAction1.setXPathNext(".//*[@class='modal fade in']//input[@class='shb-nextbtn']");
        sbyStep.addAction(clickAction1);
        LOG.info("logout", "Current URL " + driver.getWebDriver().getCurrentUrl());
    }

    public List<Transaction> scan() throws DriverInterruptException {

        Action.SleepAction sleepAction = new Action.SleepAction();
        sleepAction.setTimeSleep(4000);

        Action.ClickAction clickAction = new Action.ClickAction();
        clickAction.setXPathNext("//*[@class='row-block row row-acc']/div");
        Action.ClickAction clickAction1 = new Action.ClickAction();
        clickAction1.setXPathNext("//*[@class='row row-account-command']/div[4]/a[@class='casa-detail-post']");
        Action.ClickAction clickAction2 = new Action.ClickAction();
        clickAction2.setXPathNext("//*[@id='current-month']");

        SbyStep sb = new SbyStep();
        sb.addAction(sleepAction);
        sb.addAction(clickAction);
        sb.addAction(sleepAction);
        sb.addAction(clickAction1);
        sb.addAction(sleepAction);
        sb.addAction(clickAction2);
        sb.addAction(sleepAction);
        LOG.info("The data is " + getExecutor().run(driver, sb));

        List<Transaction> rs = getTransactionOfMonth();

        return rs;
    }

    public List<Transaction> getTransactionOfMonth() throws DriverInterruptException {

        List<WebElement> elements = driver.getWebDriver().findElements(By.xpath("//table[@class='table table-striped grid-table']/tbody//tr"));
        LOG.info("Number elements : " + elements.size());
        List<Transaction> transactions = new ArrayList<>();
        for (int i = 0; i < elements.size(); i++) {
            Action.QueryAction queryAction = new Action.QueryAction();
            //col-dateTime
            String xpathDate = "//table[@class='table table-striped grid-table']/tbody/tr[" + (i + 1) + "]/*[@class='grid-cell transactionDateCol']";
            String xpathMess = "//table[@class='table table-striped grid-table']/tbody/tr[" + (i + 1) + "]/*[@class='grid-cell descriptionCol text-description']";
            String xpathMoney = "//table[@class='table table-striped grid-table']/tbody/tr[" + (i + 1) + "]/*[@class='grid-cell amountCol']";
            String xpathTransId = "//table[@class='table table-striped grid-table']/tbody/tr[" + (i + 1) + "]/*[@class='grid-cell transactionCd']/a";

            Action.SelectParam dateParam = new Action.SelectParam();
            dateParam.setName("date");
            dateParam.setXpath(xpathDate);
            queryAction.addParam(dateParam);

            Action.SelectParam messParam = new Action.SelectParam();
            messParam.setName("mess");
            messParam.setXpath(xpathMess);
            queryAction.addParam(messParam);

            Action.SelectParam moneyParam = new Action.SelectParam();
            moneyParam.setName("money");
            moneyParam.setXpath(xpathMoney);
            queryAction.addParam(moneyParam);

            Action.SelectParam transParam = new Action.SelectParam();
            transParam.setName("trans_id");
            transParam.setXpath(xpathTransId);
            queryAction.addParam(transParam);

            SbyStep sbyStep = new SbyStep();
            sbyStep.addAction(queryAction);

            JsonObject rs = getExecutor().run(driver, sbyStep);
            Transaction transaction = new Transaction(rs);
            System.out.println(transaction);
            transactions.add(transaction);
        }

        return transactions;
    }

    public SpiderPhantomExecutor getExecutor() {
        SpiderPhantomExecutor spiderPhantomExecutor = new SpiderPhantomExecutor(configuration);
        return spiderPhantomExecutor;
    }
}
