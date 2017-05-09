package com.wuqi.dev.http;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * Created by Risky57 on 2017/5/9.
 */
public class HttpManager {

    private static class SingletonHolder{
        private static final HttpManager INSTANCE = new HttpManager();
    }

    public static HttpManager getInstance(){
        return SingletonHolder.INSTANCE;
    }

    private OkHttpClient mClient;

    private HttpManager() {
        mClient = new OkHttpClient.Builder().build();
    }

    private Response _startRequest(Request request) throws IOException {
        return mClient.newCall(request).execute();
    }

    public static Response startRequest(Request request){
        try {
            return getInstance()._startRequest(request);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
