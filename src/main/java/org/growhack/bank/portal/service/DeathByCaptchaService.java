package org.growhack.bank.portal.service;

import com.imagetyperzapi.ImageTyperzAPI;
import com.lvtech.rd.common.config.Configuration;
import com.lvtech.rd.common.config.YamlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class DeathByCaptchaService {

    public static Logger LOG = LoggerFactory.getLogger(DeathByCaptchaService.class);

    static String token;

    static {
        token = YamlConfiguration
                .YamConfigFactory
                .load(Configuration.MODE.PRODUCT)
                .getConfigString("captcha-token");
    }

    public static String detectCaptcha(String pathFile) throws Exception {
        ImageTyperzAPI i = new ImageTyperzAPI(token);
        HashMap<String, String> image_params = new HashMap<>();
        String captcha_id = i.submit_image(pathFile, image_params);
        HashMap<String, String> response = i.retrieve_response(captcha_id);
        Map.Entry<String,String> captchaValue = response.entrySet()
                .stream()
                .findFirst()
                .get();
        String result = captchaValue.getValue();
        return result;
    }

    public static void main(String[] args) throws Exception {
        String pathFile = "C:\\Users\\NHT\\Desktop\\avoid-captcha.jpg";
        DeathByCaptchaService deathByCaptchaService = new DeathByCaptchaService();
        System.out.println(deathByCaptchaService.detectCaptcha(pathFile));
    }
}
