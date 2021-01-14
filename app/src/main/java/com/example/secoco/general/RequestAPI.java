package com.example.secoco.general;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class RequestAPI {

    private static RequestAPI instance;
    private RequestQueue requestQueue;
    private static Context context;

    private RequestAPI(Context context) {
        this.context = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized RequestAPI getInstance(Context context) {
        if (instance == null) {
            instance = new RequestAPI(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void add(Request<T> req) {
        getRequestQueue().add(req);
    }

}
