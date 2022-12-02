package org.growhack.bank.portal.service;

import org.growhack.bank.portal.model.Transaction;
import com.google.gson.Gson;
import com.lvtech.rd.common.config.Configuration;
import com.lvtech.rd.common.config.YamlConfiguration;
import org.apache.http.HttpVersion;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class UpdateTransactionService {

    public static Logger LOG = LoggerFactory.getLogger(UpdateTransactionService.class);

    static String CALLBACK_URL;

    static {

        Configuration config = YamlConfiguration.YamConfigFactory.load(Configuration.MODE.PRODUCT);
        CALLBACK_URL = config.getConfigString("callback");

    }

    public static boolean updateTransaction(Transaction transaction) {

        Gson gson = new Gson();
        String content = gson.toJson(transaction);
        try {
            Request.Post(CALLBACK_URL).useExpectContinue().version(HttpVersion.HTTP_1_1)
                    .bodyString(content, ContentType.APPLICATION_JSON)
                    .execute().returnContent().asString();
            LOG.info("Send transaction to storage success : " + content);
            return true;
        } catch (IOException e) {
            LOG.warn("Update transaction error", e);
        }

        return false;

    }

}
