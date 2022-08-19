package com.example.drivingmonitor.DrawerFunctionManagement;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
//import com.baidu.mapapi.SDKInitializer;
//import com.baidu.mapapi.map.BaiduMap;
//import com.baidu.mapapi.map.MapStatusUpdate;
//import com.baidu.mapapi.map.MapStatusUpdateFactory;
//import com.baidu.mapapi.map.MapView;
//import com.baidu.mapapi.map.MyLocationData;
//import com.baidu.mapapi.model.LatLng;
import com.example.drivingmonitor.R;

public class ActivityLocationService extends AppCompatActivity {
    private LocationClient locationClient;
    private TextView currentLocation;

    private SharedPreferences sharedPreferencesLocationInformation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_location);

        Init();
    }

    private void Init() {
        LocationClient.setAgreePrivacy(true);

        currentLocation = findViewById(R.id.currentLocation);

        sharedPreferencesLocationInformation = getSharedPreferences("LocationInformation", MODE_PRIVATE);
        currentLocation.setText(sharedPreferencesLocationInformation.getString("LatestLocation", getString(R.string.noData)));

        ImageView quitMap = findViewById(R.id.quitMap);
        quitMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ImageView refreshLocation = findViewById(R.id.refreshLocation);
        refreshLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation();
            }
        });

        registerReceiver(locationReceiver, new IntentFilter("com.example.drivingmonitor.locationInformation"));
    }

    private BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String location = intent.getStringExtra("location");
            currentLocation.setText(location);
        }
    };

    private void getLocation() {
        try {
            locationClient = new LocationClient(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        LocationClientOption locationClientOption = new LocationClientOption();
        locationClientOption.setIsNeedAddress(true);
        locationClient.setLocOption(locationClientOption);

        BDAbstractLocationListener bdAbstractLocationListener = new BDAbstractLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                String location = bdLocation.getAddrStr();
                sendLocationBroadcast(location);
                currentLocation.setText(location);
                SharedPreferences.Editor editor = sharedPreferencesLocationInformation.edit();
                editor.putString("LatestLocation", location);
                editor.apply();
            }
        };
        locationClient.registerLocationListener(bdAbstractLocationListener);

        locationClient.start();
    }

    //发送位置信息广播
    private void sendLocationBroadcast(String location) {
        Intent intent = new Intent();
        intent.setAction("com.example.drivingmonitor.locationInformation");
        intent.putExtra("location", location);
        Log.d("Broadcast", "成功发送一条位置信息广播");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(locationReceiver);
    }

}