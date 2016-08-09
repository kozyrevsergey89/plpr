package com.protection.plpt.plpt.mpkz.mpkz.method;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.CallLog;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import me.everything.providers.android.calllog.Call;
import me.everything.providers.android.calllog.CallsProvider;
import me.everything.providers.android.telephony.Mms;
import me.everything.providers.android.telephony.Sms;
import me.everything.providers.android.telephony.TelephonyProvider;

/**
 * Created by sergey on 6/4/16.
 */
public class SmsMethod {

    private static final String SENT_SMS_CONTENT_PROVIDER_URI_OLDER_API_19 = "content://sms/sent";

    public static String storeSms(Context context) {
        TelephonyProvider provider = new TelephonyProvider(context);
        List<Sms> smsList = provider.getSms(TelephonyProvider.Filter.ALL).getList();

        //List<Mms> mmses = provider.gMms(TelephonyProvider.Filter.ALL).getList();

        CallsProvider callsProvider = new CallsProvider(context);
        List<Call> callList = callsProvider.getCalls().getList();

        SmsJsonDataModel smsJsonDataModel = new SmsJsonDataModel();
        smsJsonDataModel.setCallList(callList);
        smsJsonDataModel.setSmsList(smsList);
        return new GsonBuilder().create().toJson(smsJsonDataModel);
    }

    public static void writeSmsAndCalls(Context context, List<Sms> smsList, List<Call> callList) {
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && !SmsWriteOpUtil.isWriteEnabled(context) ) {
            SmsWriteOpUtil.setWriteEnabled(context, true);
        }

        for (Sms sms : smsList) {
            ContentValues values = new ContentValues();
            values.put("address", sms.address);
            values.put("body", sms.body);
            values.put("type", sms.type != null ? sms.type.ordinal() : 0);
            values.put("date_sent", sms.sentDate);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                context.getContentResolver().insert(Telephony.Sms.Sent.CONTENT_URI, values);
            } else {
                context.getContentResolver()
                        .insert(Uri.parse(SENT_SMS_CONTENT_PROVIDER_URI_OLDER_API_19), values);
            }
        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALL_LOG)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        for (Call call : callList) {
            ContentValues values = new ContentValues();
            values.put(CallLog.Calls.NUMBER, call.number);
            values.put(CallLog.Calls.DATE, call.callDate);
            values.put(CallLog.Calls.DURATION, call.duration);
            values.put(CallLog.Calls.TYPE, null != call.type ? call.type.ordinal() : 0);
            values.put(CallLog.Calls.NEW, 1);
            values.put(CallLog.Calls.CACHED_NAME, call.name);
            values.put(CallLog.Calls.CACHED_NUMBER_TYPE, 0);
            values.put(CallLog.Calls.CACHED_NUMBER_LABEL, "");

            context.getContentResolver().insert(CallLog.Calls.CONTENT_URI, values);
        }
    }
}
