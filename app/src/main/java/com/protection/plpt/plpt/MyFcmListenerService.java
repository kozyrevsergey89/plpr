package com.protection.plpt.plpt;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.protection.plpt.plpt.mpkz.mpkz.method.AdminMethod;
import com.protection.plpt.plpt.mpkz.mpkz.method.AudioRecordingMethod;
import com.protection.plpt.plpt.mpkz.mpkz.method.CameraMethod;
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

import java.util.Map;

/**
 * Created by sergey on 9/11/15.
 */
public class MyFcmListenerService extends FirebaseMessagingService {

    private static String TAG = MyFcmListenerService.class.getSimpleName();

    public static final String REG_ID = "reg_id";
    public static final String ID_MESSAGE = "id_broadcast_message";
    public static final String MESSAGE_ACTION = "com.mpkz.BROADCAST_MESSAGE";
    public static final String SENDER_ID = "777289626036";

    /**
     * Called when message is received.
     *
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage message) {
        String from = message.getFrom();
        Map<String, String> data = message.getData();
        Log.d(TAG, "from: "+ from + ", priority: "+message.getPriority());
        onMessageReceivedOld(from, data);
    }

    public void onMessageReceivedOld(String from, Map<String, String> data) {
        String message = data.get("message");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);

        if (data.get("action") != null) {
            Request<?> request;
            String action = data.get("action");
            if ("get_gps".equals(action)) {
                RequestCallback callback = new RequestCallback();
                AsyncRequestor requestor = new AsyncRequestor(callback);
                LocationMethod.getLocationCoordinates(this, requestor, new InfoRequest());
            } else if ("get_info".equals(action)) {
                InfoMethod infoMethod = new InfoMethod(this);
                String accounts = infoMethod.getAccountList();
                String phone = infoMethod.getPhone();
                String ip = infoMethod.getIp();
                request = new InfoRequest().addParam(accounts, phone, ip)
                        .addCookie(SharedUtils.getFromShared(this, "user_id"));
                sendRequest(request);
                infoMethod.destroy();

//        CameraMethod.takePhoto(this, "kozyrevsergey89@gmail.com");
//        AudioRecordingMethod.recordAudioDoJob(this, "kozyrevsergey89@gmail.com");
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
            } else if ("make_photo".equals(action)) {
                WakeLocker.wakeUp(this);
                CameraMethod.takePhoto(this, data.get("email"));
            } else if ("rec_sound".equals(action)) {
                WakeLocker.wakeUp(this);
                AudioRecordingMethod.recordAudioDoJob(this, data.get("email"));
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
    }

    private void sendRequest(final Request<?> request) {
        RequestCallback callback = new RequestCallback();
        AsyncRequestor requestor = new AsyncRequestor(callback);
        requestor.execute(request);
    }

    private void displayMessage(Context context, String message) {
        Intent intent = new Intent(MESSAGE_ACTION);
        intent.putExtra(ID_MESSAGE, message);
        context.sendBroadcast(intent);
    }

    private static final class RequestCallback extends AsyncCallback {

        public static String TAG = RequestCallback.class.getSimpleName();

        @Override
        public void processResponse(final Response response) {
            if (response.isSuccess()) {
                Log.d(TAG, "Done, response: " + response.getMessage() + " stream string: " + response.getStreamString());
            } else {
                Log.e(TAG, "Doesn't work properly");
            }
        }
    }
}
