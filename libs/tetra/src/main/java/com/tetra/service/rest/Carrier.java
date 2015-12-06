package com.tetra.service.rest;

import java.io.File;

import com.tetra.service.rest.Request.RequestType;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * {@link Parcelable} wrapper for {@link Request} object.
 * Will be deserialized on service side process. 
 * @author David Mayboroda (david.ftzi@gmail.com)
 */
public class Carrier implements Parcelable{

	/**Request instance.*/
	private Request<?> request;

	public Carrier(final Request<?> request) {
		this.request = request;
		this.request.setClassType(request.getModelClass());
	}
	
	private Carrier(final Parcel parcel) {
		this.request = (Request<?>)parcel.readSerializable();
		this.request.setUrlParams(parcel.createTypedArrayList(Parameter.CREATOR));
		this.request.setHeaders(parcel.createTypedArrayList(Parameter.CREATOR));
		this.request.setPostEntities(parcel.createTypedArrayList(Parameter.CREATOR));
		this.request.setCookies(parcel.createTypedArrayList(Parameter.CREATOR));
		this.request.setRequestType(RequestType.byCode(parcel.readInt()));
		this.request.setUrl(parcel.readString());
		this.request.setApiType(parcel.readInt());
		this.request.setClassType((Class<?>)parcel.readSerializable());
		this.request.setBody(parcel.createByteArray());
		this.request.setFile((File)parcel.readSerializable());
	}
	
	public void setRequest(final Request<?> request) {
		this.request = request;
	}
	
	public Request<?> getRequest() { return request; }
	
	@Override
	public int describeContents() { return 0; }

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		dest.writeSerializable(request);
		dest.writeTypedList(request.getUrlParams());
		dest.writeTypedList(request.getHeaders());
		dest.writeTypedList(request.getPostEntities());
		dest.writeTypedList(request.getCookies());
		dest.writeInt(request.getRequestType().ordinal());
		dest.writeString(request.getUrl());
		dest.writeInt(request.getApiType());
		dest.writeSerializable(request.getModelClass());
		dest.writeByteArray(request.getBody());
		dest.writeSerializable(request.getFile());
	}
	
	/**
	 * Carrier parcelable creator.
	 * @author David Mayboroda (david.ftzi@gmail.com)
	 * {@link Parcelable}
	 */
	public static final Parcelable.Creator<Carrier> CREATOR = new Creator<Carrier>() {
		
		@Override
		public Carrier[] newArray(final int size) {
			return new Carrier[size];
		}
		
		@Override
		public Carrier createFromParcel(final Parcel source) {
			return new Carrier(source);
		}
	};

}
