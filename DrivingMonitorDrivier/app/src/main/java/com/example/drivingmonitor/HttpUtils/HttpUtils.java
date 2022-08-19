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
    private final static String UPLOAD = "http://fuchuang.wangsz12.top/api/updateUserData";

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

    public static void Upload(String uid, String alcoholConcentration, String heartRate, String bloodOxygen,
                              String microCirculation, String highPressure, String lowPressure,
                              String fatigueState, String abnormalState, String currentLocation, Callback callback) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(UPLOAD)
                .post(generateDataBody(uid, alcoholConcentration, heartRate, bloodOxygen, microCirculation,
                        highPressure, lowPressure, fatigueState, abnormalState, currentLocation))
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static String parseJSON(String JSON) throws JSONException {
        JSONObject jsonObject = new JSONObject(JSON);

        String status = jsonObject.getString("status");
        Log.d("Test", status);
        if (status.equals("false")) {
            return jsonObject.getString("msg");
        }else {
            return "注册成功";
        }
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

    private static RequestBody generateDataBody(String uid, String alcoholConcentration, String heartRate,
                                                String bloodOxygen, String microCirculation, String highPressure,
                                                String lowPressure, String fatigueState,
                                                String abnormalState, String currentLocation) {
        JSONObject JSON = new JSONObject();
        try {
            JSON.put("uid", uid);
            JSON.put("alcohol_concentration", alcoholConcentration);
            JSON.put("heart_rate", heartRate);
            JSON.put("blood_oxygen", bloodOxygen);
            JSON.put("micro_circulation", microCirculation);
            JSON.put("high_pressure", highPressure);
            JSON.put("low_pressure", lowPressure);
            JSON.put("fatigue", fatigueState);
            JSON.put("abnormal", abnormalState);
            JSON.put("location", currentLocation);
        }catch (JSONException e) {
            e.printStackTrace();
        }
        MediaType mediaType = MediaType.parse("application/json;charset=utf-8");
        return RequestBody.create(mediaType, ""+JSON.toString());
    }
}
