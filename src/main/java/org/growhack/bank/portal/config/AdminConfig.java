package org.growhack.bank.portal.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Data
@ToString
public class AdminConfig {

    public HashMap<String, Boolean> enableList = new HashMap<>();

    public void fromConfig(String body) {
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(body).getAsJsonObject().get("config").getAsJsonObject();
        Iterator<Map.Entry<String, JsonElement>> itr = jsonObject.entrySet().iterator();

        while (itr.hasNext()) {
            Map.Entry<String, JsonElement> entry = itr.next();
            String key = entry.getKey();
//            int value = entry.getValue().getAsInt();

            if (NumberUtils.isNumber(String.valueOf(entry.getValue())) == true) {
                if (entry.getValue().getAsInt() == 0)
                    enableList.put(key, false);
                else
                    enableList.put(key, true);
            } else {
                JsonObject jsonObject1 = jsonParser.parse(String.valueOf(entry.getValue())).getAsJsonObject();
                Iterator<Map.Entry<String, JsonElement>> itr1 = jsonObject1.entrySet().iterator();
                while (itr1.hasNext()) {
                    Map.Entry<String, JsonElement> entry1 = itr1.next();
                    String key1 = entry1.getKey();
                    if (key1.equals("status")) {
                        int value1 = entry1.getValue().getAsInt();
                        if (value1 == 0)
                            enableList.put(key, false);
                        else
                            enableList.put(key, true);
                    }
                }
            }


        }

    }

    public HashMap<String, JsonElement> getAccountFromConfig(String content) {
        HashMap<String, JsonElement> accountMap = new HashMap<>();
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(content).getAsJsonObject().get("config").getAsJsonObject();
        Iterator<Map.Entry<String, JsonElement>> itr = jsonObject.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry<String, JsonElement> entry = itr.next();
            if (NumberUtils.isNumber(String.valueOf(entry.getValue())) == false) {
                accountMap.put(entry.getKey(), entry.getValue());
            }
        }
        return accountMap;
    }

    public boolean getActiveStatusOfBank(String bankId) {
        return enableList.get(bankId);
    }

}
