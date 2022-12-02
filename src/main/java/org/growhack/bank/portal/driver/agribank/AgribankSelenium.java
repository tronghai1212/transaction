package org.growhack.bank.portal.driver.agribank;

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

public class AgribankSelenium {

    public static Logger LOG = LoggerFactory.getLogger(AgribankSelenium.class);

    public static Configuration configuration = YamlConfiguration.YamConfigFactory.load(Configuration.MODE.PRODUCT);

    public static final String HOME_URL = "https://ibank.agribank.com.vn/ibank/index.jsp";

    public static final String XPATH_LOGIN_USER = ".//*[@name='userName']";
    public static final String XPATH_LOGIN_PASS = ".//*[@name='userPass']";
    public static final String XPATH_LOGIN_CAPTCHA = ".//*[@class='inpCaptcha']";
    public static final String XPATH_LOGIN_BUTTON = ".//*[@id='doOK']";

    public static final String LOGGED_URL = "https://ibank.agribank.com.vn/ibank/";

    private int CAPTCHA_X = 600;
    private int CAPTCHA_Y = 430;
    private int CAPTCHA_W = 80;
    private int CAPTCHA_H = 30;

    private SpiderPhantomJsDriver driver;

    public AgribankSelenium(SpiderPhantomJsDriver driver) {
        this.driver = driver;
    }

    public void visitHomePage() throws DriverInterruptException {
        visit(HOME_URL);
    }

    public void visit(String url) throws DriverInterruptException {
        Action.SleepAction sleepAction = new Action.SleepAction();
        sleepAction.setTimeSleep(3000);
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

        Action.DirectLinkAction directLinkAction = new Action.DirectLinkAction(LOGGED_URL);
        SbyStep sbyStep = new SbyStep();
        sbyStep.addAction(directLinkAction);
        getExecutor().run(driver, sbyStep);
        try {
            WebElement webElement = driver.getWebDriver().findElement(By.xpath("//*[@name='userName']"));
            if (!webElement.getAttribute("value").equals("")) {
                return true;
            }
        } catch (Exception ex) {
            LOG.info("Login Agribank failed");
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

        WebElement webElement = driver.getWebDriver().findElement(By.xpath(".//form/div[3]/img"));

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

        Action.DirectLinkAction directLinkAction = new Action.DirectLinkAction(HOME_URL);
        SbyStep sbyStep = new SbyStep();
        Action.ClickAction clickAction = new Action.ClickAction();
        clickAction.setXPathNext(".//*[@id='doQuit']");
        sbyStep.addAction(directLinkAction);
        sbyStep.addAction(clickAction);
        getExecutor().run(driver, sbyStep);
        LOG.info("logout", "Current URL " + driver.getWebDriver().getCurrentUrl());
    }

    public List<Transaction> scan() throws DriverInterruptException {

        //For each month get data
        Action.SleepAction sleepAction = new Action.SleepAction();
        sleepAction.setTimeSleep(3000);
        Action.ClickAction clickAction = new Action.ClickAction();
        clickAction.setXPathNext(".//*[@id='browser']/li[2]/a");
        Action.ClickAction clickAction1 = new Action.ClickAction();
        clickAction1.setXPathNext(".//*[@id='browser']/li[2]/ul/li[2]/a");
        Action.ClickAction clickAction2 = new Action.ClickAction();
        clickAction2.setXPathNext(".//*[@id='browser']/li[2]/ul/li[2]/ul[2]/li/a");
        SbyStep sb = new SbyStep();
        sb.addAction(clickAction);
        sb.addAction(sleepAction);
        sb.addAction(clickAction1);
        sb.addAction(sleepAction);
        sb.addAction(clickAction2);
        getExecutor().run(driver, sb);
        LOG.info("Access to transaction data success");
        List<Transaction> rs = getTransactionOfMonth();

        return rs;
    }

    public List<Transaction> getTransactionOfMonth() {

        List<WebElement> elements = driver.getWebDriver().findElements(By.xpath(".//*[@id='ibTransList']/tbody//tr"));
        LOG.info("Number elements : " + elements.size());
        List<Transaction> transactions = new ArrayList<>();
        for (int i = elements.size() - 1; i > -1; i--) {
            Action.QueryAction queryAction = new Action.QueryAction();
            //col-dateTime
            String xpathDate = ".//*[@id='ibTransList']/tbody/tr[" + (i + 2) + "]/td[2]";
            String xpathMess = ".//*[@id='ibTransList']/tbody/tr[" + (i + 2) + "]/td[7]";
            String xpathMoney = ".//*[@id='ibTransList']/tbody/tr[" + (i + 2) + "]/td[4]";
            String xpathBalance = ".//*[@id='ibTransList']/tbody/tr[" + (i + 2) + "]/td[5]";
            String xpathTypeTransaction = ".//*[@id='ibTransList']/tbody/tr[" + (i + 2) + "]/td[3]";

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

            Action.SelectParam balanceParam = new Action.SelectParam();
            balanceParam.setName("balance");
            balanceParam.setXpath(xpathBalance);
            queryAction.addParam(balanceParam);

            SbyStep sbyStep = new SbyStep();
            sbyStep.addAction(queryAction);

            try {
                JsonObject rs = getExecutor().run(driver, sbyStep);
                Transaction transaction = new Transaction(rs);
                try {
                    transaction.setTransId(transaction.getMessages());
                    String webElementTypeTrans = driver.getWebDriver().findElement(By.xpath(xpathTypeTransaction)).getText();
                    transaction.setAmount(webElementTypeTrans+transaction.getAmount());
                } catch (Exception e) {
                    LOG.warn("Excess blank row in table", e);
                }
                if (transaction.getAmount() != null) {
                    System.out.println(transaction);
                    transactions.add(transaction);
                }
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
