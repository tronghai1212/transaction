package org.growhack.bank.portal.executor;

import org.apache.commons.io.FileUtils;
import org.growhack.bank.portal.config.AdminConfig;
import org.growhack.bank.portal.utils.HttpUtils;
import com.lvtech.rd.common.config.Configuration;
import com.lvtech.rd.common.config.YamlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class ConfigThread {

    final static long delayTime = 10 * 60 * 1000l;
    String URL_CONFIG ;

    public static Logger LOG = LoggerFactory.getLogger(ConfigThread.class);

    public static class DataLoader implements Runnable {

        private AdminConfig adminConfig;
        private Configuration configuration;
        public DataLoader(AdminConfig adminConfig, Configuration configuration){
            this.adminConfig = adminConfig;
            this.configuration = configuration;
        }

        public void loadConfigFromService() throws IOException {
            File file = new File(configuration.getConfigString("account-config"));
            String content = FileUtils.readFileToString(file, "utf-8");
            adminConfig.fromConfig(content);
        }

        public void run(){
            while (true){
                try {
                    loadConfigFromService();
                    LOG.info("Config : " + adminConfig);
                    Thread.sleep(delayTime);
                }catch (Exception e){
                    LOG.info("Thread has exception", e);
                }
            }
        }
    }

    public void run(AdminConfig adminConfig, Configuration configuration) throws IOException{
        DataLoader dataLoader = new DataLoader(adminConfig, configuration);
        dataLoader.loadConfigFromService();
        Thread thread = new Thread(dataLoader);
        thread.start();

    }

    public static void main(String[] args) throws IOException {
        YamlConfiguration configuration = YamlConfiguration.YamConfigFactory.load(Configuration.MODE.PRODUCT);
        AdminConfig adminConfig =  new AdminConfig();
        ConfigThread configThread = new ConfigThread();
        configThread.run(adminConfig, configuration);

    }

}
