package org.growhack.bank.portal.driver.seabank;

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

public class SeabankSelenium {

    public static Logger LOG = LoggerFactory.getLogger(SeabankSelenium.class);

    public static Configuration configuration = YamlConfiguration.YamConfigFactory.load(Configuration.MODE.PRODUCT);

    public static final String HOME_URL = "https://seanet.vn/canhan/auth/login";
    public static final String TRANSACTION_URL = "https://seanet.vn/canhan/account/account-statement";


    public static final String XPATH_LOGIN_USER = ".//*[@name='username']";

    public static final String XPATH_LOGIN_PASS = ".//*[@name='new-password']";
    public static final String XPATH_LOGIN_BUTTON = ".//*[@class='button-login']";

    public static final String LOGGED_URL = "https://seanet.vn/canhan/dashboard";

    private SpiderPhantomJsDriver driver;

    public SeabankSelenium(SpiderPhantomJsDriver driver){
        this.driver = driver;
    }

    public void visitHomePage() throws DriverInterruptException {
        visit(HOME_URL);
    }

    public void visit(String url) throws DriverInterruptException{
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


    public void login(String account, String pass) throws DriverInterruptException {
        Action.MultipleParamSubmitAction loginAction = new Action.MultipleParamSubmitAction();
        loginAction.addParam(new Action.ActionParam(XPATH_LOGIN_USER,
                account, "Account"));
        loginAction.addParam(new Action.ActionParam(XPATH_LOGIN_PASS,
                pass, "Pass"));
        loginAction.setClickXpath(XPATH_LOGIN_BUTTON);
        SbyStep sbyStep = new SbyStep();
        sbyStep.addAction(loginAction);
        getExecutor().run(driver,sbyStep);
    }

    public void logout() throws DriverInterruptException {
        Action.DirectLinkAction directLinkAction = new Action.DirectLinkAction(LOGGED_URL);
        Action.ClickAction clickAction = new Action.ClickAction();
        clickAction.setXPathNext(".//div[@id='kt_header_mobile']/div[1]/button");
        Action.ClickAction clickAction1 = new Action.ClickAction();
        clickAction1.setXPathNext(".//*[@id='kt_aside_menu']/ul/li[13]/a");
        SbyStep sbyStep = new SbyStep();
        sbyStep.addAction(directLinkAction);
        sbyStep.addAction(clickAction);
        sbyStep.addAction(clickAction1);
        getExecutor().run(driver, sbyStep);
        LOG.info("logout", "Current URL " + driver.getWebDriver().getCurrentUrl());
    }

    public List<Transaction> scan() throws DriverInterruptException{

        visit(TRANSACTION_URL);
        //For each month get data
        Action.SleepAction sleepAction = new Action.SleepAction();
        sleepAction.setTimeSleep(15000);

        Action.ClickAction clickAction = new Action.ClickAction();
        clickAction.setXPathNext(".//*[@class='col-sm-12  col-md-9 text-md-left']/label[1]");
        Action.ClickAction clickAction1 = new Action.ClickAction();
        clickAction1.setXPathNext(".//*[@class='btn btn-sm sb-button-danger sb-cursor-not-allowed col-8 offset-2']");
        SbyStep sb = new SbyStep();
        sb.addAction(sleepAction);
        sb.addAction(clickAction);
        sb.addAction(clickAction1);
        sb.addAction(sleepAction);
        LOG.info("The data is " + getExecutor().run(driver, sb));

        List<Transaction> rs = getTransactionOfMonth();

        return rs;
    }

    public List<Transaction> getTransactionOfMonth(){

        List<WebElement> elements = driver.getWebDriver().findElements(By.xpath(".//kt-result-statement//table"));
        LOG.info("Number elements : " + elements.size());
        List<Transaction> transactions = new ArrayList<>();
        for (int i = 0; i < elements.size(); i++){
//            Action.ClickAction clickAction = new Action.ClickAction();
//            clickAction.setXPathNext(".//kt-result-statement/table[" + (i+1)+ "]/tr[2]/td[3]");
            driver.getWebDriver().findElement(By.xpath(".//kt-result-statement/table[" + (i+1)+ "]/tr[2]/td[3]")).click();

            Action.QueryAction queryAction= new Action.QueryAction();
            //col-dateTime
            String xpathDate = ".//*[@class='modal-body']/table[3]/tr[5]/td[2]";
            String xpathMess = ".//*[@class='modal-body']/table[3]/tr[4]/td[2]";
            String xpathMoney = ".//*[@class='modal-body']/table[3]/tr[3]/td[2]";
            String xpathTransId = ".//h4[@class='modal-title fix-title']";
            String xpathAccountSender = ".//*[@class='modal-body']/table[1]/tr[1]/td[2]";
            String xpathNameReceived = ".//*[@class='modal-body']/table[2]/tr[2]/td[2]";
            String xpathBankSender = ".//*[@class='modal-body']/table[1]/tr[2]/td[2]";

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

            Action.SelectParam accParam = new Action.SelectParam();
            accParam.setName("acc");
            accParam.setXpath(xpathAccountSender);
            queryAction.addParam(accParam);

            Action.SelectParam nameParam = new Action.SelectParam();
            nameParam.setName("name");
            nameParam.setXpath(xpathNameReceived);
            queryAction.addParam(nameParam);

            Action.SelectParam bankIdParam = new Action.SelectParam();
            bankIdParam.setName("bank");
            bankIdParam.setXpath(xpathBankSender);
            queryAction.addParam(bankIdParam);

            Action.ClickAction clickAction1 = new Action.ClickAction();
            clickAction1.setXPathNext(".//*[@class='fix-modal-content']//button[@class='fix-close']");

            SbyStep sbyStep = new SbyStep();
//            sbyStep.addAction(clickAction);
            sbyStep.addAction(queryAction);
            sbyStep.addAction(clickAction1);

            try {
                JsonObject rs = getExecutor().run(driver, sbyStep);
                Transaction transaction = new Transaction(rs);
                transaction.setTransId(transaction
                        .getTransId()
                        .split(" ")[0]);
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
