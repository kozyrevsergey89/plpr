package com.protection.plpt.plpt;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.luminous.pick.Action;
import com.protection.plpt.plpt.mpkz.mpkz.method.BackupAdminReceiver;
import com.protection.plpt.plpt.mpkz.mpkz.method.ContactsMethod;
import com.protection.plpt.plpt.mpkz.mpkz.method.SmsMethod;
import com.protection.plpt.plpt.mpkz.mpkz.net.AsyncCallback;
import com.protection.plpt.plpt.mpkz.mpkz.net.AsyncRequestor;
import com.protection.plpt.plpt.mpkz.mpkz.net.request.GetFileRequest;
import com.protection.plpt.plpt.mpkz.mpkz.utils.SharedUtils;
import com.tetra.service.rest.Parameter;
import com.tetra.service.rest.Request;
import com.tetra.service.rest.Response;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

/**
 * Created by sergey on 8/29/15.
 */
public class BackupActivity extends BaseActivity {

  ViewGroup imagesLayout;
  ViewGroup contactsLayout;
  ViewGroup smsCallLogLayout;

  //old vars section
  public static final int RESULT_ENABLE = 1, RESULT_SOUND = 5;
  private static final String TAG = "123";
  private String userId;
  //    private Button backup, pictures;
  private DevicePolicyManager mDPM;
  private ComponentName mDeviceAdminSample;
  private boolean mAdminActive;
  private boolean useflag = false;
  //    private View statusView;
  public String url, soundUriString;
  private ContactLader contactLader;
  private boolean isLite = false;
  private static final int SELECT_PICTURE = 8;
  private String selectedImagePath;

  public static void start(Context context) {
    context.startActivity(new Intent(context, BackupActivity.class));
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.backup_activity_layout);

    getSupportActionBar().setDisplayShowHomeEnabled(true);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    imagesLayout = (ViewGroup) findViewById(R.id.backup_images);
    contactsLayout = (ViewGroup) findViewById(R.id.backup_contacts);
    smsCallLogLayout = (ViewGroup) findViewById(R.id.backup_sms_and_call_log);

    ((ImageView) imagesLayout.findViewById(R.id.main_item_icon)).setImageResource(
        R.drawable.copy_images);
    ((TextView) imagesLayout.findViewById(R.id.main_item_text)).setText(R.string.backup_images);

    ((ImageView) contactsLayout.findViewById(R.id.main_item_icon)).setImageResource(
        R.drawable.copy_contacts);
    ((TextView) contactsLayout.findViewById(R.id.main_item_text)).setText(R.string.backup_contacts);

    ((ImageView) smsCallLogLayout.findViewById(R.id.main_item_icon)).setImageResource(
        R.drawable.copy_contacts);
    ((TextView) smsCallLogLayout.findViewById(R.id.main_item_text)).setText(
        R.string.backup_sms_and_call_log);

    //old code section

    userId = SharedUtils.getFromShared(this, "user_id");

    contactLader = new ContactLader();
    //contactLader.execute();
    mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
    mDeviceAdminSample = new ComponentName(this, BackupAdminReceiver.class);
    mAdminActive = isActiveAdmin();

    View.OnClickListener onClickListener = new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        switch (view.getId()) {
          case R.id.backup_contacts:
            if (isLite) {
              Toast.makeText(BackupActivity.this, R.string.only_in_full, Toast.LENGTH_SHORT).show();
              break;
            }
            showProgress(true);
            new ContactLader().execute();
            //GetFileRequest fileRequest = new GetFileRequest().addCookie(userId);
            //UrlRequestCallback callback = new UrlRequestCallback(this);
            //sendRequest(callback, fileRequest);
            break;
          case R.id.backup_images:
            Intent intent = new Intent();
            //                intent.setType("image/*");
            intent.setAction(Action.ACTION_MULTIPLE_PICK);
            startActivityForResult(/*Intent.createChooser(intent,
                        "Select Picture")*/intent, SELECT_PICTURE);
            break;
          case R.id.backup_sms_and_call_log:
            showProgress(true);
            new SmsLader().execute();
            break;
        }
      }
    };

    imagesLayout.setOnClickListener(onClickListener);
    contactsLayout.setOnClickListener(onClickListener);
    smsCallLogLayout.setOnClickListener(onClickListener);
  }

  //old methods section

  public void sendBackupedFile() {
    GetFileRequest fileRequest = new GetFileRequest().addCookie(userId);
    UrlRequestCallback callback = new UrlRequestCallback(this);
    sendRequest(callback, fileRequest);
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
      case SELECT_PICTURE:
        if (resultCode == Activity.RESULT_OK) {
          String[] all_path = data.getStringArrayExtra("all_path");
          for (String path : all_path) {
            Log.i("123", path);
          }
          sendImagesTosServer(all_path);
        } else {
          Toast toast = Toast.makeText(getApplicationContext(),
              getString(R.string.picture_not_choosen_toast), Toast.LENGTH_SHORT);
          toast.show();
        }

        return;

      case RESULT_SOUND:
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  //    private void sendImagesTosServer(String[] all_path) {
  //        showProgress(true);
  //        MethodActivity.PostImageFiles postFile = new PostImageFiles();
  //        postFile.addCookie(userId);
  //        File[] files = new File[all_path.length];
  //        for (int i=0;i<all_path.length;i++) {
  //            files[i] = new File(all_path[i]);
  //        }
  ////        File file = new File(Environment.getExternalStorageDirectory(), "backup.vcf");
  //        postFile.addFiles(files);
  //        sendRequest(new AsyncCallback() {
  //            @Override
  //            public void processResponse(Response response) {
  //                showProgress(false);
  //                if (response.isSuccess()) {
  //                    Toast.makeText(MethodActivity.this, "Images backuped successfuly!", Toast.LENGTH_SHORT).show();
  //                } else {
  //                    Log.i("123", "ne proshlo" + response.getMessage());
  //                }
  //            }
  //        }, postFile);
  //    }

  int mediaCounter = 0;

  private void sendImagesTosServer(final String[] all_path) {

    showProgress(true);
    File[] files = new File[all_path.length];
    for (int i = 0; i < all_path.length; i++) {
      files[i] = new File(all_path[i]);
    }
    mediaCounter = 0;
    for (File file : files) {
      PostMediaFile postMediaFile = new PostMediaFile();
      postMediaFile.addCookie(userId);
      postMediaFile.addFile(file);
      sendRequest(new AsyncCallback() {
        @Override
        public void processResponse(Response response) {
          mediaCounter++;
          if (mediaCounter == all_path.length) {
            showProgress(false);
            if (response.isSuccess()) {
              Toast.makeText(BackupActivity.this,
                  "" + mediaCounter + " images backuped successfully! ", Toast.LENGTH_SHORT).show();
              OkActivity.start(BackupActivity.this, OkActivity.DONE_BACKUP);
            } else {
              Log.i("123", "ne proshlo" + response.getMessage());
            }
          }
          //                    if (response.isSuccess()) {
          //                        Toast.makeText(MethodActivity.this, "Images backuped successfuly! " + mediaCounter, Toast.LENGTH_SHORT).show();
          //                    } else {
          //                        showProgress(false);
          //                        Log.i("123", "ne proshlo" + response.getMessage());
          //                    }
        }
      }, postMediaFile);
    }

    //        File file = new File(Environment.getExternalStorageDirectory(), "backup.vcf");
    //        postFile.addFiles(files);
    //        sendRequest(new AsyncCallback() {
    //            @Override
    //            public void processResponse(Response response) {
    //                showProgress(false);
    //                if (response.isSuccess()) {
    //                    Toast.makeText(MethodActivity.this, "Images backuped successfuly!", Toast.LENGTH_SHORT).show();
    //                } else {
    //                    Log.i("123", "ne proshlo" + response.getMessage());
    //                }
    //            }
    //        }, postFile);
  }

  private void sendRequest(final AsyncCallback callback,
      final Request<?> request) {
    AsyncRequestor requestor = new AsyncRequestor(callback);
    requestor.execute(request);
  }

  private class UrlRequestCallback extends AsyncCallback {

    private BackupActivity activity;

    public UrlRequestCallback(final BackupActivity activity) {
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

  public class PostSmsAndCall extends Request<Serializable> {

    private static final long serialVersionUID = 6468307116960005184L;

    @Override
    public com.tetra.service.rest.Request.RequestType getRequestType() {
      return RequestType.POST;
    }

    @Override
    public String getUrl() {
      return getString(R.string.base_url)+"/sms_call";
    }

    public PostSmsAndCall addCookie(final String cookie) {
      //setHeaders("content-type", "application/xml");
      setHeaders("Cookie", "user_id=" + cookie);
      return this;
    }
  }

  public PostFile getPost() {
    return new PostFile();
  }

  private class PostCallback extends AsyncCallback {

    private BackupActivity activity;

    public PostCallback(final BackupActivity activity) {
      this.activity = activity;
    }

    @Override
    public void processResponse(final Response response) {
      Log.i("123123", response.getMessage() + "");
      Log.i("123123", response.getResultCode() + "");
      Log.i("123123", response.getStatus().name());
      Log.i("123123", response.getStreamString() + "");
      activity.showProgress(false);
      OkActivity.start(BackupActivity.this, OkActivity.DONE_BACKUP);
      activity = null;
    }
  }

  public class PostFile extends Request<Serializable> {

    private static final long serialVersionUID = 6468307116960005184L;

    @Override
    public com.tetra.service.rest.Request.RequestType getRequestType() {
      return RequestType.MULTIPART;
    }

    @Override
    public String getUrl() {
      return BackupActivity.this.url;
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

  public class PostImageFiles extends Request<Serializable> {

    private static final long serialVersionUID = 6468307116960005184L;

    @Override
    public com.tetra.service.rest.Request.RequestType getRequestType() {
      return RequestType.MULTIPART;
    }

    @Override
    public String getUrl() {
      return getString(R.string.base_url)+"/imagetest";
    }

    public PostImageFiles addFiles(final File[] files) {
      setFiles(files);
      return this;
    }

    public PostImageFiles addCookie(final String cookie) {
      //setHeaders("content-type", "application/xml");
      setHeaders("Cookie", "user_id=" + cookie);
      return this;
    }
  }

  public class PostImageFilesZipOneFile extends Request<Serializable> {

    private static final long serialVersionUID = 6468307116960005184L;

    @Override
    public com.tetra.service.rest.Request.RequestType getRequestType() {
      return RequestType.MULTIPART;
    }

    @Override
    public String getUrl() {
      return getString(R.string.base_url)+"/imagetest";
    }

    public PostImageFilesZipOneFile addFiles(final File[] files) {
      setFiles(files);
      return this;
    }

    public PostImageFilesZipOneFile addCookie(final String cookie) {
      //setHeaders("content-type", "application/xml");
      setHeaders("Cookie", "user_id=" + cookie);
      return this;
    }
  }

  private class ContactLader extends AsyncTask<Void, Void, File> {

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      (BackupActivity.this).showProgress(true);
    }

    @Override
    protected File doInBackground(Void... params) {
      ContactsMethod method = new ContactsMethod();
      try {
        File file = method.getVcardFile(BackupActivity.this);

        return file;
      } catch (final IOException e) {
        Log.e("123123", "Unable to get vcard file");
        return null;
      }
    }

    @Override
    protected void onPostExecute(final File result) {
      super.onPostExecute(result);

      if (BackupActivity.this != null) {
        ((BackupActivity) BackupActivity.this).sendBackupedFile();

        //                BackupActivity.this = null;
      }
      if (result != null) {
        Log.i("123123", result.getAbsolutePath());
      } else {
        Toast.makeText(BackupActivity.this, "No contacts to backup", Toast.LENGTH_SHORT).show();
        ((BackupActivity) BackupActivity.this).showProgress(false);
        Log.e("123123", "No file");
      }
    }
  }

  private class SmsLader extends AsyncTask<Void, Void, String> {

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      (BackupActivity.this).showProgress(true);
    }

    @Override
    protected String doInBackground(Void... params) {
      return SmsMethod.storeSms(BackupActivity.this);
    }

    @Override
    protected void onPostExecute(final String result) {
      super.onPostExecute(result);
      showProgress(false);
      PostSmsAndCall post = new PostSmsAndCall();
      post.addCookie(userId);
      post.setPostEntities("sms_call", result);
      sendRequest(new AsyncCallback() {
        @Override public void processResponse(Response response) {
          if (response.isSuccess()) {
            Toast.makeText(BackupActivity.this, "" + " sms and call log stored successfully! ",
                Toast.LENGTH_SHORT).show();
            OkActivity.start(BackupActivity.this, OkActivity.DONE_BACKUP);
          } else {
            Log.i("123", "ne proshlo sms backup" + response.getMessage());
          }
        }
      }, post);
    }
  }
}
