package com.example.drivingmonitor.HttpUtils;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpUtils {
    private final static String LOGIN = "http://fuchuang.wangsz12.top/api/login";
    private final static String REGISTER = "http://fuchuang.wangsz12.top/api/register";
    private final static String DOWNLOAD = "http://fuchuang.wangsz12.top/api/getUserData?uid=";

    public static void testRequest(String Url, Callback callback) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(Url).build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void Login(String account, String password, Callback callback) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(LOGIN)
                .post(generateAccountBody(account, password))
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void Register(String account, String password, Callback callback) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(REGISTER)
                .post(generateAccountBody(account, password))
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void Download(String uid, Callback callback) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(DOWNLOAD + uid)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    private static RequestBody generateAccountBody(String account, String password) {
        JSONObject JSON = new JSONObject();
        try {
            JSON.put("account", account);
            JSON.put("password", password);
        }catch (JSONException e) {
            e.printStackTrace();
        }
        MediaType mediaType = MediaType.parse("application/json;charset=utf-8");
        return RequestBody.create(mediaType, ""+JSON.toString());
    }
}
