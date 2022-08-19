package com.example.drivingmonitor.LoginRegisterManagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.drivingmonitor.HomeFunctionManagement.ActivityHome;
import com.example.drivingmonitor.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ActivityWelcome extends AppCompatActivity {
    private static final int ACQUIRE_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                acquirePermission();
            }
        };
        timer.schedule(timerTask, 100);//开始申请权限前时间

    }

    protected void acquirePermission() {
        List<String> permissionList = new ArrayList<>();//需要申请的权限列表
        //拨号权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.CALL_PHONE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!permissionList.isEmpty()) {
            String[] permissionArray = permissionList.toArray(new String[0]);
            ActivityCompat.requestPermissions(this, permissionArray, ACQUIRE_PERMISSION);
        }else {
            pageJump();
        }
    }

    //权限获取情况判断
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACQUIRE_PERMISSION) {
            if (grantResults.length > 0) {
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "未获得全部授权！", Toast.LENGTH_SHORT).show();
                        ActivityWelcome.this.finish();
                        return;
                    }
                }
                pageJump();
            }
        }
    }

    private void pageJump() {
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(ActivityWelcome.this, ActivityLogin.class);
                intent.putExtra("loginDirectly", true);
                startActivity(intent);
                finish();
            }
        };
        timer.schedule(timerTask, 0);//申请权限后跳转时间
    }
}