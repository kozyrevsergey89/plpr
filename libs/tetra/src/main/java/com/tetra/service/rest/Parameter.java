package com.tetra.service.rest;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Parameter entity 
 * @author David Mayboroda (david.ftzi@gmail.com)
 * {@link Parcelable}
 */
public class Parameter implements Parcelable {

	/** Parcelable parameter creator for unpacking values. */
	public static final Parcelable.Creator<Parameter> CREATOR = new Parcelable.Creator<Parameter>() {
		@Override
		public Parameter createFromParcel(final Parcel source) {
			return new Parameter(source);
		}

		@Override
		public Parameter[] newArray(final int size) {
			return new Parameter[size];
		}
	};
	
	/**Parameter name.*/
	private String name;
	/**Parameter value.*/
	private String value;
	
	private Parameter(final Parcel parcel) {
		this.value = parcel.readString();
		this.name = parcel.readString();
	}
	
	public Parameter(final String name, final String value) {
		this.name = name;
		this.value = value;
	}
	
	@Override
	public int describeContents() { return 0; }

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		dest.writeString(name);
		dest.writeString(value);
	}
	
	public String getName() { return name; }
	
	public String getValue() { return value; }
	
	public void setValue(final String value) {
		this.value = value;
	}
	
	public void setName(final String name) {
		this.name = name;
	}
	
}