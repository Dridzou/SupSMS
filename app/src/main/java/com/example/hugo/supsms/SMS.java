package com.example.hugo.supsms;

/**
 * Created by Hugo on 05/02/2015.
 */
public class SMS {

    private String address;
    private String body;

    public SMS(String address, String body) {
        this.address = address;
        this.body = body;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
