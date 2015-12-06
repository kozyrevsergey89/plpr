package com.tetra.service.rest;

import android.net.Uri;
import android.text.TextUtils;
import com.tetra.service.rest.Response.ResponseStatus;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

/**
 * Request method object used for HTTP GET and POST requests.
 * All methods return response in basic {@link Response} representation.
 *
 * @author David Mayboroda (david.ftzi@gmail.com)
 */
public class RequestMethod {

    /**
     * XML API type constant.
     */
    public static final int XML = 0;
    /**
     * JSON API type constant.
     */
    public static final int JSON = 1;

    /**
     * API type.
     */
    private int type;
    /**
     * HTTP Client.
     */
    private DefaultHttpClient httpClient;

    public RequestMethod(final int type) {
        switch (type) {
            case XML:
                ;
                break;
            case JSON:
                ;
                break;
            default:
                throw new UnsupportedOperationException("Unsupported operation " + type);
        }
        this.type = type;
    }

    public RequestMethod() { /* nothing */ }

    public Response doRequset(final Request<?> request) {
        if (request == null) {
            return buildErrorResponse("Request is null", ResponseStatus.FAILED);
        }
        switch (request.getRequestType()) {
            case POST:
                return doPost(request);
            case GET:
                return doGet(request);
            case MULTIPART:
                return doMultipart(request);
            default:
                return buildErrorResponse("Unsupported request type", ResponseStatus.FAILED);
        }
    }

    private Response doMultipart(final Request<?> request) {
        if (checkRequest(request)) {
            return null;
        }
        HttpPost post = new HttpPost(request.getUrl());
        MultipartEntity entity = new MultipartEntity();
        if (request.getFile() != null) {
            //TO DO: create an object for multipart entity;
            entity.addPart("file", new FileBody(request.getFile()));
            try {
                entity.addPart("file_name", new StringBody(request.getFile().getName()));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            post.setEntity(entity);
        } else if (request.getFiles() != null) {
            File[] files = request.getFiles();
            for (int i = 0; i < files.length; i++) {
                entity.addPart("file"+i, new FileBody(files[i]));
            }
            post.setEntity(entity);
        }

//        List<Parameter> postEntities = request.getPostEntities();
//        if (checkListOfParams(postEntities)) {
//            final LinkedList<BasicNameValuePair> postParams = new LinkedList<BasicNameValuePair>();
//            for (Parameter parameter : postEntities) {
//                postParams.add(new BasicNameValuePair(parameter.getName(), parameter.getValue()));
//            }
//            try {
//                //Encoding parameter
//                post.setEntity(new UrlEncodedFormEntity(postParams, "UTF-8"));
//            } catch (final UnsupportedEncodingException ex) {
//                return buildErrorResponse(ex.getMessage(), ResponseStatus.FAILED);
//            }
//        }
        return doRequest(request, post);
    }

    private Response doGet(final Request<?> request) {
        if (checkRequest(request)) {
            return null;
        }
        final HttpGet get = new HttpGet(createUrlWithParams(request));
        return doRequest(request, get);
    }

    private Response doPost(final Request<?> request) {
        if (checkRequest(request)) {
            return null;
        }
        final HttpPost post = new HttpPost(createUrlWithParams(request));

        List<Parameter> postEntities = request.getPostEntities();
        if (checkListOfParams(postEntities)) {
            final LinkedList<BasicNameValuePair> postParams = new LinkedList<BasicNameValuePair>();
            for (Parameter parameter : postEntities) {
                postParams.add(new BasicNameValuePair(parameter.getName(), parameter.getValue()));
            }
            try {
                //Encoding parameter
                post.setEntity(new UrlEncodedFormEntity(postParams, "UTF-8"));
            } catch (final UnsupportedEncodingException ex) {
                return buildErrorResponse(ex.getMessage(), ResponseStatus.FAILED);
            }
        }

        return doRequest(request, post);
    }

    private String createUrlWithParams(final Request<?> request) {
        if (request == null || TextUtils.isEmpty(request.getUrl())) {
            return null;
        }
        Uri.Builder uriBuilder = Uri.parse(request.getUrl()).buildUpon();

        List<Parameter> params = request.getUrlParams();
        if (checkListOfParams(params)) {
            for (Parameter parameter : params) {
                uriBuilder.appendQueryParameter(parameter.getName(), parameter.getValue());
            }
        }
        return uriBuilder.build().toString();
    }

    private Response doRequest(final Request<?> request, final HttpRequestBase fetcher) {
        if (fetcher == null) {
            return null;
        }

        List<Parameter> headers = request.getHeaders();
        if (checkListOfParams(headers)) {
            for (Parameter parameter : headers) {
                Header header = new BasicHeader(parameter.getName(), parameter.getValue());
                fetcher.addHeader(header);
            }
        }

        final DefaultHttpClient httpClient = getHttpClient();
        List<Parameter> cookies = request.getCookies();
        if (checkListOfParams(cookies)) {
            CookieStore cookieStore = httpClient.getCookieStore();
            for (Parameter cookie : cookies) {
                cookieStore.addCookie(new BasicClientCookie(cookie.getName(), cookie.getValue()));
            }
            httpClient.setCookieStore(cookieStore);
        }

        return doRequest(fetcher, httpClient);
    }

    private Response doRequest(final HttpRequestBase request, final DefaultHttpClient httpClient) {
        final Response response = new Response();
        int resultCode;
        try {
            HttpResponse httpResponse = httpClient.execute(request);
            InputStream stream = httpResponse.getEntity().getContent();
            response.setStreamString(RequestUtils.convertStreamToString(stream));
            Header[] headers = httpResponse.getAllHeaders();

            if (headers != null && headers.length != 0) {
                for (Header header : headers) {
                    response.setHeader(header.getName(), header.getValue());
                }
            }

            List<Cookie> cookies = httpClient.getCookieStore().getCookies();
            if (cookies != null && !cookies.isEmpty()) {
                for (Cookie cookie : cookies) {
                    response.setCookies(cookie.getName(), cookie.getValue());
                }
            }

            resultCode = httpResponse.getStatusLine().getStatusCode();

            if (resultCode == 200 || resultCode == 302) {
                response.setStatus(ResponseStatus.SUCCESS);
            } else {
                response.setStatus(ResponseStatus.FAILED);
            }
            response.setResultCode(resultCode);
            return response;
        } catch (final Exception ex) {
            //TO DO: create types of response statuses for all kind of exceptions
            return buildErrorResponse(ex.getMessage(), ResponseStatus.FAILED);
        }
    }

    private boolean checkListOfParams(final List<Parameter> params) {
        return params != null && !params.isEmpty();
    }

    private boolean checkRequest(final Request<?> request) {
        return request == null || TextUtils.isEmpty(request.getUrl());
    }

    private Response buildErrorResponse(final String message, final ResponseStatus status) {
        Response response = new Response();
        response.setMessage(!TextUtils.isEmpty(message) ? message : "message is empty � Cap");
        response.setStatus(status);
        return response;
    }

    public int getAPIType() {
        return type;
    }

    public void setHttpClient(final DefaultHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public DefaultHttpClient getHttpClient() {
        return httpClient == null ? new DefaultHttpClient() : httpClient;
    }

    //TO DO: Create a Killer interface that will delete all objects: parcer, request, response, request method;
}
