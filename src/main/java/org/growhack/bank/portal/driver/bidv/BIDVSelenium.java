package org.growhack.bank.portal.driver.bidv;

import org.growhack.bank.portal.model.Transaction;
import org.growhack.bank.portal.service.CaptchaService;
import com.google.gson.JsonObject;
import com.lvtech.rd.common.config.Configuration;
import com.lvtech.rd.common.config.YamlConfiguration;
import org.openqa.selenium.*;
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

public class BIDVSelenium {

    public static Logger LOG = LoggerFactory.getLogger(BIDVSelenium.class);

    public static Configuration configuration = YamlConfiguration.YamConfigFactory.load(Configuration.MODE.PRODUCT);

    public static final String HOME_URL = "https://smartbanking.bidv.com.vn/dang-nhap";
    public static final String TRANSACTION_URL = "https://smartbanking.bidv.com.vn/tai-khoan";
    public static final String SUBMIT_OTP_URL = "https://smartbanking.bidv.com.vn/dang-nhap/xac-thuc-chuyen-doi-otp";

    public static final String LOGOUT_URL = "https://smartbanking.bidv.com.vn/";
    public static final String XPATH_LOGIN_USER = ".//*[@formcontrolname='soDienThoai']";

    public static final String XPATH_LOGIN_PASS = ".//*[@id='app_password_matKhau']";
    public static final String XPATH_LOGIN_CAPTCHA = ".//app-captcha//input";
    public static final String XPATH_CAPTCHA_IMAGE = ".//*[@class='login-captcha']/img";
    public static final String XPATH_LOGIN_BUTTON = ".//*[@class='ubtn ubg-gradient-primary ubtn-md ripple width-full no-m']";

    public static final String LOGGED_URL = "https://smartbanking.bidv.com.vn/";

    private int CAPTCHA_X = 420;
    private int CAPTCHA_Y = 530;
    private int CAPTCHA_W = 130;
    private int CAPTCHA_H = 50;

    private SpiderPhantomJsDriver driver;
    private Scanner sc = new Scanner(System.in);

    public BIDVSelenium(SpiderPhantomJsDriver driver) {
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

    public boolean isLogin() throws DriverInterruptException {

        Action.DirectLinkAction directLinkAction = new Action.DirectLinkAction(LOGGED_URL);
        SbyStep sbyStep = new SbyStep();
        sbyStep.addAction(directLinkAction);
        getExecutor().run(driver, sbyStep);
        String currentURL = driver.getWebDriver().getCurrentUrl();
        if (currentURL.equals(LOGGED_URL)) {
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

        String captchaValue = CaptchaService.detect(driver,
                CAPTCHA_X,
                CAPTCHA_Y,
                CAPTCHA_W,
                CAPTCHA_H);

        if (captchaValue != null){
            LOG.info("Captcha value is : " + captchaValue);
        } else {
            LOG.info("Can't detected captcha, please refresh and try again");
        }

        loginAction.addParam(new Action.ActionParam(XPATH_LOGIN_CAPTCHA,
                captchaValue, "Captcha"));
        loginAction.setClickXpath(XPATH_LOGIN_BUTTON);

        SbyStep sbyStep = new SbyStep();
        sbyStep.addAction(loginAction);
        sbyStep.addAction(sleepAction);
        getExecutor().run(driver, sbyStep);

        String otp = sc.nextLine();

        Action.MultipleParamSubmitAction submitAction = new Action.MultipleParamSubmitAction();
        submitAction.addParam(new Action.ActionParam(".//*[@formcontrolname='otp']", otp, "OTP"));

        submitAction.setClickXpath(".//*[@class='list-mv15']/div[2]/a");

        SbyStep sbyStep1 = new SbyStep();
        sbyStep1.addAction(submitAction);
        getExecutor().run(driver, sbyStep1);
    }

    public void logout() throws DriverInterruptException {
        Action.DirectLinkAction directLinkAction = new Action.DirectLinkAction(LOGOUT_URL);
        Action.ClickAction clickAction = new Action.ClickAction();
        clickAction.setXPathNext(".//ng-component[@class='component-host-scrollable']//button[@class='ubtn ubg-light-blue ubtn-md ripple width-full']");
        Action.ClickAction clickAction1 = new Action.ClickAction();
        clickAction1.setXPathNext(".//*[class='dropdown-menu']/li[3]");
        SbyStep sbyStep = new SbyStep();
        sbyStep.addAction(directLinkAction);
        sbyStep.addAction(clickAction);
        sbyStep.addAction(clickAction1);
        getExecutor().run(driver, sbyStep);
        LOG.info("logout", "Current URL " + driver.getWebDriver().getCurrentUrl());
    }

    public List<Transaction> scan() throws DriverInterruptException {

        visit(TRANSACTION_URL);

        Action.SleepAction sleepAction = new Action.SleepAction();
        sleepAction.setTimeSleep(4000);

        Action.ClickAction clickAction = new Action.ClickAction();
        clickAction.setXPathNext("//div[@class='accordion-arrow__content ubg-grey-4']/div/ul[@class='list-tk']/li/div/a");
        Action.ClickAction clickAction1 = new Action.ClickAction();
        clickAction1.setXPathNext("//*[@class='tab-btn-group']//button[@class='ubtn ubg-light-blue ubtn-md ripple width-full']");
        SbyStep sb = new SbyStep();
        sb.addAction(sleepAction);
        sb.addAction(clickAction);
        sb.addAction(sleepAction);
        sb.addAction(clickAction1);
        sb.addAction(sleepAction);
        LOG.info("The data is " + getExecutor().run(driver, sb));

        List<Transaction> rs = getTransactionOfMonth();

        return rs;
    }

    public List<Transaction> getTransactionOfMonth() throws DriverInterruptException {

        List<WebElement> elements = driver.getWebDriver().findElements(By.xpath("//div[@id='tab1']/div[@class='list list-line list-his']//div[@class='list-line-item hightlight-fadeout']"));
        LOG.info("Number elements : " + elements.size());
        List<Transaction> transactions = new ArrayList<>();
        for (int i = elements.size() -1; i > -1; i --) {
            Action.QueryAction queryAction = new Action.QueryAction();
            //col-dateTime
            String xpathDate = "//div[@id='tab1']/div[@class='list list-line list-his']/div["+(i+1)+"]/div/div/div";
            String xpathMess = "//div[@id='tab1']/div[@class='list list-line list-his']/div["+(i+1)+"]/div/div/div[2]";
            String xpathMoney = "//div[@id='tab1']/div[@class='list list-line list-his']/div["+(i+1)+"]/div/div[2]/div[2]";
            String xpathTransId = "//div[@id='tab1']/div[@class='list list-line list-his']/div["+(i+1)+"]/div/div[2]/div";

            //queryAction.addParam(param);
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

            try {
                JsonObject rs = getExecutor().run(driver, sbyStep);
                Transaction transaction = new Transaction(rs);
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
