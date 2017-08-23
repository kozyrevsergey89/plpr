package com.protection.plpt.plpt;

import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.protection.plpt.plpt.mpkz.mpkz.method.BackupAdminReceiver;
import com.protection.plpt.plpt.mpkz.mpkz.method.InfoMethod;
import com.protection.plpt.plpt.mpkz.mpkz.net.AsyncCallback;
import com.protection.plpt.plpt.mpkz.mpkz.net.AsyncRequestor;
import com.protection.plpt.plpt.mpkz.mpkz.net.request.GCMRequest;
import com.protection.plpt.plpt.mpkz.mpkz.net.request.LoginRequest;
import com.protection.plpt.plpt.mpkz.mpkz.net.request.RegRequest;
import com.protection.plpt.plpt.mpkz.mpkz.utils.SharedUtils;
import com.tetra.service.rest.Parameter;
import com.tetra.service.rest.Response;

import java.util.List;


public class LoginActivity extends BaseActivity {

    public static final String EXTRA_LOGIN = "mpkz.authenticatordemo.extra.LOGIN";
    public static final String SENDER_ID = "777289626036";
    public static final String COOKIE_ID = "USER_ID";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final int MY_PERMISSIONS_REQUEST = 2;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private AsyncRequestor requestTask = null;
    private com.protection.plpt.plpt.LoginActivity.LoginCallback callback = null;
    private LoginRequest loginRequest = null;
    private com.protection.plpt.plpt.LoginActivity.ServiceBroadcast broadcastReceiver;
    private com.protection.plpt.plpt.LoginActivity.GCMCallback gcmCallback = null;

    // Values for email and password at the time of the login attempt.
    private String mLogin;
    private String mPassword;
//    private String mVerifyPassword;
//    private String mEmail;

    // UI references.
    private EditText mLoginView;
    private EditText mPasswordView;
//    private EditText mEmailView;
//    private EditText verifyPass;
//    private View mLoginFormView;
//    private View mLoginStatusView;
//    private TextView mLoginStatusMessageView;

//    private TextView userTerms, oneDevice;

    private Button registrationButton;
    private Button startRegister;
    private Button signIn;
    private TextView register;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }


    private Button loginBtn;
    private EditText usernameEdt;
    private EditText passwordEdt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //check if agreed with terms of use
        String checkStr = SharedUtils.getFromShared(this, "TermsAgreed");

//		if ( checkStr == null) {
//			Intent intent = new Intent(getApplicationContext(), TermsOfUseActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			startActivity(intent);
//			finish();
//		}


//        userTerms = (TextView) findViewById(R.id.user_terms);
//        oneDevice = (TextView) findViewById(R.id.two_users);

        // Set up the login form.
//        mLogin = getIntent().getStringExtra(EXTRA_LOGIN);
        mLoginView = (EditText) findViewById(R.id.login_edit);
//        mLoginView.setText(mLogin);

//        mEmailView = (EditText) findViewById(R.id.email);
//        verifyPass = (EditText) findViewById(R.id.verify_pass);

//        register = (TextView) findViewById(R.id.reg_text);
//        String regText = getString(R.string.or);
//        register.setText(Html.fromHtml("<a href='app://backup'>" + regText + "</a>"));

//        startRegister = (Button) findViewById(R.id.push_to_reg_button);
//        startRegister.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                attemptLogin();
//            }
//        });

        signIn = (Button) findViewById(R.id.login_btn);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkConnected()) {
                    attemptLogin();
                } else {
                    Toast.makeText(LoginActivity.this, R.string.no_connection, Toast.LENGTH_SHORT).show();
                }
            }
        });

//        registrationButton = (Button) findViewById(R.id.register_button);
//        registrationButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
//            }
//        });

        mPasswordView = (EditText) findViewById(R.id.password_edit);
        mPasswordView
                .setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int id,
                                                  KeyEvent keyEvent) {
                        if (id == R.id.login_edit || id == EditorInfo.IME_NULL) {
                            attemptLogin();
                            return true;
                        }
                        return false;
                    }
                });

//        mLoginFormView = findViewById(R.id.login_form);
//        mLoginStatusView = findViewById(R.id.login_status);
//        mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

        if (this instanceof LoginActivity) {
            gcmCallback = new GCMCallback(this);
            broadcastReceiver = new ServiceBroadcast(gcmCallback);
            registerReceiver(broadcastReceiver, new IntentFilter(GCMIntentService.MESSAGE_ACTION));
        }


        //new code
        loginBtn = (Button) findViewById(R.id.login_btn);
        usernameEdt = (EditText) findViewById(R.id.login_edit);
        passwordEdt = (EditText) findViewById(R.id.password_edit);
//        loginBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                doLogin();
//            }
//        });
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    String newMessage = intent.getExtras().getString(GCMIntentService.ID_MESSAGE);
                    SharedUtils.writeToShared(context, "reg_id", newMessage);
                    AsyncRequestor requestor = new AsyncRequestor(new GCMCallback(LoginActivity.this));
                    String cookie = SharedUtils.getFromShared(context, "user_id");
                    GCMRequest request = new GCMRequest().addRegId(newMessage)
                            .addCookie(cookie)
                            .addDeviceId(new InfoMethod(context).getImey());
                    requestor.execute(request);
                } else {
                }
            }
        };


        int hasWriteStoragePermissions = ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE");//
        int hasReadContactsPermissions = ContextCompat.checkSelfPermission(this, "android.permission.READ_CONTACTS");//
        int hasGetAccountsPermissions = ContextCompat.checkSelfPermission(this, "android.permission.GET_ACCOUNTS");
        int hasAccessLocationPermissions = ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION");//
        int hasCameraPermissions = ContextCompat.checkSelfPermission(this, "android.permission.CAMERA");//
        int hasRecordAudioPermissions = ContextCompat.checkSelfPermission(this, "android.permission.RECORD_AUDIO");//
        int hasREAD_PHONE_STATEPermissions = ContextCompat.checkSelfPermission(this, "android.permission.READ_PHONE_STATE");
        int hasREAD_CALL_LOGPermissions = ContextCompat.checkSelfPermission(this, "android.permission.READ_CALL_LOG");
        int hasWRITE_CALL_LOGPermissions = ContextCompat.checkSelfPermission(this, "android.permission.WRITE_CALL_LOG");//
        int hasREAD_SMSPermissions = ContextCompat.checkSelfPermission(this, "android.permission.READ_SMS");//
        int hasWRITE_SMSPermissions = ContextCompat.checkSelfPermission(this, "android.permission.WRITE_SMS");//
        if (hasWriteStoragePermissions != PackageManager.PERMISSION_GRANTED ||
                hasReadContactsPermissions != PackageManager.PERMISSION_GRANTED ||
                hasGetAccountsPermissions != PackageManager.PERMISSION_GRANTED ||
                hasAccessLocationPermissions != PackageManager.PERMISSION_GRANTED ||
                hasCameraPermissions != PackageManager.PERMISSION_GRANTED ||
                hasRecordAudioPermissions != PackageManager.PERMISSION_GRANTED ||
                hasREAD_PHONE_STATEPermissions != PackageManager.PERMISSION_GRANTED ||
                hasREAD_CALL_LOGPermissions != PackageManager.PERMISSION_GRANTED ||
                hasWRITE_CALL_LOGPermissions != PackageManager.PERMISSION_GRANTED ||
                hasREAD_SMSPermissions != PackageManager.PERMISSION_GRANTED ||
                hasWRITE_SMSPermissions != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_CONTACTS",
                            "android.permission.GET_ACCOUNTS", "android.permission.ACCESS_FINE_LOCATION",
                            "android.permission.CAMERA", "android.permission.RECORD_AUDIO", "android.permission.READ_PHONE_STATE",
                            "android.permission.READ_CALL_LOG", "android.permission.WRITE_CALL_LOG", "android.permission.READ_SMS",
                            "android.permission.WRITE_SMS"},
                    MY_PERMISSIONS_REQUEST);
        } else {
            checkDrawOverlayPermission();
        }

        // Show info about Device Administrator Permissions request
        DevicePolicyManager mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName mDeviceAdminSample = new ComponentName(this, BackupAdminReceiver.class);
        if(!mDPM.isAdminActive(mDeviceAdminSample)) {
            //View dialogView = getLayoutInflater().inflate(R.layout.admin_dialog, null, false);
            //WebView dialogWebView = (WebView) dialogView.findViewById(R.id.web_view);
            WebView dialogWebView = new WebView(this);
            dialogWebView.getSettings().setJavaScriptEnabled(true);
            dialogWebView.loadUrl("http://smprotect.com/dap.html");

            AlertDialog alertDialog =
                new AlertDialog.Builder(LoginActivity.this, R.style.DialogeTheme).
                    setView(dialogWebView).
                    setNegativeButton("Ok", null).create();
            alertDialog.show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED
                    && grantResults[3] == PackageManager.PERMISSION_GRANTED
                    && grantResults[4] == PackageManager.PERMISSION_GRANTED
                    && grantResults[5] == PackageManager.PERMISSION_GRANTED
                    && grantResults[6] == PackageManager.PERMISSION_GRANTED
                    && grantResults[7] == PackageManager.PERMISSION_GRANTED
                    && grantResults[8] == PackageManager.PERMISSION_GRANTED
                    && grantResults[9] == PackageManager.PERMISSION_GRANTED
                    && grantResults[10] == PackageManager.PERMISSION_GRANTED) {


                // permission was granted, yay! Do the
                // contacts-related task you need to do.

            } else {
                Toast.makeText(this, R.string.app_will_not_work_properly, Toast.LENGTH_LONG).show();
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }
            checkDrawOverlayPermission();
            return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
//                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i("123123", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private void doLogin() {
        MainActivity.start(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //old code

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        /*
        if (requestTask != null) {
			return;
		}
		*/

        // Reset errors.
        mLoginView.setError(null);
        mPasswordView.setError(null);
//        mEmailView.setError(null);
//        verifyPass.setError(null);

        // Store values at the time of the login attempt.
        mLogin = mLoginView.getText().toString();
        mPassword = mPasswordView.getText().toString();
//        if(mEmailView.getVisibility() == View.VISIBLE) {
//            mEmail = mEmailView.getText().toString();
//        }
//
//        if (verifyPass.getVisibility() == View.VISIBLE) {
//            mVerifyPassword = verifyPass.getText().toString();
//        }


        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(mPassword)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(mLogin)) {
            mLoginView.setError(getString(R.string.error_field_required));
            focusView = mLoginView;
            cancel = true;
        }

//        if(mEmailView.getVisibility() == View.VISIBLE && TextUtils.isEmpty(mEmail)) {
//            mEmailView.setError(getString(R.string.error_field_required));
//            focusView = mEmailView;
//            cancel = true;
//        }
//
//        if (verifyPass.getVisibility() == View.VISIBLE && TextUtils.isEmpty(mVerifyPassword)) {
//            verifyPass.setError(getString(R.string.error_field_required));
//            focusView = verifyPass;
//            cancel = true;
//        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            //focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            if (this instanceof LoginActivity
//                    && mEmailView.getVisibility() != View.VISIBLE
//                    && verifyPass.getVisibility() != View.VISIBLE
                    ) {
//                mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
                callback = new LoginCallback(this);
                requestTask = new AsyncRequestor(callback);
                loginRequest = new LoginRequest().setLoginAndPass(mLogin, mPassword);

                showProgress(true);
                requestTask.execute(loginRequest);
            } else {
                if (true/*mPassword.equals(mVerifyPassword)*/) {
                    RegistrationCallback callback = new RegistrationCallback(this);
                    requestTask = new AsyncRequestor(callback);
                    RegRequest req = new RegRequest().setParams(mLogin, mPassword, "email", "verifypassword"/*mEmail, mVerifyPassword*/);
                    requestTask.execute(req);
                    showProgress(true);
                } else {
                    Toast.makeText(this, "Unable to verify password", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private static class RegistrationCallback extends AsyncCallback {

        private LoginActivity context;

        public RegistrationCallback(final LoginActivity context) {
            this.context = context;
        }

        @Override
        public void processResponse(final Response response) {
            Log.i("123123", response.getMessage() + "");
            Log.i("123123", response.getStreamString() + "");
            Log.i("123123", response.getResultCode() + "");
            context.showProgress(false);
            if (response.isSuccess()) {
                context.finish();
                context.startActivity(new Intent(context, LoginActivity.class));
            }
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
//    private void showProgress(final boolean show) {
//        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
//        // for very easy animations. If available, use these APIs to fade-in
//        // the progress spinner.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
//            int shortAnimTime = getResources().getInteger(
//                    android.R.integer.config_shortAnimTime);
//
//            mLoginStatusView.setVisibility(View.VISIBLE);
//            mLoginStatusView.animate().setDuration(shortAnimTime)
//                    .alpha(show ? 1 : 0)
//                    .setListener(new AnimatorListenerAdapter() {
//                        @Override
//                        public void onAnimationEnd(Animator animation) {
//                            mLoginStatusView.setVisibility(show ? View.VISIBLE
//                                    : View.GONE);
//                        }
//                    });
//
//            mLoginFormView.setVisibility(View.VISIBLE);
//            mLoginFormView.animate().setDuration(shortAnimTime)
//                    .alpha(show ? 0 : 1)
//                    .setListener(new AnimatorListenerAdapter() {
//                        @Override
//                        public void onAnimationEnd(Animator animation) {
//                            mLoginFormView.setVisibility(show ? View.GONE
//                                    : View.VISIBLE);
//                        }
//                    });
//        } else {
//            // The ViewPropertyAnimator APIs are not available, so simply show
//            // and hide the relevant UI components.
//            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
//            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//        }
//    }

    public class LoginCallback extends AsyncCallback {

        private Context context;

        public LoginCallback(final Context context) {
            super();
            this.context = context;
        }

        @Override
        public void processResponse(final Response response) {
            if (response.isSuccess()) {
                if (!response.getCookies().isEmpty()) {
                    for (Parameter cookie : response.getCookies()) {
                        if ("user_id".equals(cookie.getName())) {
                            SharedUtils.writeToShared(context, "user_id", cookie.getValue());
//                            GCMRegistrar.checkManifest(context);
//                            //final String regId = GCMRegistrar.getRegistrationId(context);
//                            //if (regId.equals("")) {
//                            GCMRegistrar.getRegistrationId(context);
//                            GCMRegistrar.register(context, SENDER_ID);

                            if (checkPlayServices()) {
//                                Start IntentService to register this application with GCM.
                                Intent intent = new Intent(LoginActivity.this, RegistrationIntentService.class);
                                startService(intent);
                            }
                            //}
                            /*
                            else if (GCMRegistrar.isRegisteredOnServer(context)) {
								context.startActivity(new Intent(context, MethodActivity.class));
								((LoginActivity)context).showProgress(false);
								((LoginActivity)context).finish();
								context = null;
								return;
							}
							*/
                        }
                    }
                } else {
                    ((LoginActivity) context).showProgress(false);
                    Toast.makeText(context, R.string.login_error, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, R.string.login_error, Toast.LENGTH_SHORT).show();
                //context.startActivity(new Intent(context, MethodActivity.class));
                //((LoginActivity)context).finish();
                ((LoginActivity) context).showProgress(false);
            }
            //((LoginActivity)context).showProgress(false);
            //Toast.makeText(context, R.string.login_error, Toast.LENGTH_SHORT).show();
            context = null;
        }

    }

    public static class ServiceBroadcast extends BroadcastReceiver {

        private GCMCallback callback;

        public ServiceBroadcast(final GCMCallback callback) {
            this.callback = callback;
        }

        @Override
        public void onReceive(final Context context, final Intent intent) {
            Log.i("123123", "servicebroadcast receiver after gcm registration");
            GCMRegistrar.setRegisteredOnServer(context, true);
            String newMessage = intent.getExtras().getString(GCMIntentService.ID_MESSAGE);
            SharedUtils.writeToShared(context, "reg_id", newMessage);
            AsyncRequestor requestor = new AsyncRequestor(callback);
            String cookie = SharedUtils.getFromShared(context, "user_id");
            GCMRequest request = new GCMRequest().addRegId(newMessage)
                    .addCookie(cookie)
                    .addDeviceId(new InfoMethod(context).getImey());
            requestor.execute(request);
        }

    }

    public static class GCMCallback extends AsyncCallback {

        private LoginActivity activity;

        public GCMCallback(final LoginActivity activity) {
            this.activity = activity;
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
                                SharedUtils.deleteFromShared(activity, "user_id");
                                activity.showProgress(false);
                                Toast.makeText(activity, R.string.second_user, Toast.LENGTH_SHORT).show();
                                return;
                            } else {
                                break;
                            }
                        } else {
                            continue;
                        }
                    }
                }

                if (!response.getHeaders().isEmpty()) {
                    List<Parameter> headers = response.getHeaders();
                    for (Parameter param : headers) {
                        if ("version".equals(param.getName())) {
                            String value = param.getValue();
                            SharedUtils.writeToShared(activity, "version", value);
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
                            Toast.makeText(activity,
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
                    Intent intent = new Intent(activity, MainActivity.class);
                    intent.putExtra("use_full", useflag);
                    activity.startActivity(intent);
                    activity.finish();
                } else {
                    Toast.makeText(activity, response.getMessage() + "", Toast.LENGTH_SHORT).show();
                }

            }

            activity.showProgress(false);
        }

    }

    @Override
    protected void onDestroy() {
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
        if (requestTask != null) {
            requestTask.cancel(true);
        }
        requestTask = null;
        callback = null;
        loginRequest = null;
        gcmCallback = null;
        super.onDestroy();
    }

    /**
     * code to post/handler request for permission
     */
    public final static int REQUEST_CODE = 17;

    public void checkDrawOverlayPermission() {
        /** check if we already  have permission to draw over other apps */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                /** if not construct intent to request permission */
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                /** request permission via start activity for result */
                startActivityForResult(intent, REQUEST_CODE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /** check if received result code
         is equal our requested code for draw permission  */
        if (requestCode == REQUEST_CODE) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    // continue here - permission was granted
                } else {
                    Toast.makeText(this, R.string.app_will_not_work_properly, Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
