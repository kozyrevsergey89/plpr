package com.protection.plpt.plpt;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.protection.plpt.plpt.mpkz.mpkz.method.InfoMethod;
import com.protection.plpt.plpt.mpkz.mpkz.net.AsyncCallback;
import com.protection.plpt.plpt.mpkz.mpkz.net.AsyncRequestor;
import com.protection.plpt.plpt.mpkz.mpkz.net.request.GCMRequest;
import com.protection.plpt.plpt.mpkz.mpkz.utils.SharedUtils;
import com.tetra.service.rest.Parameter;
import com.tetra.service.rest.Response;

import java.util.List;


/**
 * Created by sergey on 9/11/15.
 */
public class RegistrationIntentService extends IntentService {

    public static final String REG_ID = "reg_id";
    public static final String ID_MESSAGE = "id_broadcast_message";
    public static final String MESSAGE_ACTION = "com.mpkz.BROADCAST_MESSAGE";
    public static final String SENDER_ID = "978784319712";

    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};
    private String token;

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            // [START register_for_gcm]
            // Initially this call goes out to the network to retrieve the token, subsequent calls
            // are local.
            // [START get_token]
            FirebaseApp.initializeApp(this);
            token = FirebaseInstanceId.getInstance().getToken();
            // [END get_token]
            Log.i(TAG, "GCM Registration Token: " + token);

            // TODO: Implement this method to send any registration to your app's servers.
//            sendRegistrationToServer(token);


            // You should store a boolean that indicates whether the generated token has been
            // sent to your server. If the boolean is false, send the token to your server,
            // otherwise your server should have already received the token.
            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, true).apply();

            SharedUtils.writeToShared(this, REG_ID, token);
            displayMessage(this, token);
            // [END register_for_gcm]
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false).apply();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
        registrationComplete.putExtra(ID_MESSAGE, token);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void displayMessage(Context context, String message) {
        Intent intent = new Intent(MESSAGE_ACTION);
        intent.putExtra(ID_MESSAGE, message);
        context.sendBroadcast(intent);
    }

    /**
     * Persist registration to third-party servers.
     * <p>
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.

        SharedUtils.writeToShared(this, "reg_id", token);
        AsyncRequestor requestor = new AsyncRequestor(new GCMLocalCallback(this));
        String cookie = SharedUtils.getFromShared(this, "user_id");
        GCMRequest request = new GCMRequest().addRegId(token)
                .addCookie(cookie)
                .addDeviceId(new InfoMethod(this).getImey());
        requestor.execute(request);
    }

    private static class GCMLocalCallback extends AsyncCallback {

        private Context context;

        public GCMLocalCallback(final Context context) {
            this.context = context;
        }

        @Override
        public void processResponse(final Response response) {
            Log.i("123123", response.getResultCode() + "");
            Log.i("123123", response.getMessage() + "");
            Log.i("123123", response.getStreamString() + "");

            if (response.isSuccess()) {
                List<Parameter> params = response.getHeaders();
                if (params != null && !params.isEmpty()) {
                    for (Parameter param : params) {
                        if ("Second-User".equals(param.getName())) {
                            String flag = param.getValue();
                            if ("true".equals(flag)) {
                                SharedUtils.deleteFromShared(context, "user_id");
//                                activity.showProgress(false);
                                Toast.makeText(context, R.string.second_user, Toast.LENGTH_SHORT).show();
                                return;
                            } else {
                                break;
                            }
                        }
                    }
                }

                if (!response.getHeaders().isEmpty()) {
                    List<Parameter> headers = response.getHeaders();
                    for (Parameter param : headers) {
                        if ("version".equals(param.getName())) {
                            String value = param.getValue();
                            SharedUtils.writeToShared(context, "version", value);
                            Log.i("123123", "version - " + value);
                        }
                    }
                }

                boolean collision = false;
                boolean useflag = false;
                if (params != null && !params.isEmpty()) {
                    for (Parameter param : params) {
                        if ("user_collision".equals(param.getName())) {
                            collision = true;
                            Toast.makeText(context,
                                    "You have two accounts on one device",
                                    Toast.LENGTH_SHORT).show();
                            //break;
                        } else if ("use_full".equals(param.getName())) {
                            if ("true".equals(param.getValue())) {
                                useflag = true;
                            }
                        }
                    }
                }

                if (response.isSuccess() && !collision) {
                    Log.i("123123", "use_full - " + useflag);
//                    do nothing
                } else {
//                    do nothing
                }

            }
        }

    }
}
