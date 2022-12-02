package org.growhack.bank.portal.driver.vib;

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

public class VIBSelenium {

    public static Logger LOG = LoggerFactory.getLogger(VIBSelenium.class);

    public static Configuration configuration = YamlConfiguration.YamConfigFactory.load(Configuration.MODE.PRODUCT);

    public static final String HOME_URL = "https://ib.vib.com.vn/vi-vn/individual/myhome.aspx";
    public static final String TRANSACTION_URL = "https://ib.vib.com.vn/vi-vn/canhan2020v2/taikhoan.aspx";


    public static final String LOGOUT_URL = "https://ib.vib.com.vn/tabid/588/ctl/Logoff/language/vi-VN/Default.aspx";
    public static final String XPATH_LOGIN_USER = ".//*[@id='Username']";

    public static final String XPATH_LOGIN_PASS = ".//*[@id='Password']";
    public static final String XPATH_LOGIN_BUTTON = ".//*[@id='buttonLogin']";

    public static final String LOGGED_URL = "https://ib.vib.com.vn/vi-vn/canhan2020v2/taikhoan.aspx";

    private SpiderPhantomJsDriver driver;

    public VIBSelenium(SpiderPhantomJsDriver driver){
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
        Action.DirectLinkAction directLinkAction = new Action.DirectLinkAction(LOGOUT_URL);
        SbyStep sbyStep = new SbyStep();
        sbyStep.addAction(directLinkAction);
        getExecutor().run(driver, sbyStep);
        LOG.info("logout", "Current URL " + driver.getWebDriver().getCurrentUrl());
    }

    public List<Transaction> scan() throws DriverInterruptException{

        //Visit transactions page
        visit(TRANSACTION_URL);
        //For each month get data
        Action.SleepAction sleepAction = new Action.SleepAction();
        sleepAction.setTimeSleep(3000);

        Action.QueryAction queryAction = new Action.QueryAction();
        Action.SelectParam selectParam = new Action.SelectParam();
        selectParam.setXpath(".//*[@class='transaction-history']");
        selectParam.setName("Content");
        queryAction.addParam(selectParam);
        SbyStep sb = new SbyStep();
        sb.addAction(sleepAction);
        sb.addAction(queryAction);
        LOG.info("The data is " + getExecutor().run(driver, sb));

        List<Transaction> rs = new ArrayList<>();

        for(int i =0; i <3; i++){
            Action.ClickAction clickAction1= new Action.ClickAction();
            clickAction1.setXPathNext(".//*[@class='dropdown unLabel']");
            Action.ClickAction clickAction2= new Action.ClickAction();
            clickAction2.setXPathNext(".//*[@class='list scroller']/div[" + (i + 1) +"]");
            clickAction2.setActionName("Click to " + i);
            SbyStep sbyStep = new SbyStep();
            sbyStep.addAction(clickAction1);
            sbyStep.addAction(sleepAction);
            sbyStep.addAction(clickAction2);
            try {
                getExecutor().run(driver, sbyStep);
                rs.addAll(getTransactionOfMonth());
            }catch(DriverInterruptException e){
                LOG.info("Execute action error ", e );
            }
        }

        return rs;
    }

    public List<Transaction> getTransactionOfMonth(){

        List<WebElement> elements = driver.getWebDriver().findElements(By.xpath(".//*[@id='listTransaction']//tr"));
        LOG.info("Number elements : " + elements.size());
        List<Transaction> transactions = new ArrayList<>();
        for (int i = elements.size() -1; i > -1; i --){
            Action.ClickAction clickAction = new Action.ClickAction();
            clickAction.setXPathNext(".//*[@id='listTransaction']/table/tbody/tr[" + (i+1)+ "]");
            Action.QueryAction queryAction= new Action.QueryAction();
            //col-dateTime
            String xpathDate = ".//*[@id='listTransaction']/table/tbody/tr[@class='infomation-transaction toogle-border']//*[@class='col-dateTime']";
            String xpathMess = ".//*[@id='listTransaction']/table/tbody/tr[@class='infomation-transaction toogle-border']//*[@class='col-action']";
            String xpathMoney = ".//*[@id='listTransaction']/table/tbody/tr[@class='infomation-transaction toogle-border']//*[@class='col-value text-right blue']";
            String xpathTransId = ".//*[@id='listTransaction']/table/tbody/tr[@class='infomation-transaction toogle-border']//*[@class='sub sub-style2 status-success']/div[1]/div[1]/p[2]";
            String xpathAccountSender = ".//*[@id='listTransaction']/table/tbody/tr[@class='infomation-transaction toogle-border']//*[@class='sub sub-style2 status-success']/div[1]/div[2]/p[3]";

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

            SbyStep sbyStep = new SbyStep();
            sbyStep.addAction(clickAction);
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
