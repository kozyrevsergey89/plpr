package com.protection.plpt.plpt;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.protection.plpt.plpt.OkActivity;
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

public class GCMIntentService extends GCMBaseIntentService {

	public static final String REG_ID = "reg_id";
	public static final String ID_MESSAGE = "id_broadcast_message";
	public static final String MESSAGE_ACTION = "com.mpkz.BROADCAST_MESSAGE";
	public static final String SENDER_ID = "978784319712";
	
	public GCMIntentService() {
		super(SENDER_ID);
	}
	
	@Override
	protected void onError(final Context context, final String registrationId) {
		Log.i("123123", "onError - " + registrationId);
	}

	@Override
	protected void onMessage(final Context context, final Intent intent) {
        Log.i("123123", "message arrived - " );
		if(intent.hasExtra("action")) {
			Request<?> request;
			String action = intent.getStringExtra("action");
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
						.addCookie(SharedUtils.getFromShared(context, "user_id"));
				sendRequest(request);
				infoMethod.destroy();
			} else if ("wipe".equals(action)) {
				String adminFlag = SharedUtils.getFromShared(this, "ADMIN");
				String cookie = SharedUtils.getFromShared(context, "user_id");
				if (adminFlag != null && !adminFlag.isEmpty()) {
					request = new AdminFlagRequest().addCookie(cookie).setAdminFlag(true);
					sendRequest(request);
					AdminMethod.doWip(context);
				} else {
					request = new AdminFlagRequest().addCookie(cookie).setAdminFlag(false);
					sendRequest(request);
				}
			} else if ("find_phone".equals(action)) {
				WakeLocker.wakeUp(context);
                OkActivity.start(this, OkActivity.DONE_FOUND);
			} else if ("deregister".equals(action)) {
				SharedUtils.deleteFromShared(context, "user_id");
			}
		}
	}
	
	@Override
	protected void onRegistered(final Context context, final String registrationId) {
        Log.i("123123", "registered - " + registrationId);
		SharedUtils.writeToShared(context, REG_ID, registrationId);
		displayMessage(context, registrationId);
	}

	@Override
	protected void onUnregistered(final Context context, final String registrationId) {
		SharedUtils.writeToShared(context, REG_ID, registrationId);
		displayMessage(context, registrationId);
		if(GCMRegistrar.isRegisteredOnServer(context)) {
			//GCMRegistrar.setRegisteredOnServer(context, false);
		}
	}
	
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
