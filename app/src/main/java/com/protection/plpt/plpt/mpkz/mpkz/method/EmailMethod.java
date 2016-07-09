package com.protection.plpt.plpt.mpkz.mpkz.method;

import android.util.Log;
import com.protection.plpt.plpt.mpkz.mpkz.method.email.GMailSender;

/**
 * Created by sergey on 6/4/16.
 */
public class EmailMethod {

  public static void sendEmail(String fileName, String recipientAddress) {
    try {
      GMailSender sender = new GMailSender("kozyrevsergey89@gmail.com", "?DctLjhjubDtlenDHbv!18");
      sender.sendMail("Secret data from Your Platinum Protection",
          "This is Body",
          "autosender@plprotect.com",
          recipientAddress, fileName);
    } catch (Exception e) {
      Log.e("SendMail", e.getMessage(), e);
    }
  }
}
