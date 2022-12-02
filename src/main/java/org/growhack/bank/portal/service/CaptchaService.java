package org.growhack.bank.portal.service;

import org.apache.commons.io.FileUtils;
import org.assertj.core.util.Files;
import org.openqa.selenium.WebDriver;
import org.poc.spider.phantom.core.SpiderPhantomJsDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class CaptchaService {

    public static Logger LOG = LoggerFactory.getLogger(CaptchaService.class);

    public static String detect(SpiderPhantomJsDriver driver,
                                int originW,
                                int originH,
                                int x,
                                int y,
                                int width,
                                int height) throws Exception {

        String fileScreen = "out/screen/" + System.currentTimeMillis() +".png";
        String fileCaptchaCrop = "out/captcha/" + System.currentTimeMillis() +".png";
        driver.screenShotWithPathFile(fileScreen);

        try {
            File file = new File(fileScreen);
            LOG.info("File is exist : "+ file.exists() +"\t" + fileScreen);
            BufferedImage originImg = ImageIO.read(file);

            float ratio = (float)originImg.getData().getWidth() / originImg.getData().getHeight();


            System.out.println("*** Ratio" +"\t" + ratio);


            BufferedImage subImage = originImg.getSubimage((int)(x * ratio) ,
                    (int)(y* ratio) , (int)(width * ratio), (int)(height *ratio));
            ImageIO.write(subImage, "png", new File(fileCaptchaCrop));
            String rs = DeathByCaptchaService.detectCaptcha(fileCaptchaCrop);
            FileUtils.deleteQuietly(new File(fileScreen));
            FileUtils.deleteQuietly(new File(fileCaptchaCrop));
            return rs;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String detect(SpiderPhantomJsDriver driver,
                                int x,
                                int y,
                                int width,
                                int height) throws Exception {

        String fileScreen = "out/screen/" + System.currentTimeMillis() +".png";
        String fileCaptchaCrop = "out/captcha/" + System.currentTimeMillis() +".png";
        WebDriver.Window window = driver.getWebDriver().manage().window();
        driver.screenShotWithPathFile(fileScreen);

        BufferedImage bimg = ImageIO.read(new File(fileScreen));
        int widthInput          = bimg.getWidth();
        int heightInput         = bimg.getHeight();

        float rateX =  (float)widthInput /window.getSize().width;
        float rateY = (float)heightInput/window.getSize().height;

        y = y + 60; // Default header
        x = (int) ((float)x * rateX);
        y = (int) ((float)y * rateY);

        try {
            File file = new File(fileScreen);
            LOG.info("File is exist : "+ file.exists() +"\t" + fileScreen);
            BufferedImage originImg = ImageIO.read(file);
            BufferedImage subImage = originImg.getSubimage(x ,
                     y, (int)((float)width * rateX), (int)((float)(height + 80) * rateY));
            ImageIO.write(subImage, "png", new File(fileCaptchaCrop));
            String rs = DeathByCaptchaService.detectCaptcha(fileCaptchaCrop);
            FileUtils.deleteQuietly(new File(fileScreen));
            FileUtils.deleteQuietly(new File(fileCaptchaCrop));
            return rs;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }



}
