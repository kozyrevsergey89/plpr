package com.protection.plpt.plpt;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Telephony;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.protection.plpt.plpt.mpkz.mpkz.method.BackupAdminReceiver;
import com.protection.plpt.plpt.mpkz.mpkz.method.ContactsMethod;
import com.protection.plpt.plpt.mpkz.mpkz.method.InfoMethod;
import com.protection.plpt.plpt.mpkz.mpkz.method.SmsJsonDataModel;
import com.protection.plpt.plpt.mpkz.mpkz.method.SmsMethod;
import com.protection.plpt.plpt.mpkz.mpkz.net.AsyncCallback;
import com.protection.plpt.plpt.mpkz.mpkz.net.AsyncRequestor;
import com.protection.plpt.plpt.mpkz.mpkz.net.request.GCMRequest;
import com.protection.plpt.plpt.mpkz.mpkz.net.request.GetBackFile;
import com.protection.plpt.plpt.mpkz.mpkz.utils.SharedUtils;
import com.tetra.service.rest.Parameter;
import com.tetra.service.rest.Request;
import com.tetra.service.rest.Response;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

/**
 * Created by sergey on 8/14/15.
 */
public class MainActivity extends BaseActivity {

    private static final int DEFAULT_SMS_REQUEST_CODE = 11;
    public static final int REQUEST_CODE_RETURN_SMS_DEFAULT = 48;
    private String defaultSmsApp;

    public static void start(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    ViewGroup createLayout;
    ViewGroup restoreLayout;
    ViewGroup settingsLayout;
    ViewGroup restoreSmsLayout;
    ViewGroup rebindLayout;
    TextView boundDeviceTxt;


    //old code

    public static final int RESULT_ENABLE = 1, RESULT_SOUND = 5;
    private static final String TAG = "123";
    private String userId;
    //    private Button restore,  mapDevice;
    private DevicePolicyManager mDPM;
    private ComponentName mDeviceAdminSample;
    //    private boolean mAdminActive;
    private boolean useflag = false;
    public String url, soundUriString;
    //    private ContactLader contactLader;
    private boolean isLite = false;
    private static final int SELECT_PICTURE = 8;
//    private String selectedImagePath;

    //old code finish

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        createLayout = (ViewGroup) findViewById(R.id.main_backup);
        restoreLayout = (ViewGroup) findViewById(R.id.main_restore);
        settingsLayout = (ViewGroup) findViewById(R.id.main_settings);
        restoreSmsLayout = (ViewGroup) findViewById(R.id.main_restore_sms);
        rebindLayout = (ViewGroup) findViewById(R.id.main_bind);
        rebindLayout.setVisibility(View.GONE);
        boundDeviceTxt = (TextView) findViewById(R.id.main_bound_device_label);


        Spannable createText = new SpannableString(getString(R.string.main_create_label_first).toUpperCase() + "\n" + getString(R.string.main_create_label_second));
        createText.setSpan(new RelativeSizeSpan(0.5f), getString(R.string.main_create_label_first).length(), createText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ((ImageView) createLayout.findViewById(R.id.main_item_icon)).setImageResource(R.drawable.backup);
        ((TextView) createLayout.findViewById(R.id.main_item_text)).setText(createText);

        Spannable restoreText = new SpannableString(getString(R.string.main_restore_label_first).toUpperCase() + "\n" + getString(R.string.main_restore_label_second));
        restoreText.setSpan(new RelativeSizeSpan(0.5f), getString(R.string.main_restore_label_first).length(), restoreText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ((ImageView) restoreLayout.findViewById(R.id.main_item_icon)).setImageResource(R.drawable.restore);
        ((TextView) restoreLayout.findViewById(R.id.main_item_text)).setText(restoreText);

        ((ImageView) settingsLayout.findViewById(R.id.main_item_icon)).setImageResource(R.drawable.settings);
        ((TextView) settingsLayout.findViewById(R.id.main_item_text)).setText(R.string.main_settings_label);

        Spannable bindText = new SpannableString(getString(R.string.main_bind_new_device_label_first).toUpperCase() + "\n" + getString(R.string.main_bind_new_device_label_second));
        bindText.setSpan(new RelativeSizeSpan(0.5f), getString(R.string.main_bind_new_device_label_first).length(), bindText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ((ImageView) rebindLayout.findViewById(R.id.main_item_icon)).setImageResource(R.drawable.restore);
        ((TextView) rebindLayout.findViewById(R.id.main_item_text)).setText(bindText);

        Spannable restoreSmsText = new SpannableString(getString(R.string.main_restore_label_first_sms).toUpperCase() + "\n" + getString(R.string.main_restore_label_second_sms));
        restoreSmsText.setSpan(new RelativeSizeSpan(0.5f), getString(R.string.main_restore_label_first_sms).length(), restoreSmsText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ((ImageView) restoreSmsLayout.findViewById(R.id.main_item_icon)).setImageResource(R.drawable.restore);
        ((TextView) restoreSmsLayout.findViewById(R.id.main_item_text)).setText(restoreSmsText);


        userId = SharedUtils.getFromShared(this, "user_id");

        Intent intent = getIntent();
        if (intent != null && intent.getExtras()!=null && !intent.getExtras().isEmpty()) {
            useflag = intent.getExtras().getBoolean("use_full");
            SharedUtils.writeBooleanToShared(this, "use_full", useflag);
        } else if (intent!=null){
            useflag = intent.getBooleanExtra("use_full", false);
        }
        rebindLayout.setVisibility(View.GONE);
        String version = SharedUtils.getFromShared(this, "version");
        if (null != version && !version.isEmpty() && "lite".equals(version)) {
            isLite = true;
        }

        if (!useflag) {
            showBoundBehavior();
        }

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.main_backup:
                        BackupActivity.start(MainActivity.this);
                        break;
                    case R.id.main_restore:

                        if (isLite) {
                            Toast.makeText(MainActivity.this, R.string.only_in_full, Toast.LENGTH_SHORT).show();
                            break;
                        }
                        showProgress(true);
                        GetBackFile backGet = new GetBackFile().addCookie(userId);
                        GetBackFileCallback fileCallback = new GetBackFileCallback(MainActivity.this);
                        sendRequest(fileCallback, backGet);
                        break;
                    case R.id.main_settings:
                        SettingsActivity.start(MainActivity.this);
                        break;
                    case R.id.main_bind:
                        showProgress(true);
                        String regId = SharedUtils.getFromShared(MainActivity.this, "reg_id");
                        GCMRequest request = new GCMRequest()
                                .addCookie(userId)
                                .addRegId(regId)
                                .addDeviceId(new InfoMethod(MainActivity.this).getImey())
                                .addUseThisHeader();

                        sendRequest(new AsyncCallback() {
                            @Override
                            public void processResponse(final Response response) {
                                Log.i("123123", response.getResultCode() + "");
                                Log.i("123123", response.getMessage() + "");
                                Log.i("123123", response.getStreamString() + "");
                                if (response.isSuccess()) {
                                    List<Parameter> headers = response.getHeaders();
                                    if (headers != null && !headers.isEmpty()) {
                                        for (Parameter param : headers) {
                                            if ("use_full".equals(param.getName())) {
                                                if ("true".equals(param.getValue())) {
                                                    useflag = true;
//                                                    showCommonBehavior();
                                                    SharedUtils.writeBooleanToShared(MainActivity.this, "use_full", true);
                                                    Log.i("123123", "use_full - " + param.getValue());
                                                } else {
                                                    Toast.makeText(getBaseContext(), R.string.not_mapped, Toast.LENGTH_SHORT).show();
                                                }
                                                showProgress(false);
                                                checkIfDeviceAlreadyBound();
                                                break;
                                            }
                                        }
                                    }
                                } else {
                                    showProgress(false);
                                    checkIfDeviceAlreadyBound();
                                }
                            }
                        }, request);
                        //showProgress(false);
                        break;
                    case R.id.main_restore_sms:
                        showProgress(true);
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {

                            defaultSmsApp = Telephony.Sms.getDefaultSmsPackage(MainActivity.this);
                            final String myPackageName = getPackageName();
                            if (!myPackageName.equals(defaultSmsApp)) {
                                Toast.makeText(MainActivity.this, R.string.sms_default_app, Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                                intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, MainActivity.this.getPackageName());
                                startActivityForResult(intent, DEFAULT_SMS_REQUEST_CODE);
                            } else {
                                startRestoreSms();
                            }

                        } else {
                            startRestoreSms();
                        }

                        break;

                }
            }
        };

        createLayout.setOnClickListener(onClickListener);
        restoreLayout.setOnClickListener(onClickListener);
        settingsLayout.setOnClickListener(onClickListener);
        rebindLayout.setOnClickListener(onClickListener);
        restoreSmsLayout.setOnClickListener(onClickListener);

        mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mDeviceAdminSample = new ComponentName(this, BackupAdminReceiver.class);

    }

    private void startRestoreSms() {
        showProgress(true);
        new SmsRestoreLader().execute();
    }

    private void checkIfDeviceAlreadyBound() {
        if (SharedUtils.getBooleanFromShared(this, "use_full")) {
            showCommonBehavior();
        } else {
            showBoundBehavior();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkIfDeviceAlreadyBound();
        showProgressIfNeeded();
    }

    private void showCommonBehavior() {
        Log.i("123123", "common");
        createLayout.setVisibility(View.VISIBLE);
        restoreLayout.setVisibility(View.VISIBLE);
        restoreSmsLayout.setVisibility(View.VISIBLE);
        settingsLayout.setVisibility(View.VISIBLE);
        rebindLayout.setVisibility(View.GONE);
        boundDeviceTxt.setVisibility(View.GONE);
    }

    private void showBoundBehavior() {
        Log.i("123123", "bound");
        createLayout.setVisibility(View.GONE);
        restoreLayout.setVisibility(View.GONE);
        restoreSmsLayout.setVisibility(View.GONE);
        settingsLayout.setVisibility(View.GONE);
        rebindLayout.setVisibility(View.VISIBLE);
        boundDeviceTxt.setVisibility(View.VISIBLE);
    }

    private boolean deviceAlreadyBound() {
        return false;
    }

    //old methods section


    private void sendRequest(final AsyncCallback callback,
                             final Request<?> request) {
        AsyncRequestor requestor = new AsyncRequestor(callback);
        requestor.execute(request);
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


    private class GetBackFileCallback extends AsyncCallback {

        private Context context;
        private ContactsMethod method;

        public GetBackFileCallback(final Context context) {
            this.context = context;
            method = new ContactsMethod();
        }

        @Override
        public void processResponse(final Response response) {
            ((MainActivity) context).showProgress(false);
            checkIfDeviceAlreadyBound();
            if (response.isSuccess()) {
                String contacts = response.getStreamString();
                if (contacts != null && !contacts.isEmpty()) {
                    createFile(contacts);
                    method.importContacts(context);
                    context = null;
                    method = null;
                }
            }
        }

        private void createFile(final String string) {
            try {
                String path = Environment.getExternalStorageDirectory().toString() + File.separator + "backup.vcf";
                BufferedWriter out = new BufferedWriter(new FileWriter(path));
                out.write(string);
                out.close();
            } catch (final IOException e) {
                Log.e("123123", e.getMessage() + "");
            }
        }


    }

    private class SmsRestoreLader extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           showProgress(true);
        }

        @Override
        protected String doInBackground(Void... params) {
//            showProgress(true);
            GetSmsAndCall get = new GetSmsAndCall();
            get.addCookie(userId);
            sendRequest(new AsyncCallback() {
                @Override public void processResponse(Response response) {
                    if (response.isSuccess()) {
                        String decoded = "";
                        try {
                            decoded = URLDecoder.decode(response.getStreamString(), "UTF-8");
                            decoded = decoded.substring(1, decoded.length()-2);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        final String decodedFinal = decoded;
                        new AsyncTask<Void,Void,Void>(){
                            @Override
                            protected Void doInBackground(Void... params) {
                                SmsJsonDataModel smsJsonDataModel = new GsonBuilder().create().fromJson(decodedFinal, SmsJsonDataModel.class);
                                SmsMethod.writeSmsAndCalls(MainActivity.this, smsJsonDataModel.getSmsList(), smsJsonDataModel.getCallList());
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                super.onPostExecute(aVoid);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    Toast.makeText(MainActivity.this, R.string.dont_forget_default_sms_app, Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                                    intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, defaultSmsApp);
                                    startActivityForResult(intent, REQUEST_CODE_RETURN_SMS_DEFAULT);
                                } else {
                                    showProgress(false);
                                    OkActivity.start(MainActivity.this, OkActivity.DONE_RESTORE);
                                }
                            }
                        }.execute();

//                        Toast.makeText(MainActivity.this, "" + " sms and call log stored successfully! ",
//                            Toast.LENGTH_SHORT).show();
//                        showProgress(false);
//                        OkActivity.start(MainActivity.this, OkActivity.DONE_RESTORE);

//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                            Toast.makeText(MainActivity.this, R.string.dont_forget_default_sms_app, Toast.LENGTH_LONG).show();
//                            Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
//                            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, defaultSmsApp);
//                            startActivityForResult(intent, REQUEST_CODE_RETURN_SMS_DEFAULT);
//                        }
                    } else {
                        showProgress(false);
                        Log.i("123", "ne proshlo sms restore" + response.getMessage());
                    }
                }
            }, get);
            return null;
        }

        @Override
        protected void onPostExecute(final String result) {
            super.onPostExecute(result);
//            showProgress(false);

        }
    }

    public class GetSmsAndCall extends Request<Serializable> {

        private static final long serialVersionUID = 6468307116960905184L;

        @Override
        public com.tetra.service.rest.Request.RequestType getRequestType() {
            return RequestType.GET;
        }

        @Override
        public String getUrl() {
            return getString(R.string.base_url)+"/sms_call";
        }

        public GetSmsAndCall addCookie(final String cookie) {
            //setHeaders("content-type", "application/xml");
            setHeaders("Cookie", "user_id=" + cookie);
            return this;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /** check if received result code
         is equal our requested code for draw permission  */
        if (requestCode == DEFAULT_SMS_REQUEST_CODE) {
            startRestoreSms();
        } else if (requestCode == REQUEST_CODE_RETURN_SMS_DEFAULT) {
            showProgress(false);
            OkActivity.start(MainActivity.this, OkActivity.DONE_RESTORE);
        }
    }

}
