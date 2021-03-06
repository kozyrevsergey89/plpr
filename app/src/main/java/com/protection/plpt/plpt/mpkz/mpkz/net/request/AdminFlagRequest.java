package com.protection.plpt.plpt.mpkz.mpkz.net.request;

import java.io.Serializable;

import com.protection.plpt.plpt.App;
import com.protection.plpt.plpt.R;
import com.tetra.service.rest.Request;

public class AdminFlagRequest extends Request<Serializable>{

	/**Generated serial UID .*/
	private static final long serialVersionUID = -8308247999857198827L;

	public AdminFlagRequest() {
		super();
		setHeaders("content-type", "application/x-www-form-urlencoded");
	}
	
	@Override
	public com.tetra.service.rest.Request.RequestType getRequestType() {
		return RequestType.POST;
	}

	@Override
	public String getUrl() {
		return App.getAppContext().getString(R.string.base_url)+"/isadmin";
	}
	
	public AdminFlagRequest addCookie(final String cookie) {
		setHeaders("Cookie", "user_id=" + cookie);
		return this;
	}
	
	public AdminFlagRequest setAdminFlag(final boolean flag) {
		setHeaders("admin", String.valueOf(flag));
		return this;
	}

}
