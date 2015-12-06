package com.tetra.service.rest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Basic response model
 * @author David Mayboroda (david.ftzi@gmail.com)
 */
public class Response implements Parcelable{

	/**Response message (can be error type or null).*/
	private String message;
	/**Result code.*/
	private int resultCode;
	/**Response general info.*/
	private String stream;
	/**Response status.*/
	private ResponseStatus status;
	/**Response headers.*/
	private List<Parameter> headers;
	/**Response cookies.*/
	private List<Parameter> cookies;
	/**Response file.*/
	//TO DO: create parcelable for file
	private File file;
	/**Parsed model.*/
	//TO DO: setters and getter for model.
	//Also all parceling logic.
	//private Serializable model;
	
	public Response(final String message,
					final int resultCode,
					final String stream,
					final ResponseStatus status){
		init();
		this.message = message;
		this.resultCode = resultCode;
		this.stream = stream;
		this.status = status;
	}
	
	public Response(final Parcel parcel) {
		init();
		this.message = parcel.readString();
		this.resultCode = parcel.readInt();
		this.status = ResponseStatus.byCode(parcel.readInt());
		this.stream = parcel.readString();
		this.file = (File)parcel.readSerializable(); // ClassCastException can be thrown
		parcel.readTypedList(headers, Parameter.CREATOR);
		parcel.readTypedList(cookies, Parameter.CREATOR);
	}
	
	private void init() {
		headers = new ArrayList<Parameter>();
		cookies = new ArrayList<Parameter>();
	}
	
	public Response() { init(); }
	
	public String getMessage() { return message; }

	public void setMessage(final String message) {
		this.message = message;
	}

	public int getResultCode() { return resultCode; }
	
	public void setResultCode(final int resultCode) {
		this.resultCode = resultCode;
	}
	
	public void setStreamString(final String stream) {
		this.stream = stream;
	}
	
	public String getStreamString() { return stream; }
	
	public void setStatus(final ResponseStatus status) {
		this.status = status;
	}
	
	public boolean isSuccess() {
		return status != null && status == ResponseStatus.SUCCESS;
	}
	
	public ResponseStatus getStatus() { return status; }
	
	public void setHeaders(final List<Parameter> headers) {
		this.headers = headers;
	}
	
	public void setHeader(final String name, final String value) {
		headers.add(new Parameter(name, value));
	}
	
	public List<Parameter> getHeaders() { return headers; }
	
	public void setCookies(final String name, final String value) {
		cookies.add(new Parameter(name, value));
	}
	
	public void setCookies(final List<Parameter> cookies) {
		this.cookies = cookies;
	}
	
	public List<Parameter> getCookies() { return cookies; }
	
	public File getFile() { return file; }
	
	public void setFile(final File file) { this.file = file; }
 	
	/**
	 * Response status constants
	 * @author David Mayboroda (david.ftzi@gmail.com)
	 */
	public static enum ResponseStatus { 
		
		SUCCESS, FAILED;
		
		public static ResponseStatus byCode(final int code) {
			return ResponseStatus.values()[code];
		}
		
	}

	@Override
	public int describeContents() { return 0; }

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		dest.writeTypedList(headers);
		dest.writeTypedList(cookies);
		dest.writeString(message);
		dest.writeString(stream);
		dest.writeInt(resultCode);
		dest.writeInt(status.ordinal());
		dest.writeSerializable(file);
	}
	
	/**
	 * Response {@link Parcelable} creator.
	 * @author David Mayboroda(david.ftzi@gmail.com) 
	 */
	public static final Parcelable.Creator<Response> CREATOR = new Creator<Response>() {
		
		@Override
		public Response[] newArray(final int size) {
			return new Response[size];
		}
		
		@Override
		public Response createFromParcel(final Parcel source) {
			return new Response(source);
		}
	};

}
