package org.growhack.bank.portal.service;

import com.lvtech.rd.common.config.Configuration;
import com.lvtech.rd.common.config.YamlConfiguration;
import org.apache.http.HttpVersion;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BotService {

    private final Logger LOG = LoggerFactory.getLogger(BotService.class);

    public static Configuration configuration = YamlConfiguration.YamConfigFactory.load(Configuration.MODE.PRODUCT);

    private String url;

    private void sendMessage(String chatId, String message, String botURL) {
        String urlSend = String.format(botURL, chatId, message);
        try {
            Request.Post(urlSend).useExpectContinue().version(HttpVersion.HTTP_1_1)
                    .bodyString("Login failed", ContentType.APPLICATION_JSON)
                    .execute();
        } catch (IOException e) {
            LOG.warn("Update transaction error", e);
        }
    }

    public void reportBanks(String bankName) {
        String banksbotUrl = configuration.getConfigString("banksbot.url");
        List<String> banksbotList = new ArrayList<>(Arrays.asList(banksbotUrl.split(";")));
        for (String s : banksbotList) {
            String message = "Login failed report: ";
            String[] botURL = s.split(",");
            String[] channelIds = botURL[1].split("[+]");
            for (String ch : channelIds) {
                sendMessage(ch.trim(), URLEncoder.encode(message + bankName), botURL[0]);
            }
        }
    }

    public void reportBanks(String bankName, int numberRoundFail) {
        String banksbotUrl = configuration.getConfigString("banksbot.url");
        List<String> banksbotList = new ArrayList<>(Arrays.asList(banksbotUrl.split(";")));
        for (String s : banksbotList) {
            String message = "Login failed report, with number fail round " + numberRoundFail +". Of bank: " ;
            String[] botURL = s.split(",");
            String[] channelIds = botURL[1].split("[+]");
            for (String ch : channelIds) {
                sendMessage(ch.trim(), URLEncoder.encode(message + bankName), botURL[0]);
            }
        }
    }

    public static void main(String[] args) {
        BotService botService = new BotService();
        botService.reportBanks("ACB");
    }
}
