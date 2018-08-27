package com.example.pc.khaledwaleedshopping.Support.webservice;

import android.net.Uri;

/**
 * Created by ahmed on 4/24/2017.
 */

//customize url data which need to send in post
public class UrlData {
    String string = "";

    public void add(String s, String d) {
        d = Uri.encode(d, "utf-8");
        if (string.equals(""))
            string += s + "=" + d;
        else
            string += "&" + s + "=" + d;
    }

    public String get() {
        return string;
    }
}
