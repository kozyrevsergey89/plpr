 package com.tetra.service.rest;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Model for basic request representation.
 * {@link Parcelable}
 * @author David Mayboroda(david.ftzi@gmail.com)
 */
public abstract class Request<M extends Serializable> implements Serializable{

	/**Serial version UID.*/
	private static final long serialVersionUID = -5858829412739407915L;
	/**Constant for single request .*/
	protected RequestType requestType;
	/**Url for connection and retrieve response .*/
	protected String url;
	/**Request headers. */
	private List<Parameter> headers;
	/**Request params. */
	private List<Parameter> simpleParams;
	/**Post request entity params. */
	private List<Parameter> postEntities;
	/**Request cookies. */
	private List<Parameter> cookies;
	/**HTTP request body.*/
	private byte[] body;
	/**Model class type.*/
	private Class<?> classType;
	/**Request api type.*/
	private int apiType = -1;
	/**Multipart request file.*/
	private File file;
    /**Multipart request files.*/
    private File[] files;
	
	
	//TO DO: Parcelable Cookie object with all params like path, domain and so on.
	
	public Request() {
		//init();
		this.url = getUrl();
		this.requestType = getRequestType();
	}
	
	/*
	private void init() {
		postEntities = new ArrayList<Parameter>();
		simpleParams = new ArrayList<Parameter>();
		headers = new ArrayList<Parameter>();
	}
	*/
	
	public int getApiType() { return apiType; }
	
	public void setApiType(final int apiType) {
		this.apiType = apiType;
	}
	
	public abstract RequestType getRequestType();

	public void setRequestType(final RequestType requestType) {
		this.requestType = requestType;
	}

	public Class<?> getModelClass() { return RequestUtils.getModelClass(getClass()); }
	
	public Class<?> getClassType() { return classType; }
	
	public void setClassType(final Class<?> clazz) { this.classType = clazz; }
	
	public abstract String getUrl();
	
	public void setUrl(final String url) { this.url = url; }

	public byte[] getBody() { return body; }

	public void setBody(final byte[] body) { this.body = body; }
	
	public void setHeaders(final String name, final String value) {
		if (headers == null) {
			headers = new ArrayList<Parameter>();
		}
		headers.add(new Parameter(name, value));
	}

	public void setCookies(final String name, final String value) {
		if (cookies == null) {
			cookies = new ArrayList<Parameter>();
		}
		cookies.add(new Parameter(name, value));
	}
	
	public void setCookies(final List<Parameter> cookies) {
		this.cookies = cookies;
	}
	
	public List<Parameter> getCookies() { return cookies; }
	
	public void setHeaders(final List<Parameter> headers) {
		this.headers = headers;
	}
	
	public List<Parameter> getHeaders() { return headers; }
	
	
	public void addUrlParams(final String name, final String value) {
		if (simpleParams == null) {
			simpleParams = new ArrayList<Parameter>();
		}
		simpleParams.add(new Parameter(name, value));
	}
	
	public void setUrlParams(final List<Parameter> simpParameters) {
		this.simpleParams = simpParameters;
	}
	
	public List<Parameter> getUrlParams() { return simpleParams; }
	
	public void setPostEntities(final String name, final String value) {
		if (postEntities == null) {
			postEntities = new ArrayList<Parameter>();
		}
		postEntities.add(new Parameter(name, value));
	}
	
	public void setPostEntities(final List<Parameter> postEntities) {
		this.postEntities = postEntities;
	}
	
	public List<Parameter> getPostEntities() { return postEntities; }
	
	public void setFile(final File file) { this.file = file; }
	
	public File getFile() { return file; }

    public File[] getFiles() {
        return files;
    }

    public void setFiles(File[] files) {
        this.files = files;
    }

    /**
	 * Basic request types.
	 * @author David Mayboroda(david.ftzi@gmail.com)
	 */
	public static enum RequestType {
		POST, GET, MULTIPART;
		
		public static RequestType byCode(final int value) {
			return RequestType.values()[value];
		}
	}
	
	
}
