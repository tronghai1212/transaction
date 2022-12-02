package org.growhack.bank.portal.driver.mbbank;

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

public class MBSelenium {

    public static Logger LOG = LoggerFactory.getLogger(MBSelenium.class);

    public static Configuration configuration = YamlConfiguration.YamConfigFactory.load(Configuration.MODE.PRODUCT);

    public static final String HOME_URL = "https://online.mbbank.com.vn/pl/login";
    public static final String TRANSACTION_URL = "https://online.mbbank.com.vn/information-account/source-account";


    public static final String XPATH_LOGIN_USER = ".//*[@id='user-id']";
    public static final String XPATH_LOGIN_PASS = ".//*[@id='new-password']";
    public static final String XPATH_LOGIN_CAPTCHA = ".//*[@class='w-100 pl-upper ng-untouched ng-pristine ng-invalid']";
    public static final String XPATH_CAPTCHA_IMAGE = ".//mbb-word-captcha//*[@class='ng-star-inserted']";
    public static final String XPATH_LOGIN_BUTTON = ".//*[@id='login-btn']";

    public static final String LOGGED_URL = "https://online.mbbank.com.vn/";

    private int CAPTCHA_X = 745;
    private int CAPTCHA_Y = 464;
    private int CAPTCHA_W = 195;
    private int CAPTCHA_H = 50;

    private SpiderPhantomJsDriver driver;

    public MBSelenium(SpiderPhantomJsDriver driver) {
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
        LOG.info("logout");
    }

    public List<Transaction> scan() throws DriverInterruptException {
        visit(TRANSACTION_URL);

        //For each month get data
        Action.SleepAction sleepAction = new Action.SleepAction();
        sleepAction.setTimeSleep(3000);

        Action.ClickAction clickAction = new Action.ClickAction();
        clickAction.setXPathNext(".//*[@class='block-search']/form/div[3]/div[2]/button");
        SbyStep sb = new SbyStep();
        sb.addAction(clickAction);
        sb.addAction(sleepAction);

        LOG.info("The data is " + getExecutor().run(driver, sb));

        List<Transaction> rs = getTransactionOfMonth();
        Action.ClickAction clickAction1 = new Action.ClickAction();
        clickAction1.setXPathNext(".//*[@id='page-items']/button[2]");

        SbyStep sbyStep = new SbyStep();
        sbyStep.addAction(clickAction1);
        sbyStep.addAction(sleepAction);
        LOG.info("The data is " + getExecutor().run(driver, sbyStep));
        List<Transaction> data = getTransactionOfMonth();
        rs.addAll(data);

        return rs;
    }

    public List<Transaction> getTransactionOfMonth() {

        List<WebElement> elements = driver.getWebDriver().findElements(By.xpath(".//*[@class='table-responsive-xl']/table/tbody//tr"));
        LOG.info("Number elements : " + elements.size());
        List<Transaction> transactions = new ArrayList<>();
        for (int i = elements.size() - 1; i > -1; i--) {
            Action.QueryAction queryAction = new Action.QueryAction();
            //col-dateTime
            String xpathDate = ".//*[@class='table-responsive-xl']/table/tbody/tr[" + (i + 1) + "]/td[2]";
            String xpathMess = ".//*[@class='table-responsive-xl']/table/tbody/tr[" + (i + 1) + "]/td[5]";
            String xpathMoney = ".//*[@class='table-responsive-xl']/table/tbody/tr[" + (i + 1) + "]/td[3]";
            String xpathTransId = ".//*[@class='table-responsive-xl']/table/tbody/tr[" + (i + 1) + "]/td[4]";
            String xpathSender = ".//*[@class='table-responsive-xl']/table/tbody/tr[" + (i + 1) + "]/td[6]";
            String xpathAccountReceived = ".//*[@class='table-responsive-xl']/table/tbody/tr[" + (i + 1) + "]/td[7]";

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

            Action.SelectParam senderAccount = new Action.SelectParam();
            senderAccount.setName("bank");
            senderAccount.setXpath(xpathSender);
            queryAction.addParam(senderAccount);

            Action.SelectParam accountReceived = new Action.SelectParam();
            accountReceived.setName("name");
            accountReceived.setXpath(xpathAccountReceived);
            queryAction.addParam(accountReceived);

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
