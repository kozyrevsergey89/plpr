package com.tetra.service;
import com.tetra.service.rest.Response;

/**
* AIDL representation for response callback.
* @author David Mayboroda (david.ftzi@gmail.com).
*/
interface IRequestCallback {

	void onResponse(in Response response);

}