package com.protection.plpt.plpt.mpkz.mpkz.net.request;

import java.io.Serializable;

import com.tetra.service.rest.Request;

public class LoginRequest extends Request<Serializable>{

	private static final long serialVersionUID = -4753854739933838492L;

	public LoginRequest() {
		super();
		setHeaders("content-type", "application/x-www-form-urlencoded");
	}
	
	@Override
	public com.tetra.service.rest.Request.RequestType getRequestType() {
		return RequestType.POST;
	}

	@Override
	public String getUrl() {
		return "https://plpr-2015.appspot.com/login";
	}
	
	public LoginRequest setLoginAndPass(final String login, final String password) {
		setPostEntities("username", login);
		setPostEntities("password", password);
		return this;
	}

}
