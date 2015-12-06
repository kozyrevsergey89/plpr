package com.protection.plpt.plpt.mpkz.mpkz.net.request;

import java.io.Serializable;

import com.tetra.service.rest.Request;

public class GetFileRequest extends Request<Serializable>{
	
	private static final long serialVersionUID = 2179938409570709975L;

	@Override
	public com.tetra.service.rest.Request.RequestType getRequestType() {
		return RequestType.GET;
	}

	@Override
	public String getUrl() { return "https://plpr-2015.appspot.com/backgeturl"; }
	
	public GetFileRequest addCookie(final String cookie) {
		setHeaders("Cookie", "user_id=" + cookie);
		return this;
	}
	
}
