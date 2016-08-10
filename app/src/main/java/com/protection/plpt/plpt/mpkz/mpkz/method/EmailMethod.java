package com.protection.plpt.plpt.mpkz.mpkz.method;

import android.util.Log;

import com.protection.plpt.plpt.App;
import com.protection.plpt.plpt.R;
import com.protection.plpt.plpt.mpkz.mpkz.method.email.GMailSender;

/**
 * Created by sergey on 6/4/16.
 */
public class EmailMethod {

    public static void sendEmail(String fileName, String recipientAddress) {
        try {
            GMailSender sender = new GMailSender("noreply.plpr@gmail.com", "platprotect2015");
            sender.sendMail(App.getAppContext().getString(R.string.email_subject),
                    fileName.contains(".jpg") ?
                            App.getAppContext().getString(R.string.email_body_photo) :
                            App.getAppContext().getString(R.string.email_body_audio),
                    "noreply.plpr@gmail.com",
                    recipientAddress, fileName);
        } catch (Exception e) {
            Log.e("SendMail", e.getMessage(), e);
        }
    }
}
