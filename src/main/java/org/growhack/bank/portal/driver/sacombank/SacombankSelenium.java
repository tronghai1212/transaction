package org.growhack.bank.portal.driver.sacombank;

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

public class SacombankSelenium {

    public static Logger LOG = LoggerFactory.getLogger(SacombankSelenium.class);

    public static Configuration configuration = YamlConfiguration.YamConfigFactory.load(Configuration.MODE.PRODUCT);

    public static final String HOME_URL = "https://www.isacombank.com.vn/";

    public static final String XPATH_LOGIN_USER = ".//*[@class='type_UserPrincipal']";

    public static final String XPATH_LOGIN_PASS = ".//*[@id='AuthenticationFG.ACCESS_CODE']";
    public static final String XPATH_LOGIN_CAPTCHA = ".//*[@class='type_FEBAUnboundString']";
    public static final String XPATH_LOGIN_BUTTON = ".//*[@id='STU_VALIDATE_CREDENTIALS']";
    public static final String XPATH_LOGIN_BUTTON_1 = ".//*[@id='VALIDATE_STU_CREDENTIALS_UX']";
    public static final String XPATH_LOGIN_CHECKBOX = ".//*[@class='loginPanelColumnDetailStyle3']";

    private int CAPTCHA_X = 1015;
    private int CAPTCHA_Y = 260;
    private int CAPTCHA_W = 95;
    private int CAPTCHA_H = 35;

    private SpiderPhantomJsDriver driver;
    private Scanner sc = new Scanner(System.in);

    public SacombankSelenium(SpiderPhantomJsDriver driver) {
        this.driver = driver;
    }

    public void visitHomePage() throws DriverInterruptException {
        visit(HOME_URL);
    }

    public void visit(String url) throws DriverInterruptException {
        Action.SleepAction sleepAction = new Action.SleepAction();
        sleepAction.setTimeSleep(4000);
        Action.DirectLinkAction visitPage = new Action.DirectLinkAction(url);
        Action.ClickAction clickAction = new Action.ClickAction();
        clickAction.setXPathNext(".//*[@id='pbanner']");

        SbyStep sbyStep = new SbyStep();
        sbyStep.addAction(visitPage);
        sbyStep.addAction(clickAction);
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

        try {
            WebElement webElement = driver.getWebDriver().findElement(By.xpath("//*[@class='stb_WelcomeGreetingWidgetUX5']"));
            if (webElement != null) {
                return true;
            }
        } catch (Exception e) {
            LOG.info("Login Sacombank failed");
        }
        return false;
    }

    public void login(String account, String pass) throws Exception {
        Action.SleepAction sleepAction = new Action.SleepAction();
        sleepAction.setTimeSleep(3000);

        Action.MultipleParamSubmitAction loginAction = new Action.MultipleParamSubmitAction();
        loginAction.addParam(new Action.ActionParam(XPATH_LOGIN_USER,
                account, "Account"));

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
        sbyStep.addAction(sleepAction);
        sbyStep.addAction(loginAction);
        getExecutor().run(driver, sbyStep);

        Action.MultipleParamSubmitAction submitPass = new Action.MultipleParamSubmitAction();
        submitPass.addParam(new Action.ActionParam(XPATH_LOGIN_PASS,
                pass, "Pass"));
        submitPass.setClickXpath(XPATH_LOGIN_BUTTON_1);
        Action.ClickAction clickAction1 = new Action.ClickAction();
        clickAction1.setXPathNext(XPATH_LOGIN_CHECKBOX);
        SbyStep sbyStep1 = new SbyStep();
        sbyStep1.addAction(clickAction1);
        sbyStep1.addAction(submitPass);
        getExecutor().run(driver, sbyStep1);
    }

    public void logout() throws DriverInterruptException {
        LOG.info("logout");
    }

    public List<Transaction> scan() throws DriverInterruptException {

        Action.SleepAction sleepAction = new Action.SleepAction();
        sleepAction.setTimeSleep(3000);

        Action.ClickAction clickAction = new Action.ClickAction();
        clickAction.setXPathNext(".//*[@id='menu-button']/i");
        Action.ClickAction clickAction1 = new Action.ClickAction();
        clickAction1.setXPathNext(".//*[@id='nav-mobile']/li[2]/a");
        Action.ClickAction clickAction2 = new Action.ClickAction();
        clickAction2.setXPathNext(".//*[@class='menu__level menu__level--current']/li/a");
        Action.ClickAction clickAction3 = new Action.ClickAction();
        clickAction3.setXPathNext(".//*[@class='menu__level menu__level--current']/li/a");
        Action.ClickAction clickAction4 = new Action.ClickAction();
        clickAction4.setXPathNext(".//*[@class='width100percent singleTableHeading default footable-loaded footable']/tbody/tr/td/a");
        SbyStep sb = new SbyStep();
        sb.addAction(clickAction);
        sb.addAction(sleepAction);
        sb.addAction(clickAction1);
        sb.addAction(sleepAction);
        sb.addAction(clickAction2);
        sb.addAction(sleepAction);
        sb.addAction(clickAction3);
        sb.addAction(sleepAction);
        sb.addAction(clickAction4);
        sb.addAction(sleepAction);
        LOG.info("The data is " + getExecutor().run(driver, sb));

        List<Transaction> rs = getTransactionOfMonth();

        return rs;
    }

    public List<Transaction> getTransactionOfMonth() throws DriverInterruptException {

        List<WebElement> elements = driver.getWebDriver().findElements(By.xpath(".//*[@class='width100percent singleTableHeading default footable-loaded footable']/tbody//tr"));
        LOG.info("Number elements : " + elements.size());
        List<Transaction> transactions = new ArrayList<>();
        for (int i = elements.size() -1; i > -1; i --) {
            Action.QueryAction queryAction = new Action.QueryAction();
            //col-dateTime
            String xpathDate = ".//*[@class='width100percent singleTableHeading default footable-loaded footable']/tbody/tr["+(i+1)+"]/td[2]/span";
            String xpathMess = ".//*[@class='width100percent singleTableHeading default footable-loaded footable']/tbody/tr["+(i+1)+"]/td[4]/span";
            String xpathMoney = ".//*[@class='width100percent singleTableHeading default footable-loaded footable']/tbody/tr["+(i+1)+"]/td[7]/span";
            String xpathTransId = ".//*[@class='width100percent singleTableHeading default footable-loaded footable']/tbody/tr["+(i+1)+"]/td[1]/span";

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
