package com.tetra;


import java.io.Serializable;

import com.tetra.service.IRequestCarrier;
import com.tetra.service.RestService.RequestCarrier;
import com.tetra.service.rest.Carrier;
import com.tetra.service.rest.Parameter;
import com.tetra.service.rest.Request;
import com.tetra.service.rest.RequestCallback;
import com.tetra.service.rest.Response;

import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;

public class MainActivity extends Activity {
	
	private IRequestCarrier carrier;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		bindService(new Intent(IRequestCarrier.class.getName()), connection, BIND_AUTO_CREATE);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		try {
			Callback callback = new Callback();
			LoginRequest request = new LoginRequest();
			Carrier fetcher = new Carrier(request);
			//if(carrier != null) {
				carrier.doRequest(fetcher, callback);
			//}
		} catch (RemoteException e) {
			Log.e("ERROR", e.getMessage() + " - REMOTE EXCEPTION");
		}
	}
	
	
	@Override
	protected void onDestroy() {
		unbindService(connection);
		connection = null;
		carrier = null;
		super.onDestroy();
	}
	
	public ServiceConnection connection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(final ComponentName name) {
			carrier = null;
		}
		
		@Override
		public void onServiceConnected(final ComponentName name, final IBinder service) {
			Log.i("123123", "onServiceConnected");
			carrier = RequestCarrier.asInterface(service);
		}
	};
	
	
	public class LoginRequest extends Request<Serializable> {

		/** Serial UID code.*/
		private static final long serialVersionUID = 4598983572839238019L;
		
		public LoginRequest() {
			super();
			setPostEntities("username", "skozyrev");
			setPostEntities("password", "Pa$$w0rd");
		}

		@Override
		public com.tetra.service.rest.Request.RequestType getRequestType() {
			return RequestType.POST;
		}

		@Override
		public String getUrl() { return "http://192.168.1.235:8080/login"; }
		
	}
	
	public class Callback extends RequestCallback {

		@Override
		public void processResponse(final Response model) {
			if (model != null) {
				if (model.getHeaders() != null && !model.getHeaders().isEmpty()) {
					for (Parameter parameter : model.getHeaders()) {
						Log.i("123123", parameter.getName() + " : " + parameter.getName());
					}
				}
			} else {
				Log.e("ERROR", "MODEL IS NULL");
			}
		}
		
	}
	
}
