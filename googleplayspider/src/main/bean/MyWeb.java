package main.bean;

import org.apache.http.Header;

import java.io.InputStream;
import java.net.URL;

public class MyWeb implements Cloneable{
    /***************/
    private URL requestUrl;
    /***************/
    private Header[] requestHeaders;
    /***************/
    private String requestCookies;
    /***************/
    private String referUr;
    private boolean supportRedirct;

    private int responseStatusCode;
    private Header[] responseHeader;
    private Header[] responseCookieHeader;
    private InputStream source;
    private String sourceData;

    @Override
    public Object clone(){
        MyWeb myWeb = null;
        try {
            myWeb = (MyWeb) super.clone();
        }catch (CloneNotSupportedException e){
            e.printStackTrace();
        }
        return myWeb;
    }

    public URL getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(URL requestUrl) {
        this.requestUrl = requestUrl;
    }

    public Header[] getRequestHeaders() {
        return requestHeaders;
    }

    public void setRequestHeaders(Header[] requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public String getRequestCookies() {
        return requestCookies;
    }

    public void setRequestCookies(String requestCookies) {
        this.requestCookies = requestCookies;
    }

    public String getReferUr() {
        return referUr;
    }

    public void setReferUr(String referUr) {
        this.referUr = referUr;
    }

    public int getResponseStatusCode() {
        return responseStatusCode;
    }

    public void setResponseStatusCode(int responseStatusCode) {
        this.responseStatusCode = responseStatusCode;
    }

    public boolean isSupportRedirct() {
        return supportRedirct;
    }

    public void setSupportRedirct(boolean supportRedirct) {
        this.supportRedirct = supportRedirct;
    }

    public Header[] getResponseHeader() {
        return responseHeader;
    }

    public void setResponseHeader(Header[] responseHeader) {
        this.responseHeader = responseHeader;
    }


    public InputStream getSource() {
        return source;
    }

    public void setSource(InputStream source) {
        this.source = source;
    }

    public String getSourceData() {
        return sourceData;
    }

    public void setSourceData(String sourceData) {
        this.sourceData = sourceData;
    }

    public Header[] getResponseCookieHeader() {
        return responseCookieHeader;
    }

    public void setResponseCookieHeader(Header[] responseCookieHeader) {
        this.responseCookieHeader = responseCookieHeader;
    }
}

