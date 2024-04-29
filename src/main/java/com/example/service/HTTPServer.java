package com.example.service;

import com.example.service.http.HttpMgr;
import com.example.service.http.utils.log.Log;

public class HTTPServer extends Thread {
    private static final String TAG = "HTTPServer";

    private static HttpMgr httpMgr;

    public HTTPServer() {
        super();
    }

    public void run() {
        try {
            httpMgr = new HttpMgr();
            httpMgr.init();
        } catch (Exception e) {
            Log.d(TAG,"HttpMgr initialization failed.");
            e.printStackTrace();
        }
    }
}
