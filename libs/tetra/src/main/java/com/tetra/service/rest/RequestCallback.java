package com.tetra.service.rest;

import java.io.Serializable;

import android.os.Handler;
import android.os.RemoteException;

import com.tetra.service.IRequestCallback;

/**
 * Base request callback. It will occurs in UI thread (depends on handler creation).
 * Also it contains a model which already parsed.
 * You can cast your {@link Serializable} model to separate class. 
 * @author David Mayboroda(david.ftzi@gmail.com)
 * 
 * TO DO: Create a logic for error responses.
 */
public abstract class RequestCallback extends IRequestCallback.Stub{
	
	@Override
	public void onResponse(final Response response) throws RemoteException {
		final Handler handler = new Handler();
		handler.post(new Runnable() {
			@Override
			public void run() {
				processResponse(response);
			}
		});
	}
	
	public abstract void processResponse(final Response model);

}
