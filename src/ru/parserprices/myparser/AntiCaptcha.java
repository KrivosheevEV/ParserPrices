package ru.parserprices.myparser;

import net.marketer.RuCaptcha;

import java.io.File;

/**
 * Created by KrivosheevEV on 04.10.2016.
 */
public class AntiCaptcha {

    private String decryption = "";
    private Boolean captchaIsReady = false;

    public AntiCaptcha(String pathToCaptcha) throws Exception {

        File imgFile = new File(pathToCaptcha);
        RuCaptcha.API_KEY = "5c8c07e0c74c05af66c3cfeb277117eb";
        String CAPCHA_ID;

        String response = RuCaptcha.postCaptcha(imgFile);

        if (response.startsWith("OK")) {
            CAPCHA_ID = response.substring(3);
            while (true) {
                response = RuCaptcha.getDecryption(CAPCHA_ID);
                if (response.equals(RuCaptcha.Responses.CAPCHA_NOT_READY.toString())) {
                    Thread.sleep(5000);
//                    continue;
                } else if (response.startsWith("OK")) {
                    decryption = response.substring(3);
                    break;
                } else {
                    decryption = response;
                    break;
                }
            }
            captchaIsReady = true;
        }
    }

    public String getCaptchaText() {
        return decryption;
    }

    public Boolean getCaptchaStatus() {
        return captchaIsReady;
    }
}