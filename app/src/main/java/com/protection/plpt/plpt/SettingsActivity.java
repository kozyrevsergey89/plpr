package com.protection.plpt.plpt;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.protection.plpt.plpt.mpkz.mpkz.method.BackupAdminReceiver;
import com.protection.plpt.plpt.mpkz.mpkz.net.AsyncCallback;
import com.protection.plpt.plpt.mpkz.mpkz.net.AsyncRequestor;
import com.protection.plpt.plpt.mpkz.mpkz.net.request.AdminFlagRequest;
import com.protection.plpt.plpt.mpkz.mpkz.utils.SharedUtils;
import com.tetra.service.rest.Parameter;
import com.tetra.service.rest.Request;
import com.tetra.service.rest.Response;

import java.io.File;
import java.io.Serializable;
import java.util.List;

/**
 * Created by sergey on 8/29/15.
 */
public class SettingsActivity extends BaseActivity {

    ViewGroup adminLayout;
    ViewGroup ringtoneLayout;

    //old vars
    public static final int RESULT_ENABLE = 1, RESULT_SOUND = 5;
    private static final String TAG = "123";
    private String userId;
    private DevicePolicyManager mDPM;
    private ComponentName mDeviceAdminSample;
    private boolean mAdminActive;
    public String url, soundUriString;
    private boolean isLite = false;

    public static void start(Context context) {
        context.startActivity(new Intent(context, SettingsActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity_layout);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        adminLayout = (ViewGroup) findViewById(R.id.settings_admin);
        ringtoneLayout = (ViewGroup) findViewById(R.id.settings_ringtone);


        ((ImageView) adminLayout.findViewById(R.id.main_item_icon)).setImageResource(R.drawable.admin);
        ((TextView) adminLayout.findViewById(R.id.main_item_text)).setText(R.string.settings_admin_label);
        ((TextView) adminLayout.findViewById(R.id.main_item_text)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

        ((ImageView) ringtoneLayout.findViewById(R.id.main_item_icon)).setImageResource(R.drawable.ringtone);
        ((TextView) ringtoneLayout.findViewById(R.id.main_item_text)).setText(R.string.settings_ringtone_label);
        ((TextView) ringtoneLayout.findViewById(R.id.main_item_text)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

        //old code

        userId = SharedUtils.getFromShared(this, "user_id");

        Intent intent = getIntent();

        String version = SharedUtils.getFromShared(this, "version");
        if (null != version && !version.isEmpty() && "lite".equals(version)) {
            isLite = true;
        }

        mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mDeviceAdminSample = new ComponentName(this, BackupAdminReceiver.class);
        mAdminActive = isActiveAdmin();

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.settings_ringtone:
                        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
                        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Sound");
                        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
                        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
                        SettingsActivity.this.startActivityForResult(intent, RESULT_SOUND);
                        break;
                    case R.id.settings_admin:
                        if (isLite) {
                            Toast.makeText(SettingsActivity.this, R.string.only_in_full, Toast.LENGTH_SHORT).show();
                            break;
                        }
                        if (!mAdminActive) {
                            getAdminRights();
                        } else {
                            Toast.makeText(SettingsActivity.this,
                                    "The app already has the admin rights",
                                    Toast.LENGTH_SHORT).show();
                        }
                        break;

                }
            }
        };

        adminLayout.setOnClickListener(onClickListener);
        ringtoneLayout.setOnClickListener(onClickListener);
    }


    /**
     * Helper to determine if we are an active admin
     */
    private boolean isActiveAdmin() {
        return mDPM.isAdminActive(mDeviceAdminSample);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RESULT_ENABLE:
                if (resultCode == Activity.RESULT_OK) {
                    Log.i("DeviceAdminSample", "Admin enabled!");
                    SharedUtils.writeToShared(this, "ADMIN", String.valueOf(isActiveAdmin()));
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    //mDPM.removeActiveAdmin(mDeviceAdminSample);
                    mAdminActive = false;
                    Log.i("DeviceAdminSample", "Admin enable FAILED!");
                }
                String cookie = SharedUtils.getFromShared(this, "user_id");
                AdminFlagRequest request = new AdminFlagRequest()
                        .addCookie(cookie).setAdminFlag(isActiveAdmin());
                sendRequest(new AdminFlagCallback(), request);
                return;

            case RESULT_SOUND:
                if (resultCode == Activity.RESULT_OK) {
                    Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                    soundUriString = uri.toString();

                    Log.i("SOUND", "picked sound: " + soundUriString);
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    soundUriString = null;
                    Toast.makeText(this, R.string.default_sound, Toast.LENGTH_SHORT).show();
                }
                SharedUtils.writeToShared(this, "SOUND_URI_STRING", soundUriString);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void getAdminRights() {
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "Application need admin rights for data wipe availability.");
        startActivityForResult(intent, RESULT_ENABLE);
    }

    private void sendRequest(final AsyncCallback callback,
                             final Request<?> request) {
        AsyncRequestor requestor = new AsyncRequestor(callback);
        requestor.execute(request);
    }

    private static class UrlRequestCallback extends AsyncCallback {

        private SettingsActivity activity;

        public UrlRequestCallback(final SettingsActivity activity) {
            this.activity = activity;
        }

        @Override
        public void processResponse(final Response response) {
            if (response.isSuccess()) {
                List<Parameter> headers = response.getHeaders();
                if (headers != null && !headers.isEmpty()) {
                    for (Parameter param : headers) {
                        if ("backurl".equals(param.getName())) {
                            activity.url = param.getValue();
                            break;
                        }
                    }
                    PostFile postFile = activity.getPost();
                    postFile.addCookie(activity.userId);
                    File file = new File(Environment.getExternalStorageDirectory(), "backup.vcf");
                    postFile.addFile(file);
                    PostCallback callback = new PostCallback(activity);
                    activity.sendRequest(callback, postFile);
                    activity = null;
                }
            } else {
                activity.showProgress(false);
                Toast.makeText(activity, R.string.no_connection, Toast.LENGTH_SHORT).show();
            }
        }

    }

    public class PostMediaFile extends Request<Serializable> {

        private static final long serialVersionUID = 6468307116960005184L;

        @Override
        public com.tetra.service.rest.Request.RequestType getRequestType() {
            return RequestType.MULTIPART;
        }

        @Override
        public String getUrl() {
            return getString(R.string.base_url)+"/image_single_backup";
        }

        public PostMediaFile addFile(final File file) {
            setFile(file);
            setPostEntities("file_name", file.getName());
            return this;
        }

        public PostMediaFile addCookie(final String cookie) {
            //setHeaders("content-type", "application/xml");
            setHeaders("Cookie", "user_id=" + cookie);
            return this;
        }
    }

    public PostFile getPost() {
        return new PostFile();
    }

    private static class PostCallback extends AsyncCallback {

        private SettingsActivity activity;

        public PostCallback(final SettingsActivity activity) {
            this.activity = activity;
        }

        @Override
        public void processResponse(final Response response) {
            Log.i("123123", response.getMessage() + "");
            Log.i("123123", response.getResultCode() + "");
            Log.i("123123", response.getStatus().name());
            Log.i("123123", response.getStreamString() + "");
            activity.showProgress(false);
            activity = null;
        }
    }

    private static class AdminFlagCallback extends AsyncCallback {

        @Override
        public void processResponse(final Response response) {
            if (response.isSuccess()) {
                Log.i("123123", response.getResultCode() + "");
                Log.i("123123", response.getStreamString() + "");
                Log.i("123123", response.getMessage() + "");
            }

        }

    }

    private class PostFile extends Request<Serializable> {

        private static final long serialVersionUID = 6468307116960005184L;

        @Override
        public com.tetra.service.rest.Request.RequestType getRequestType() {
            return RequestType.MULTIPART;
        }

        @Override
        public String getUrl() {
            return SettingsActivity.this.url;
        }

        public PostFile addFile(final File file) {
            setFile(file);
            return this;
        }

        public PostFile addCookie(final String cookie) {
            //setHeaders("content-type", "application/xml");
            setHeaders("Cookie", "user_id=" + cookie);
            return this;
        }
    }


}
