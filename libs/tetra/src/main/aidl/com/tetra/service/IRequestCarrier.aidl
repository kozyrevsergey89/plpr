package com.tetra.service;

import com.tetra.service.rest.Carrier;
import com.tetra.service.IRequestCallback;

/**
* AIDL representation for service binder.
* @author David Mayboroda (david.ftzi@gmail.com).
*/
interface IRequestCarrier {
	
	/** 
	* Method for request fetching from activity.
	* @param Carrier with request.
	* @param IRequestCallback callback for response handling.
	*/
	void doRequest(in Carrier carrier, in IRequestCallback callback);
	
}