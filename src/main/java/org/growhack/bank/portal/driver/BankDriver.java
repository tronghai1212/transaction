package org.growhack.bank.portal.driver;

import org.growhack.bank.portal.model.Transaction;
import com.lvtech.rd.common.config.Configuration;
import lombok.Data;
import org.openqa.selenium.Alert;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.SessionNotFoundException;
import org.poc.spider.phantom.core.SpiderPhantomJsDriver;
import org.poc.spider.phantom.exception.DriverInterruptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


@Data
public abstract class BankDriver {

    public static Logger LOG = LoggerFactory.getLogger(BankDriver.class);

    protected String bankName = "Default App";
    protected String account;
    protected String password;
    protected Configuration configuration;
    protected SpiderPhantomJsDriver spiderPhantomJsDriver;
    protected boolean keepProfiles = true;


    public abstract void login() throws SessionNotFoundException, Exception;

    public abstract List<Transaction> scanTransactions() throws DriverInterruptException;

    public abstract void logout() throws DriverInterruptException;

    public abstract boolean isLogged() throws DriverInterruptException;

    public void restartDriver() throws DriverInterruptException {
        LOG.info("Logout first");
        ((ChromeDriver) spiderPhantomJsDriver.getWebDriver()).getCommandExecutor();
        logout();
        if (!isLogged()) {
            LOG.info("Logout success");
            closeDriver();
            LOG.info("Close driver success");
            createDriver();
            LOG.info("Create driver success .....");
        } else {
            LOG.info("Logout fail, skip close driver .... ");
        }
    }

    boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }


    public void createDriver() {

        Path path = Paths.get(configuration.getConfigString("driver-path")).getParent();
        Path driverProfiles = path.resolve("ChromeProfiles-" + bankName);

        if (!keepProfiles) {
            LOG.info("Remove driver profile: " + driverProfiles);
            deleteDirectory(driverProfiles.toFile());
        }

        if ((Boolean) configuration.getConfig("debug")) {
            spiderPhantomJsDriver = new SpiderPhantomJsDriver.Builder()
                    .setLoadImage(true)
                    .setTakesScreenshot(true)
                    .setDriverPath(configuration.getConfigString("driver-path"))
                    .setDimension(new Dimension(Integer.parseInt(configuration.getConfigString("screen-shot-width")),
                            Integer.parseInt(configuration.getConfigString("screen-shot-height"))))
                    .setAppName(bankName)
                    .build(SpiderPhantomJsDriver.DRIVER_TYPE.CHROME);
        } else {
            spiderPhantomJsDriver = new SpiderPhantomJsDriver.Builder()
                    .setLoadImage(true)
                    .setTakesScreenshot(false)
                    .setDriverPath(configuration.getConfigString("driver-path"))
                    .setAppName(bankName)
                    .setDimension(new Dimension(Integer.parseInt(configuration.getConfigString("screen-shot-width")),
                            Integer.parseInt(configuration.getConfigString("screen-shot-height"))))
                    .build(SpiderPhantomJsDriver.DRIVER_TYPE.CHROME);
        }
    }

    /**
     * Close web driver
     */
    public void closeDriver() {
        try {
            confirmAlert();
            this.spiderPhantomJsDriver.getWebDriver().close();
            this.spiderPhantomJsDriver.getWebDriver().quit();
        } catch (Exception e) {
            LOG.error("Error when close driver", e);
        }
    }

    public void confirmAlert() {
        Alert alert = this.spiderPhantomJsDriver.getWebDriver().switchTo().alert();
        if (alert != null)
            alert.accept();
    }

}
