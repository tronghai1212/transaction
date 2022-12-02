package org.growhack.bank.portal.driver.techcombank;

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

public class TechcombankSelenium {

    public static Logger LOG = LoggerFactory.getLogger(TechcombankSelenium.class);

    public static Configuration configuration = YamlConfiguration.YamConfigFactory.load(Configuration.MODE.PRODUCT);

    public static final String HOME_URL = "https://ib.techcombank.com.vn/servlet/BrowserServlet";

    public static final String XPATH_LOGIN_USER = ".//*[@id='signOnName']";

    public static final String XPATH_LOGIN_PASS = ".//*[@id='password']";
    public static final String XPATH_LOGIN_BUTTON = ".//*[@name='btn_login']";

    public static final String LOGGED_URL = "https://ib.techcombank.com.vn/servlet/BrowserServlet#1";

    private SpiderPhantomJsDriver driver;

    public TechcombankSelenium(SpiderPhantomJsDriver driver) {
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

        try {
            WebElement webElement = driver.getWebDriver().findElement(By.xpath("//body/div/table/tbody/*[@class='fragmentContainer printableFragment']"));
            if (webElement != null) {
                return true;
            }
        } catch (Exception e) {
            LOG.info("Login Techcombank failed");
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

    public List<Transaction> scan() throws DriverInterruptException {

        Action.ClickAction clickAction = new Action.ClickAction();
        clickAction.setXPathNext(".//*[@name='AI.QCK.ACCOUNT']");
        //For each month get data
        Action.SleepAction sleepAction = new Action.SleepAction();
        sleepAction.setTimeSleep(3000);

        Action.ClickAction clickAction1 = new Action.ClickAction();
        clickAction1.setXPathNext(".//div[@class='radio_btn_form']/input[@value='2']");
        Action.ClickAction clickAction2 = new Action.ClickAction();
        clickAction2.setXPathNext(".//body/div/table/tbody/tr[2]/td/table/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr/td[2]/table/tbody/tr/td/div/table/tbody/tr[2]//table[@id='goButton']//table/tbody/tr/td/a");
        SbyStep sb = new SbyStep();
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

        List<WebElement> elements = driver.getWebDriver().findElements(By.xpath(".//table[@class='enquirydata wrap_words']/tbody//tr"));
        LOG.info("Number elements : " + elements.size());
        List<Transaction> transactions = new ArrayList<>();
        for (int i = 0; i < elements.size(); i++) {
            Action.QueryAction queryAction = new Action.QueryAction();
            //col-dateTime
            String xpathDate = ".//table[@class='enquirydata wrap_words']/tbody/tr[" + (i + 1) + "]/td";
            String xpathMess = ".//table[@class='enquirydata wrap_words']/tbody/tr[" + (i + 1) + "]/td[2]";
            String xpathMoney = ".//table[@class='enquirydata wrap_words']/tbody/tr[" + (i + 1) + "]/td[3]";
            String xpathBalance = ".//table[@class='enquirydata wrap_words']/tbody/tr[" + (i + 1) + "]/td[4]";


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
                System.out.println(transaction);
                transactions.add(transaction);
                String webElement = driver.getWebDriver().findElement(By.xpath(xpathMess)).getText();
                String[] list = webElement.split(" / ");
                String transId = list[1];
                transaction.setTransId(transId);
                System.out.println("Transaction id:" + (transaction.toString().hashCode()));
                transaction.setTransId(String.valueOf(transaction.toString().hashCode()));
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
