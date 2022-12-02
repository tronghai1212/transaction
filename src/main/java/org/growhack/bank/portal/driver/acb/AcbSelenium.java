package org.growhack.bank.portal.driver.acb;

import org.growhack.bank.portal.model.Transaction;
import org.growhack.bank.portal.service.CaptchaService;
import com.google.gson.JsonObject;
import com.lvtech.rd.common.config.Configuration;
import com.lvtech.rd.common.config.YamlConfiguration;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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

public class AcbSelenium {

    public static Logger LOG = LoggerFactory.getLogger(AcbSelenium.class);

    public static Configuration configuration = YamlConfiguration.YamConfigFactory.load(Configuration.MODE.PRODUCT);

    public static final String HOME_URL = "https://online.acb.com.vn/acbib/";

    public static final String XPATH_LOGIN_USER = ".//input[@id='user-name']";
    public static final String XPATH_LOGIN_PASS = ".//input[@id='password']";
    public static final String XPATH_LOGIN_CAPTCHA = ".//input[@id='security-code']";
    public static final String XPATH_LOGIN_BUTTON = ".//*[@class='button-blue acbone-submit-button']";



    public static final String LOGGED_URL = "https://online.acb.com.vn/acbib/Request";

    private int CAPTCHA_X = 1090;
    private int CAPTCHA_Y = 500;
    private int CAPTCHA_W = 150;
    private int CAPTCHA_H = 50;

    private SpiderPhantomJsDriver driver;

    public AcbSelenium(SpiderPhantomJsDriver driver){
        this.driver = driver;
    }

    public void visitHomePage() throws DriverInterruptException {
        visit(HOME_URL);
    }

    public void visit(String url) throws DriverInterruptException{

        Action.SleepAction sleepAction = new Action.SleepAction();
        sleepAction.setTimeSleep(3000);

        Action.ClickAction clickAction = new Action.ClickAction();
        clickAction.setXPathNext("//*[@class='content-holder']/div/div/a");

        Action.DirectLinkAction visitPage = new Action.DirectLinkAction(url);

        SbyStep sbyStep = new SbyStep();
        sbyStep.addAction(visitPage);
        sbyStep.addAction(sleepAction);
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

        SbyStep sbyStep = new SbyStep();
        //sbyStep.addAction(loginAction);
        driver.getWebDriver().findElement(By.xpath(".//input[@name='UserName']")).click();
        driver.getWebDriver().findElement(By.xpath(".//input[@name='UserName']")).clear();
        driver.getWebDriver().findElement(By.xpath(".//input[@name='UserName']")).sendKeys(account);
        driver.getWebDriver().findElement(By.xpath(".//input[@id='password']")).click();
        driver.getWebDriver().findElement(By.xpath(".//input[@id='password']")).clear();
        driver.getWebDriver().findElement(By.xpath(".//input[@id='password']")).sendKeys(pass);
        driver.getWebDriver().findElement(By.xpath(".//input[@id='security-code']")).click();
        driver.getWebDriver().findElement(By.xpath(".//input[@id='security-code']")).clear();
        driver.getWebDriver().findElement(By.xpath(".//input[@id='security-code']")).sendKeys(captchaValue);


/*
        WebElement element = driver.getWebDriver().findElement(By.xpath(".//*[@class='button-blue acbone-submit-button']"));
        Actions actions = new Actions(driver.getWebDriver());
        actions.moveToElement(element);
        actions.perform();
*/

        ((JavascriptExecutor) driver.getWebDriver())
                .executeScript("window.scrollTo(0, document.body.scrollHeight)");

        //WebElement.sendKeys(Keys.DOWN);



        Action.ClickAction clickAction = new Action.ClickAction();
        clickAction.setXPathNext(".//*[@class='button-blue acbone-submit-button']");
        sbyStep.addAction(sleepAction);
        sbyStep.addAction(clickAction);

        getExecutor().run(driver, sbyStep);
    }

    public void logout() throws DriverInterruptException {
        Action.DirectLinkAction directLinkAction = new Action.DirectLinkAction(HOME_URL);
        SbyStep sbyStep = new SbyStep();
        sbyStep.addAction(directLinkAction);
        getExecutor().run(driver, sbyStep);
        LOG.info("logout", "Current URL " + driver.getWebDriver().getCurrentUrl());
    }

    public List<Transaction> scan() throws DriverInterruptException{

        //For each month get data
        Action.SleepAction sleepAction = new Action.SleepAction();
        sleepAction.setTimeSleep(3000);

        Action.ClickAction clickAction = new Action.ClickAction();
        clickAction.setXPathNext(".//*[@id='top_link']/ul/li/a");
        Action.ClickAction clickAction1 = new Action.ClickAction();
        clickAction1.setXPathNext(".//*[@id='topmenu_left']/ul/li[2]/a");
        Action.ClickAction clickAction2 = new Action.ClickAction();
        clickAction2.setXPathNext(".//*[@class='menu_link']/table/tbody/tr/td/a");
        Action.ClickAction clickAction3 = new Action.ClickAction();
        clickAction3.setXPathNext(".//*[@id='content']/table/tbody/tr/td/table/tbody/tr[3]/td/div/a");
        Action.ClickAction clickAction4 = new Action.ClickAction();
        clickAction4.setXPathNext(".//*[@id='table']/tbody/tr[2]/td/a");
        Action.ClickAction clickAction5 = new Action.ClickAction();
        clickAction5.setXPathNext(".//*[@id='button']");
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
        sb.addAction(clickAction5);
        LOG.info("The data is " + getExecutor().run(driver, sb));

        List<Transaction> rs = getTransactionOfMonth();

        return rs;
    }

    public List<Transaction> getTransactionOfMonth(){

        List<WebElement> elements = driver.getWebDriver().findElements(By.xpath(".//*[@id='table1']/tbody//tr"));
        LOG.info("Number elements : " + elements.size());
        List<Transaction> transactions = new ArrayList<>();
        for (int i = elements.size() -1; i > -1; i --){
            Action.QueryAction queryAction= new Action.QueryAction();
            //col-dateTime
            String xpathDate = ".//*[@id='table1']/tbody/tr["+(i+2)+"]/td[1]";
            String xpathMess = ".//*[@id='table1']/tbody/tr["+(i+2)+"]/td[3]";
            String xpathMoney = ".//*[@id='table1']/tbody/tr["+(i+2)+"]/td[5]";
            String xpathTransId = ".//*[@id='table1']/tbody/tr["+(i+2)+"]/td[2]";

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
            }catch (DriverInterruptException e){
                LOG.warn("Execute action error", e);
            }
        }

        return transactions;
    }

    public SpiderPhantomExecutor getExecutor(){
        SpiderPhantomExecutor spiderPhantomExecutor = new SpiderPhantomExecutor(configuration);
        return spiderPhantomExecutor;
    }
}
