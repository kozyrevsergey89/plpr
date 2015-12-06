package com.tetra.service;

import com.tetra.service.rest.Carrier;
import com.tetra.service.rest.Request;
import com.tetra.service.rest.RequestMethod;
import com.tetra.service.rest.Response;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

/**
 * {@link Service} for handling requests
 * @author David Mayboroda(david.ftzi@gmail.com)
 */
public class RestService extends Service{

	/**
	 * {@link RequestCarrier} instance.
	 * Will be used as {@link Binder} in onBind() method.
	 */
	private RequestCarrier requestCarrier;

	@Override
	public IBinder onBind(final Intent intent) {
		requestCarrier = new RequestCarrier();
		return requestCarrier;
	}
	
	@Override
	public boolean onUnbind(final Intent intent) {
		requestCarrier = null;
		return super.onUnbind(intent);
	}
	
	/**
	 * {@link IRequestCarrier} realization.
	 * Request handling entity;
	 * @author David Mayboroda (david.ftzi@gmail.com)
	 */
	public static class RequestCarrier extends IRequestCarrier.Stub{

		@Override
		public void doRequest(final Carrier carrier, final IRequestCallback callback)
				throws RemoteException {
			final AsyncRequestor requestor = new AsyncRequestor(callback);
			requestor.executeOnExecutor(AsyncRequestor.THREAD_POOL_EXECUTOR, carrier.getRequest());
		}
	}
	
	/**
	 * {@link AsyncTask} for parallel requests.
	 * @author David Mayboroda (david.ftzi@gmail.com)
	 */
	public static class AsyncRequestor extends AsyncTask<Request<?>, Void, Response> {

		/**{@link IRequestCallback} callback for main ui thread.*/
		private IRequestCallback callback;
		
		public AsyncRequestor(final IRequestCallback callback) {
			this.callback = callback;
		}
		
		@Override
		protected Response doInBackground(Request<?>... params) {
			Request<?> request = params[0];
			RequestMethod requestMethod;
			Response response = new Response();
			if(request.getApiType() == -1) {
				requestMethod = new RequestMethod();
			} else {
				requestMethod = new RequestMethod(request.getApiType());
			}
			response = requestMethod.doRequset(request);
			return response;
		}
		
		@Override
		protected void onPostExecute(final Response result) {
			super.onPostExecute(result);
			try {
				callback.onResponse(result);
			} catch (final RemoteException e) {
				Log.e("ERROR", e.getMessage() + "");
			} finally {
				callback = null;
			}
		}
		
	}
	
}