package com.protection.plpt.plpt.mpkz.mpkz.net.request;

import java.io.Serializable;

import com.protection.plpt.plpt.App;
import com.protection.plpt.plpt.R;
import com.tetra.service.rest.Request;

public class GetBackFile extends Request<Serializable>{

	private static final long serialVersionUID = 70844032293693582L;

	@Override
	public com.tetra.service.rest.Request.RequestType getRequestType() {
		return RequestType.GET;
	}

	@Override
	public String getUrl() { return App.getAppContext().getString(R.string.base_url)+"/backserve"; }
	
	public GetBackFile addCookie(final String cookie) {
		setHeaders("Cookie", "user_id=" + cookie);
		return this;
	}

}
