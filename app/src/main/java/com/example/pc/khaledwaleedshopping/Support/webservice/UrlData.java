package com.example.pc.khaledwaleedshopping.Support.webservice;

/**
 * Created by ahmed on 4/24/2017.
 */

//customize url data which need to send in post
public class UrlData {
    String string = "";

    public void add(String s, String d) {
        if (string.equals(""))
            string += s + "=" + d;
        else
            string += "&" + s + "=" + d;
    }

    public String get() {
        return string;
    }
}
