/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.saas;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;


/**
 * RestResponse
 *
 * @author sigmar
 */
public class CompatResponse {
    private ByteArrayOutputStream os;
    private String contentType = "text/plain";
    private String contentEncoding;
    private int responseCode;
    private String responseMsg;
    private long lastModified;

    
    public CompatResponse() {
        os = new ByteArrayOutputStream();
    }
    
    public CompatResponse(byte[] bytes) throws IOException {
        this();

        byte[] buffer = new byte[1024];
        int count = 0;
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        while ((count = bis.read(buffer)) != -1) {
            write(buffer, 0, count);
        }
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    
    public String getContentType() {
        return contentType;
    }

    public void setContentEncoding(String contentEncoding) {
        this.contentEncoding = contentEncoding;
    }
    
    public void setResponseMessage(String msg) {
        this.responseMsg = msg;
    }
    
    public String getResponseMessage() {
        return responseMsg;
    }
    
    public void setResponseCode(int code) {
        this.responseCode = code;
    }
    
    public int getResponseCode() {
        return responseCode;
    }
    
    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }
    
    public long getLastModified() {
        return lastModified;
    }
    
    public void write(byte[] bytes, int start, int length) {
        os.write(bytes, start, length);
    }
    
    public byte[] getDataAsByteArray() {
        return os.toByteArray();
    }
    
    public String getDataAsString() {
        try {
            return os.toString("UTF-8");
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
        
        return null;
    }
    
    public OutputStream getOutputStream() {
        return os;
    }
}
