package com.example.drivingmonitor.DrawerFunctionManagement;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.example.drivingmonitor.R;

public class ActivityLocationServicePlus extends AppCompatActivity {
    private static final boolean NAVIGATE_TO_LOCATION = true;

    private LocationClient locationClient;
    private MapView mapView;
    private BaiduMap baiduMap;

    private TextView currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //实例化地图SDK
        SDKInitializer.setAgreePrivacy(getApplicationContext(), true);
        try {
            SDKInitializer.initialize(getApplicationContext());
        }catch (Exception e) {
            e.printStackTrace();
        }

        setContentView(R.layout.activity_location_plus);

        //设置透明状态栏
        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        decorView.setSystemUiVisibility(option);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        //实例化LocationClient
        LocationClient.setAgreePrivacy(true);
        try {
            locationClient = new LocationClient(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mapView = findViewById(R.id.mapView);
        mapView.showZoomControls(false);    //关闭原生放大缩小按钮
        baiduMap = mapView.getMap();
        baiduMap.setMyLocationEnabled(true);

        currentLocation = findViewById(R.id.currentLocation);

        ImageView quitMap = findViewById(R.id.quitMap);
        quitMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

//        setLocationClient();
//        locationClient.start();
        registerReceiver(locationReceiver, new IntentFilter("com.example.drivingmonitor.currentLocation"));
    }

    //定位设置
    private void setLocationClient() {
        //定位方式设置
        LocationClientOption locationClientOption = new LocationClientOption();
        locationClientOption.setIsNeedAddress(true);
        locationClient.setLocOption(locationClientOption);

        //创建定位，回调结果
        BDAbstractLocationListener abstractLocationListener = new BDAbstractLocationListener() {
            @Override
            public void onReceiveLocation(final BDLocation bdLocation) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(bdLocation.getCountry());
                        stringBuilder.append(bdLocation.getProvince());
                        stringBuilder.append(bdLocation.getCity());
                        stringBuilder.append(bdLocation.getDistrict());
                        stringBuilder.append(bdLocation.getStreet());
                        currentLocation.setText(stringBuilder);
//                        mapLocation(bdLocation);
                    }
                });
            }
        };
        locationClient.registerLocationListener(abstractLocationListener);
    }

    //地图定位
    private void mapLocation(BDLocation bdLocation) {
        if (NAVIGATE_TO_LOCATION) {
            //将地图定位到当前位置
            LatLng latLng = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
            MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(latLng);
            baiduMap.animateMapStatus(mapStatusUpdate);
            mapStatusUpdate = MapStatusUpdateFactory.zoomTo(16f);
            baiduMap.animateMapStatus(mapStatusUpdate);

            //显示当前坐标点
            MyLocationData.Builder builder = new MyLocationData.Builder();
            builder.latitude(bdLocation.getLatitude());
            builder.longitude(bdLocation.getLongitude());
            MyLocationData locationData = builder.build();
            baiduMap.setMyLocationData(locationData);
        }
    }

    private void setLocation(String location) {
        currentLocation.setText(location);
    }

    private void setCoordinate(String latitude, String longitude) {
        if (NAVIGATE_TO_LOCATION) {
            //将地图定位到当前位置
            LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
            MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(latLng);
            baiduMap.animateMapStatus(mapStatusUpdate);
            mapStatusUpdate = MapStatusUpdateFactory.zoomTo(16f);
            baiduMap.animateMapStatus(mapStatusUpdate);

            //显示当前坐标点
            MyLocationData.Builder builder = new MyLocationData.Builder();
            builder.latitude(Double.parseDouble(latitude));
            builder.longitude(Double.parseDouble(longitude));
            MyLocationData locationData = builder.build();
            baiduMap.setMyLocationData(locationData);
        }
    }

    BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String locationData = intent.getStringExtra("currentLocation");
            assert locationData != null;
            String[] locationList = locationData.split(" ");
            if (locationList.length == 3) {
                if (!locationList[0].equals("null")) {
                    setLocation(locationList[0]);
                    setCoordinate(locationList[1], locationList[2]);
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationClient.stop();
        mapView.onDestroy();
        baiduMap.setMyLocationEnabled(false);
        unregisterReceiver(locationReceiver);
    }
}