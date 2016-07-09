package com.protection.plpt.plpt.mpkz.mpkz.method;

import java.util.List;
import me.everything.providers.android.calllog.Call;
import me.everything.providers.android.telephony.Sms;

/**
 * Created by sergey on 6/8/16.
 */
public class SmsJsonDataModel {
  private List<Sms> smsList;
  private List<Call> callList;

  public List<Sms> getSmsList() {
    return smsList;
  }

  public void setSmsList(List<Sms> smsList) {
    this.smsList = smsList;
  }

  public List<Call> getCallList() {
    return callList;
  }

  public void setCallList(List<Call> callList) {
    this.callList = callList;
  }
}
