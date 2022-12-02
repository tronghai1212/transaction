package org.growhack.bank.portal.driver.vietinbank;

import org.growhack.bank.portal.model.Transaction;
import org.growhack.bank.portal.service.CaptchaService;
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
import java.util.Scanner;

public class VietinbankSelenium {

    public static Logger LOG = LoggerFactory.getLogger(VietinbankSelenium.class);

    public static Configuration configuration = YamlConfiguration.YamConfigFactory.load(Configuration.MODE.PRODUCT);

    public static final String HOME_URL = "https://ipay.vietinbank.vn/login";

    public static final String XPATH_LOGIN_USER = ".//*[@name='userName']";
    public static final String XPATH_LOGIN_PASS = ".//*[@name='accessCode']";
    public static final String XPATH_LOGIN_CAPTCHA = ".//*[@name='captchaCode']";
    public static final String XPATH_CAPTCHA_IMAGE = ".//*[@class='login']/div[4]/img";
    public static final String XPATH_LOGIN_BUTTON = ".//*[@class='form-input-captcha']//button";

    public static final String LOGGED_URL = "https://ipay.vietinbank.vn/";

    private int CAPTCHA_X = 910;
    private int CAPTCHA_Y = 480;
    private int CAPTCHA_W = 170;
    private int CAPTCHA_H = 40;

    private SpiderPhantomJsDriver driver;
    private Scanner sc = new Scanner(System.in);

    public VietinbankSelenium(SpiderPhantomJsDriver driver) {
        this.driver = driver;
    }

    public void visitHomePage() throws DriverInterruptException {
        visit(HOME_URL);
    }

    public void visit(String url) throws DriverInterruptException {
        Action.SleepAction sleepAction = new Action.SleepAction();
        sleepAction.setTimeSleep(4000);
        Action.DirectLinkAction visitPage = new Action.DirectLinkAction(url);
        SbyStep sbyStep = new SbyStep();
        sbyStep.addAction(visitPage);
        sbyStep.addAction(sleepAction);
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

        String currentURL = driver.getWebDriver().getCurrentUrl();
        if (currentURL.equals(LOGGED_URL)
                || currentURL.contains("https://ipay.vietinbank.vn/payment-history")) {
            return true;
        }

        return false;

    }

    public void login(String account, String pass) throws Exception {

        Action.SleepAction sleepAction = new Action.SleepAction();
        sleepAction.setTimeSleep(3000);

        Action.MultipleParamSubmitAction loginAction = new Action.MultipleParamSubmitAction();
        loginAction.addParam(new Action.ActionParam(XPATH_LOGIN_USER,
                account, "Account"));
        loginAction.addParam(new Action.ActionParam(XPATH_LOGIN_PASS,
                pass, "Pass"));

        WebElement webElement = driver.getWebDriver().findElement(By.xpath(XPATH_CAPTCHA_IMAGE));

        String captchaValue = CaptchaService.detect(driver,
                webElement.getLocation().getX(),
                webElement.getLocation().getY(),
                webElement.getSize().getWidth(),
                webElement.getSize().getHeight());

        if (captchaValue != null) {
            LOG.info("Captcha value is : " + captchaValue);
        } else {
            LOG.info("Can't detected captcha, please refresh and try again");
        }
        loginAction.addParam(new Action.ActionParam(XPATH_LOGIN_CAPTCHA,
                captchaValue, "Captcha"));
        loginAction.setClickXpath(XPATH_LOGIN_BUTTON);
        SbyStep sbyStep = new SbyStep();
        sbyStep.addAction(loginAction);
        getExecutor().run(driver, sbyStep);
    }

    public void logout() throws DriverInterruptException {
        Action.SleepAction sleepAction = new Action.SleepAction();
        sleepAction.setTimeSleep(3000);

        Action.DirectLinkAction directLinkAction = new Action.DirectLinkAction(LOGGED_URL);
        SbyStep sbyStep = new SbyStep();
        Action.ClickAction clickAction = new Action.ClickAction();
        clickAction.setXPathNext(".//*[@class='main-header__actions']//img[@class='ml-8 pointer btn-logout']");
        Action.ClickAction clickAction1 = new Action.ClickAction();
        clickAction1.setXPathNext(".//*[@class='app-btn b-medium b-danger']");
        sbyStep.addAction(directLinkAction);
        sbyStep.addAction(sleepAction);
        sbyStep.addAction(clickAction);
        sbyStep.addAction(clickAction1);
        getExecutor().run(driver, sbyStep);
        LOG.info("logout", "Current URL " + driver.getWebDriver().getCurrentUrl());
    }

    public List<Transaction> scan() throws DriverInterruptException {

        //For each month get data
        Action.SleepAction sleepAction = new Action.SleepAction();
        sleepAction.setTimeSleep(3000);

        Action.ClickAction clickAction = new Action.ClickAction();
        clickAction.setXPathNext(".//*[@class='all-information']//div[@class='card-name oneline-text label-2']");
        Action.ClickAction clickAction1 = new Action.ClickAction();
        clickAction1.setXPathNext(".//*[@class='Dropdown-control dropdown-control']");
        Action.ClickAction clickAction2 = new Action.ClickAction();
        clickAction2.setXPathNext(".//*[@class='Dropdown-menu dropdown-menu']/div[3]");
        SbyStep sb = new SbyStep();
        sb.addAction(clickAction);
        sb.addAction(sleepAction);
        sb.addAction(clickAction1);
        sb.addAction(sleepAction);
        sb.addAction(clickAction2);
        LOG.info("The data is " + getExecutor().run(driver, sb));

        List<Transaction> rs = getTransactionOfMonth();

        return rs;
    }

    public List<Transaction> getTransactionOfMonth() {

        List<WebElement> elements = driver.getWebDriver().findElements(By.xpath(".//*[@class='cms-main-zone payment-history__table']/table/tbody//tr"));
        LOG.info("Number elements : " + elements.size());
        List<Transaction> transactions = new ArrayList<>();
        for (int i = elements.size() - 1; i > -1; i--) {
            Action.ClickAction clickAction = new Action.ClickAction();
            clickAction.setXPathNext(".//*[@class='cms-main-zone payment-history__table']/table/tbody/tr["+(i+1)+"]");
            Action.ClickAction clickAction1 = new Action.ClickAction();
            clickAction1.setXPathNext(".//*[@class='payment-footer__left']");
            Action.QueryAction queryAction = new Action.QueryAction();
            //col-dateTime
            String xpathDate = ".//*[@class='cms-main-zone payment-history__table']/table/tbody/tr["+(i+1)+"]/td[1]";
            String xpathMess = ".//*[@class='cms-main-zone payment-history__table']/table/tbody/tr["+(i+1)+"]/td[2]";
            String xpathMoney = ".//*[@class='cms-main-zone payment-history__table']/table/tbody/tr["+(i+1)+"]/td[3]";
            String xpathTransId = ".//*[@class='payment-header__transaction']/div[2]/span";
//            String xpathSenderBank = ".//*[@class='payment-layout__body']/div[1]/div[4]";
//            String xpathSenderAccount = ".//*[@class='payment-layout__body']/div[1]/div[1]";
//            String xpathAccountReceived = ".//*[@class='payment-layout__body']/div[3]/div[2]";
            String xpathBalance = ".//*[@class='cms-main-zone payment-history__table']/table/tbody/tr["+(i+1)+"]/td[4]";

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

//            Action.SelectParam senderBank = new Action.SelectParam();
//            senderBank.setName("bank");
//            senderBank.setXpath(xpathSenderBank);
//            queryAction.addParam(senderBank);
//
//            Action.SelectParam senderAccount = new Action.SelectParam();
//            senderAccount.setName("acc");
//            senderAccount.setXpath(xpathSenderAccount);
//            queryAction.addParam(senderAccount);
//
//            Action.SelectParam receivedAccount = new Action.SelectParam();
//            receivedAccount.setName("name");
//            receivedAccount.setXpath(xpathAccountReceived);
//            queryAction.addParam(receivedAccount);

            Action.SelectParam balanceParam = new Action.SelectParam();
            balanceParam.setName("balance");
            balanceParam.setXpath(xpathBalance);
            queryAction.addParam(balanceParam);

            SbyStep sbyStep = new SbyStep();
            sbyStep.addAction(clickAction);
            sbyStep.addAction(queryAction);
            sbyStep.addAction(clickAction1);

            try {
                JsonObject rs = getExecutor().run(driver, sbyStep);
                Transaction transaction = new Transaction(rs);
                transaction.setTransId(String
                        .valueOf(transaction.toString().hashCode()));
                System.out.println(transaction);
                transactions.add(transaction);
            } catch (DriverInterruptException e) {
                LOG.warn("Execute action error", e);
            }
        }

        return transactions;
    }

    public SpiderPhantomExecutor getExecutor() {
        SpiderPhantomExecutor spiderPhantomExecutor = new SpiderPhantomExecutor(configuration);
        return spiderPhantomExecutor;
    }
}
