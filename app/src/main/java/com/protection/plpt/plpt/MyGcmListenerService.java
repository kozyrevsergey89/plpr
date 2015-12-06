package com.protection.plpt.plpt;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.protection.plpt.plpt.mpkz.mpkz.method.AdminMethod;
import com.protection.plpt.plpt.mpkz.mpkz.method.InfoMethod;
import com.protection.plpt.plpt.mpkz.mpkz.method.LocationMethod;
import com.protection.plpt.plpt.mpkz.mpkz.net.AsyncCallback;
import com.protection.plpt.plpt.mpkz.mpkz.net.AsyncRequestor;
import com.protection.plpt.plpt.mpkz.mpkz.net.request.AdminFlagRequest;
import com.protection.plpt.plpt.mpkz.mpkz.net.request.InfoRequest;
import com.protection.plpt.plpt.mpkz.mpkz.utils.SharedUtils;
import com.protection.plpt.plpt.mpkz.mpkz.utils.WakeLocker;
import com.tetra.service.rest.Request;
import com.tetra.service.rest.Response;

/**
 * Created by sergey on 9/11/15.
 */
public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    public static final String REG_ID = "reg_id";
    public static final String ID_MESSAGE = "id_broadcast_message";
    public static final String MESSAGE_ACTION = "com.mpkz.BROADCAST_MESSAGE";
    public static final String SENDER_ID = "777289626036";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }

        if(data.getString("action")!=null) {
            Request<?> request;
            String action = data.getString("action");
            if("get_gps".equals(action)){
                RequestCallback callback = new RequestCallback();
                AsyncRequestor requestor =  new AsyncRequestor(callback);
                LocationMethod.getLocationCoordinates(this, requestor, new InfoRequest());
            } else if ("get_info".equals(action)){
                InfoMethod infoMethod = new InfoMethod(this);
                String accounts = infoMethod.getAccountList();
                String phone = infoMethod.getPhone();
                String ip = infoMethod.getIp();
                request = new InfoRequest().addParam(accounts, phone, ip)
                        .addCookie(SharedUtils.getFromShared(this, "user_id"));
                sendRequest(request);
                infoMethod.destroy();
            } else if ("wipe".equals(action)) {
                String adminFlag = SharedUtils.getFromShared(this, "ADMIN");
                String cookie = SharedUtils.getFromShared(this, "user_id");
                if (adminFlag != null && !adminFlag.isEmpty()) {
                    request = new AdminFlagRequest().addCookie(cookie).setAdminFlag(true);
                    sendRequest(request);
                    AdminMethod.doWip(this);
                } else {
                    request = new AdminFlagRequest().addCookie(cookie).setAdminFlag(false);
                    sendRequest(request);
                }
            } else if ("find_phone".equals(action)) {
                WakeLocker.wakeUp(this);
                OkActivity.startNewTask(this, OkActivity.DONE_FOUND);
            } else if ("deregister".equals(action)) {
                SharedUtils.deleteFromShared(this, "user_id");
            }
        }

        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        //sendNotification(message);
        // [END_EXCLUDE]
    }
    // [END receive_message]

//    /**
//     * Create and show a simple notification containing the received GCM message.
//     *
//     * @param message GCM message received.
//     */
//    private void sendNotification(String message) {
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
//                PendingIntent.FLAG_ONE_SHOT);
//
//        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
//                .setSmallIcon(R.drawable.ic_stat_ic_notification)
//                .setContentTitle("GCM Message")
//                .setContentText(message)
//                .setAutoCancel(true)
//                .setSound(defaultSoundUri)
//                .setContentIntent(pendingIntent);
//
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
//    }
    private void sendRequest(final Request<?> request) {
        RequestCallback callback = new RequestCallback();
        AsyncRequestor requestor =  new AsyncRequestor(callback);
        requestor.execute(request);
    }

    private void displayMessage(Context context, String message) {
        Intent intent = new Intent(MESSAGE_ACTION);
        intent.putExtra(ID_MESSAGE, message);
        context.sendBroadcast(intent);
    }

    private static final class RequestCallback extends AsyncCallback {

        @Override
        public void processResponse(final Response response) {
            if (response.isSuccess()) {
                Log.i("123123", "Done");
                Log.i("123123", response.getMessage() + "");
                Log.i("123123", response.getStreamString() + "");
            } else {
                Log.e("123123", "Doesn't work properly");
            }
        }

    }
}
