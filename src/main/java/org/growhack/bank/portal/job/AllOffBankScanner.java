package org.growhack.bank.portal.job;

import org.growhack.bank.portal.config.AdminConfig;
import org.growhack.bank.portal.executor.ConfigThread;
import com.lvtech.rd.common.config.Configuration;
import com.lvtech.rd.common.config.YamlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class AllOffBankScanner {

    public static Logger LOG = LoggerFactory.getLogger(AllOffBankScanner.class);

    public static void main(String[] args){

        YamlConfiguration configuration = YamlConfiguration.
                YamConfigFactory.load(Configuration.MODE.PRODUCT);

        AdminConfig adminConfig = new AdminConfig();
        ConfigThread configThread = new ConfigThread();
        try {
            configThread.run(adminConfig, configuration);
        }catch (IOException e){
            LOG.info("Can't load config data", e);
        }
//        AcbTransScanner.executeThread(configuration, true, adminConfig);
        AgribankTransScanner.executeThread(configuration, true, adminConfig);
//        BIDVTransScanner.executeThread(configuration, true, adminConfig);
        MBTransScanner.executeThread(configuration, true, adminConfig);
        SacombankTransScanner.executeThread(configuration, true, adminConfig);
        SeabankTransScanner.executeThread(configuration, true, adminConfig);
        SHBTransScanner.executeThread(configuration, true, adminConfig);
        TechcombankTransScanner.executeThread(configuration, true, adminConfig);
        VIBTransScanner.executeThread(configuration, true, adminConfig);
        VietcombankTransScanner.executeThread(configuration, true, adminConfig);
        VietinbankTransScanner.executeThread(configuration, true, adminConfig);
    }



}
