package org.growhack.bank.portal.driver.vietcombank;

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

public class VietcombankSelenium {

    public static Logger LOG = LoggerFactory.getLogger(VietcombankSelenium.class);

    public static Configuration configuration = YamlConfiguration.YamConfigFactory.load(Configuration.MODE.PRODUCT);

    public static final String HOME_URL = "https://vcbdigibank.vietcombank.com.vn/login";

    public static final String XPATH_LOGIN_USER = ".//*[@id='username']";
    public static final String XPATH_LOGIN_CAPTCHA = ".//*[@formcontrolname='captcha']";
    public static final String XPATH_LOGIN_PASS = ".//*[@id='app_password_login']";
    public static final String XPATH_LOGIN_BUTTON = ".//*[@id='btnLogin']";

    public static final String LOGGED_URL = "https://vcbdigibank.vietcombank.com.vn/";

    private int CAPTCHA_W = 100;
    private int CAPTCHA_H = 40;

    private SpiderPhantomJsDriver driver;

    public VietcombankSelenium(SpiderPhantomJsDriver driver) {
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

        String currentURL = driver.getWebDriver().getCurrentUrl();
        driver.getWebDriver().manage().window().maximize();
        System.out.println("Current url : " + currentURL);
        if (currentURL.equals(LOGGED_URL)
                || currentURL.contains("https://vcbdigibank.vietcombank.com.vn/thongtintaikhoan/chitiettaikhoan")) {
            driver.getWebDriver().get(currentURL);
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

        WebElement webElement = driver.getWebDriver().findElement(By.xpath(".//div[@class='input-group-slot no-border captcha']"));

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
        Action.ClickAction clickAction = new Action.ClickAction();
        clickAction.setXPathNext(".//*[@class='menu__mobile']/div[3]/a");
        SbyStep sbyStep = new SbyStep();
        sbyStep.addAction(directLinkAction);
        sbyStep.addAction(sleepAction);
        sbyStep.addAction(clickAction);
        getExecutor().run(driver, sbyStep);
        LOG.info("logout", "Current URL " + driver.getWebDriver().getCurrentUrl());
    }

    public List<Transaction> scan() throws DriverInterruptException {


        Action.SleepAction sleepAction = new Action.SleepAction();
        sleepAction.setTimeSleep(3000);
        Action.ClickAction clickAction = new Action.ClickAction();
        clickAction.setXPathNext(".//div[@class='list-link-sm']//div[@class='tk-inner color-white txt no-pl']/div[4]");

        Action.ClickAction clickAction2 = new Action.ClickAction();
        clickAction2.setXPathNext(".//div[@class='account-list']/div[@class='account-list-item']//div[@class='col-auto']/a");

        Action.ClickAction clickAction3 = new Action.ClickAction();
        clickAction3.setXPathNext(".//*[@class='form-main-footer']/div/div[2]");

        SbyStep sb = new SbyStep();

        try {
            sb.addAction(sleepAction);
            sb.addAction(clickAction);
            getExecutor().run(driver, sb);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            sb = new SbyStep();
            sb.addAction(sleepAction);
            sb.addAction(clickAction2);
            getExecutor().run(driver, sb);
        } catch (Exception e) {
            e.printStackTrace();
        }

        sb = new SbyStep();
        sb.addAction(sleepAction);
        sb.addAction(clickAction3);
        sb.addAction(sleepAction);
        LOG.info("The data is " + getExecutor().run(driver, sb));

        List<Transaction> rs = getTransactionOfMonth();

        return rs;
    }

    public List<Transaction> getTransactionOfMonth() {

        List<WebElement> elements = driver.getWebDriver().findElements(By.xpath(".//*[@id='toanbo']/div[1]/div"));
        LOG.info("Number elements : " + elements.size());
        List<Transaction> transactions = new ArrayList<>();
        for (int i = elements.size() - 1; i > -1; i--) {
            try {//col-dateTime
                String date = elements.get(i).findElement(By.xpath(".//div[1]/div[1]/div[1]")).getText();
                String mess = elements.get(i).findElement(By.xpath(".//div[1]/div[1]/div[2]")).getText();
                String transId = elements.get(i).findElement(By.xpath(".//div[1]/div[2]/div[1]")).getText();
                String money = elements.get(i).findElement(By.xpath(".//div[1]/div[2]/div[2]")).getText();
                Transaction transaction = new Transaction();
                transaction.setCreatedTime(date);
                transaction.setMessages(mess);
                transaction.setAmount(money);
                transaction.setTransId(transId);
                System.out.println(transaction);
                transactions.add(transaction);
            } catch (Exception e) {
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
