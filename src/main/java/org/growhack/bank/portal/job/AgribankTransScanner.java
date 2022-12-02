package org.growhack.bank.portal.job;

import org.apache.commons.io.FileUtils;
import org.growhack.bank.portal.config.AdminConfig;
import org.growhack.bank.portal.driver.BankDriver;
import org.growhack.bank.portal.driver.agribank.AgribankDriver;
import org.growhack.bank.portal.executor.ConfigThread;
import org.growhack.bank.portal.utils.HttpUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lvtech.rd.common.config.Configuration;
import com.lvtech.rd.common.config.YamlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class AgribankTransScanner extends TransScanner{
    public static String bankName = "AGB";

    public static Logger LOG = LoggerFactory.getLogger(AgribankTransScanner.class);

    public AgribankTransScanner(Configuration configuration, BankDriver bankDriver){
        super(bankName,configuration,bankDriver);
    }

    public AgribankTransScanner(Configuration configuration,
                                   BankDriver bankDriver,
                                   String bankCode, AdminConfig adminConfig) {
        super(bankName, bankCode,
                configuration, bankDriver, adminConfig);
    }

    public static void execute(YamlConfiguration configuration) {
        executeThread(configuration, false, null);
    }

    public static void executeThread(YamlConfiguration configuration, AdminConfig adminConfig) {
        executeThread(configuration, true, adminConfig);
    }

    public static void executeThread(YamlConfiguration configuration, boolean runAsThread, AdminConfig adminConfig) {
        JsonElement jsonElement = null;
        try {
            File file = new File(configuration.getConfigString("account-config"));
            String content = FileUtils.readFileToString(file, "utf-8");
            HashMap<String, JsonElement> banksMap = adminConfig.getAccountFromConfig(content);
            jsonElement = banksMap.get(bankName.toLowerCase());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (jsonElement != null) {
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = jsonParser.parse(String.valueOf(jsonElement)).getAsJsonObject();
            String account = String.valueOf(jsonObject.get("acc")).replaceAll("\"", "");
            String password = String.valueOf(jsonObject.get("pass")).replaceAll("\"", "");
            String bankCode = bankName.toLowerCase();
            AgribankDriver driver = new AgribankDriver(configuration, account, password);
            AgribankTransScanner scanner = new AgribankTransScanner(configuration, driver, bankCode, adminConfig);

            if (runAsThread) {
                Thread thread = new Thread(scanner);
                thread.start();
            } else {
                scanner.scan();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        YamlConfiguration configuration = YamlConfiguration.YamConfigFactory.load(Configuration.MODE.PRODUCT);
        AdminConfig adminConfig = new AdminConfig();
        ConfigThread configThread = new ConfigThread();
        configThread.run(adminConfig, configuration);
        executeThread(configuration, true, adminConfig);
    }
}
