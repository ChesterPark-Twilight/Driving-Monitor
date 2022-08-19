package com.example.drivingmonitor.HomeFunctionManagement;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.baidu.location.LocationClient;
import com.example.drivingmonitor.CustomView.TextViewAnimationFloat;
import com.example.drivingmonitor.CustomView.TextViewAnimationInt;
import com.example.drivingmonitor.DrawerFunctionManagement.ActivityAccount;
import com.example.drivingmonitor.FamilyMemberFunction.ActivityFamilyMember;
import com.example.drivingmonitor.DrawerFunctionManagement.ActivityLocationServicePlus;
import com.example.drivingmonitor.HttpUtils.HttpUtils;
import com.example.drivingmonitor.LoginRegisterManagement.ActivityLogin;
import com.example.drivingmonitor.LoginRegisterManagement.DatabaseConnector;
import com.example.drivingmonitor.R;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.ColumnChartView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.example.drivingmonitor.LoginRegisterManagement.DatabaseConnector.QUERY_SUCCESS;

public class ActivityHome extends AppCompatActivity {
    private static final String MESSAGE_CHANNEL_NAME = "stateNotification";
    private static final String MESSAGE_CHANNEL = "Notification";

    private DrawerLayout drawerLayout;
    private SwipeRefreshLayout homeRefreshLayout;

    private TextView drawerAccountName;
    private TextView homeAccountName;
    private TextView currentMember;

    private CircularProgressBar progressBarAlcoholConcentration;

    private ColumnChartView chartBloodPressure;

    private SharedPreferences sharedPreferencesAccountInformation;
    private SharedPreferences sharedPreferencesFamilyMember;
    private SharedPreferences sharedPreferencesHealthData;

    private TextView dataHeartRate;
    private TextView dataAlcoholConcentration;
    private TextView dataFatigueState;
    private TextView dataAbnormalState;
    private TextView dataHighBloodPressure;
    private TextView dataLowBloodPressure;
    private TextView dataMicroCirculation;
    private TextView dataBloodOxygen;

    private TextView stateHeartRate;
    private TextView stateAlcoholConcentration;
    private TextView stateBloodPressure;
    private TextView stateMicroCirculation;
    private TextView stateBloodOxygen;

    private CardView cardFatigueState;
    private CardView cardAbnormalState;

    private boolean monitorState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        LocationClient.setAgreePrivacy(true);   //同意定位政策

        Init();
    }

    private void Init() {

        drawerLayout = findViewById(R.id.drawerLayout);
        homeRefreshLayout = findViewById(R.id.homeRefreshLayout);
        homeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                homeRefreshLayout.setRefreshing(false);
                updateData("0.1", "1", "1",
                        "1", "1", "1",
                        "0", "0");
            }
        });

        drawerAccountName = findViewById(R.id.drawerAccountName);
        homeAccountName = findViewById(R.id.homeAccountName);
        currentMember = findViewById(R.id.currentMember);

        progressBarAlcoholConcentration = findViewById(R.id.progressBarAlcoholConcentration);

        chartBloodPressure = findViewById(R.id.chartBloodPressure);

        dataHeartRate = findViewById(R.id.dataHeartRate);
        dataAlcoholConcentration = findViewById(R.id.dataAlcoholConcentration);
        dataFatigueState = findViewById(R.id.dataFatigueState);
        dataAbnormalState = findViewById(R.id.dataAbnormalState);
        dataHighBloodPressure = findViewById(R.id.dataHighBloodPressure);
        dataLowBloodPressure = findViewById(R.id.dataLowBloodPressure);
        dataMicroCirculation = findViewById(R.id.dataMicroCirculation);
        dataBloodOxygen = findViewById(R.id.dataBloodOxygen);

        stateHeartRate = findViewById(R.id.stateHeartRate);
        stateAlcoholConcentration = findViewById(R.id.stateAlcoholConcentration);
        stateBloodPressure = findViewById(R.id.stateBloodPressure);
        stateMicroCirculation = findViewById(R.id.stateMicroCirculation);
        stateBloodOxygen = findViewById(R.id.stateBloodOxygen);

        cardFatigueState = findViewById(R.id.cardFatigueState);
        cardAbnormalState = findViewById(R.id.cardAbnormalState);

        sharedPreferencesAccountInformation = getSharedPreferences("AccountInformation", MODE_PRIVATE);
        drawerAccountName.setText(sharedPreferencesAccountInformation.getString("AccountName", getString(R.string.accountNameInitially)));
        homeAccountName.setText(sharedPreferencesAccountInformation.getString("AccountName", getString(R.string.accountNameInitially)));

        sharedPreferencesFamilyMember = getSharedPreferences("CurrentMember", MODE_PRIVATE);
        currentMember.setText(sharedPreferencesFamilyMember.getString("memberUser", getString(R.string.noAbnormalState)));

        sharedPreferencesHealthData = getSharedPreferences("HealthData", MODE_PRIVATE);
        dataHeartRate.setText(sharedPreferencesHealthData.getString("dataHeartRate", getString(R.string.noData)));
        dataAlcoholConcentration.setText(sharedPreferencesHealthData.getString("dataAlcoholConcentration", getString(R.string.noData)));
        dataFatigueState.setText(sharedPreferencesHealthData.getString("dataFatigueState", getString(R.string.noCondition)));
        dataAbnormalState.setText(sharedPreferencesHealthData.getString("dataAbnormalState", getString(R.string.noCondition)));
        dataHighBloodPressure.setText(sharedPreferencesHealthData.getString("dataHighBloodPressure", getString(R.string.noData)));
        dataLowBloodPressure.setText(sharedPreferencesHealthData.getString("dataLowBloodPressure", getString(R.string.noData)));
        dataMicroCirculation.setText(sharedPreferencesHealthData.getString("dataMicroCirculation", getString(R.string.noData)));
        dataBloodOxygen.setText(sharedPreferencesHealthData.getString("dataBloodOxygen", getString(R.string.noData)));

        stateHeartRate.setText(sharedPreferencesHealthData.getString("stateHeartRate", getString(R.string.noCondition)));
        stateAlcoholConcentration.setText(sharedPreferencesHealthData.getString("stateAlcoholConcentration", getString(R.string.noCondition)));
        stateBloodPressure.setText(sharedPreferencesHealthData.getString("stateBloodPressure", getString(R.string.noCondition)));
        stateMicroCirculation.setText(sharedPreferencesHealthData.getString("stateMicroCirculation", getString(R.string.noCondition)));
        stateBloodOxygen.setText(sharedPreferencesHealthData.getString("stateBloodOxygen", getString(R.string.noCondition)));

        String alcoholConcentrationData = dataAlcoholConcentration.getText().toString();
        if (alcoholConcentrationData.equals("---")) {
            updateProgressBarAlcoholConcentration(0.00f);
        }else {
            updateProgressBarAlcoholConcentration(Float.parseFloat(alcoholConcentrationData));
        }

        String highData = dataHighBloodPressure.getText().toString();
        String lowData = dataLowBloodPressure.getText().toString();
        if (highData.equals("---") || lowData.equals("---")) {
            updateChartBloodPressure(0, 0);
        }else {
            updateChartBloodPressure(Integer.parseInt(highData), Integer.parseInt(lowData));
        }

        final CircleImageView headSculpture = findViewById(R.id.headSculpture);
        final CircleImageView entranceAccount = findViewById(R.id.entranceAccount);

        final Button entranceEmergencyContact = findViewById(R.id.entranceEmergencyContact);
        final Button entranceLocationService = findViewById(R.id.entranceLocationService);

        headSculpture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
//                updateData("1.4", "70", "95",
//                        "89", "125", "73",
//                        "6", "3");
//                Timer timer = new Timer();
//                TimerTask timerTask1 = new TimerTask() {
//                    @Override
//                    public void run() {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                updateData("1.4", "70", "95",
//                                        "89", "125", "73",
//                                        "1", "6");
//                            }
//                        });
//
//                    }
//                };
//                TimerTask timerTask2 = new TimerTask() {
//                    @Override
//                    public void run() {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                updateData("1.4", "70", "95",
//                                        "89", "125", "73",
//                                        "2", "6");
//                            }
//                        });
//
//                    }
//                };
//                timer.schedule(timerTask1, 7000);
//                timer.schedule(timerTask2, 11000);
            }
        });

        entranceAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityHome.this, ActivityAccount.class);
                startActivity(intent);
            }
        });

        entranceEmergencyContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityHome.this, ActivityFamilyMember.class);
                startActivity(intent);
            }
        });

        entranceLocationService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityHome.this, ActivityLocationServicePlus.class);
                startActivity(intent);
            }
        });

        registerReceiver();
    }

    //更新数据函数
    private void updateData(String newAlcoholConcentration, String newHeartRate, String newBloodOxygen,
                            String newMicroCirculation, String newHighBloodPressure, String newLowBloodPressure,
                            String newFatigueState, String newAbnormalState) {
        SharedPreferences.Editor editor = sharedPreferencesHealthData.edit();
        int tempData;
        String currentStringData;

        //酒精浓度更新
        float tempAlcoholConcentration = Float.parseFloat(newAlcoholConcentration);
        if (tempAlcoholConcentration != 0f) {
            currentStringData = dataAlcoholConcentration.getText().toString();
            if (currentStringData.equals("---")) {
                currentStringData = "0.0";
            }
            TextViewAnimationFloat.runAnimation(dataAlcoholConcentration, Float.parseFloat(currentStringData), tempAlcoholConcentration);
            dataAlcoholConcentration.setText(newAlcoholConcentration);
            if (tempAlcoholConcentration > 3.0f) {
                stateAlcoholConcentration.setText(getString(R.string.DrunkDriving));
                editor.putString("stateAlcoholConcentration", getString(R.string.DrunkDriving));
            }else {
                stateAlcoholConcentration.setText(getString(R.string.noDrunkDriving));
                editor.putString("stateAlcoholConcentration", getString(R.string.noDrunkDriving));
            }
            updateProgressBarAlcoholConcentration(tempAlcoholConcentration);
            editor.putString("dataAlcoholConcentration", newAlcoholConcentration);
        }

        //心率更新
        tempData = Integer.parseInt(newHeartRate);
        if (tempData != 0) {
            currentStringData = dataHeartRate.getText().toString();
            if (currentStringData.equals("---")) {
                currentStringData = "0";
            }
            TextViewAnimationInt.runAnimation(dataHeartRate, Integer.parseInt(currentStringData), tempData);
            dataHeartRate.setText(newHeartRate);
            if (tempData < 60 || tempData > 100) {
                stateHeartRate.setText(R.string.badCondition);
                editor.putString("stateHeartRate", getString(R.string.badCondition));
            }else {
                stateHeartRate.setText(R.string.goodCondition);
                editor.putString("stateHeartRate", getString(R.string.goodCondition));
            }
            editor.putString("dataHeartRate", newHeartRate);
        }

        //血氧饱和度更新
        tempData = Integer.parseInt(newBloodOxygen);
        if (tempData != 0) {
            currentStringData = dataBloodOxygen.getText().toString();
            if (currentStringData.equals("---")) {
                currentStringData = "0";
            }
            TextViewAnimationInt.runAnimation(dataBloodOxygen, Integer.parseInt(currentStringData), tempData);
            dataBloodOxygen.setText(newBloodOxygen);
            if (tempData < 95) {
                stateBloodOxygen.setText(R.string.badCondition);
                editor.putString("stateBloodOxygen", getString(R.string.badCondition));
            }else {
                stateBloodOxygen.setText(R.string.goodCondition);
                editor.putString("stateBloodOxygen", getString(R.string.goodCondition));
            }
            editor.putString("dataBloodOxygen", newBloodOxygen);
        }

        //微循环更新
        tempData = Integer.parseInt(newMicroCirculation);
        if (tempData != 0) {
            currentStringData = dataMicroCirculation.getText().toString();
            if (currentStringData.equals("---")) {
                currentStringData = "0";
            }
            TextViewAnimationInt.runAnimation(dataMicroCirculation, Integer.parseInt(currentStringData), tempData);
            dataMicroCirculation.setText(newMicroCirculation);
            if (tempData < 80) {
                stateMicroCirculation.setText(R.string.badCondition);
                editor.putString("stateMicroCirculation", getString(R.string.badCondition));
            }else {
                stateMicroCirculation.setText(R.string.goodCondition);
                editor.putString("stateMicroCirculation", getString(R.string.goodCondition));
            }
            editor.putString("dataMicroCirculation", newMicroCirculation);
        }

        //血压更新
        int highData = Integer.parseInt(newHighBloodPressure);
        int lowData = Integer.parseInt(newLowBloodPressure);
        if (highData != 0 && lowData != 0) {
            String highStringData = dataHighBloodPressure.getText().toString();
            String lowStringData = dataLowBloodPressure.getText().toString();
            if (highStringData.equals("---")) {
                highStringData = "0";
            }
            if (lowStringData.equals("---")) {
                lowStringData = "0";
            }
            TextViewAnimationInt.runAnimation(dataHighBloodPressure, Integer.parseInt(highStringData), highData);
            TextViewAnimationInt.runAnimation(dataLowBloodPressure, Integer.parseInt(lowStringData), lowData);
            dataHighBloodPressure.setText(newHighBloodPressure);
            dataLowBloodPressure.setText(newLowBloodPressure);
            if (highData < 90 || highData > 140 || lowData < 60 || lowData > 90) {
                stateBloodPressure.setText(R.string.badCondition);
                editor.putString("stateBloodPressure", getString(R.string.badCondition));
            }else {
                stateBloodPressure.setText(R.string.goodCondition);
                editor.putString("stateBloodPressure", getString(R.string.goodCondition));
            }
            updateChartBloodPressure(highData, lowData);
            editor.putString("dataHighBloodPressure", newHighBloodPressure);
            editor.putString("dataLowBloodPressure", newLowBloodPressure);
        }

        //疲劳状态更新
        tempData = Integer.parseInt(newFatigueState);
        if (tempData == 0) {
            dataFatigueState.setText(getString(R.string.noCondition));
            editor.putString("dataFatigueState", getString(R.string.noCondition));
            cardFatigueState.setCardBackgroundColor(getColor(R.color.groupCyan));
        }else if (tempData == 1) {
            dataFatigueState.setText(R.string.fatigueMild);
            editor.putString("dataFatigueState", getString(R.string.fatigueMild));
            cardFatigueState.setCardBackgroundColor(getColor(R.color.groupOrange));
        }else if (tempData == 2) {
            dataFatigueState.setText(R.string.fatigueExcessive);
            editor.putString("dataFatigueState", getString(R.string.fatigueExcessive));
            cardFatigueState.setCardBackgroundColor(getColor(R.color.groupOrange));
        }else {
            dataFatigueState.setText(R.string.fatigueNormal);
            editor.putString("dataFatigueState", getString(R.string.fatigueNormal));
            cardFatigueState.setCardBackgroundColor(getColor(R.color.groupCyan));
        }

        //异常状态更新
        tempData = Integer.parseInt(newAbnormalState);
        if (tempData == 0) {
            dataAbnormalState.setText(R.string.noAbnormalState);
            editor.putString("dataAbnormalState", getString(R.string.noAbnormalState));
            cardAbnormalState.setCardBackgroundColor(getColor(R.color.groupCyan));
        }else if (tempData == 1) {
            dataAbnormalState.setText(R.string.makePhoneCall);
            editor.putString("dataAbnormalState", getString(R.string.makePhoneCall));
            cardAbnormalState.setCardBackgroundColor(getColor(R.color.groupOrange));
        }else if (tempData == 2) {
            dataAbnormalState.setText(R.string.smoke);
            editor.putString("dataAbnormalState", getString(R.string.smoke));
            cardAbnormalState.setCardBackgroundColor(getColor(R.color.groupOrange));
        }else if (tempData == 3) {
            dataAbnormalState.setText(R.string.drink);
            editor.putString("dataAbnormalState", getString(R.string.drink));
            cardAbnormalState.setCardBackgroundColor(getColor(R.color.groupOrange));
        }else {
            dataAbnormalState.setText(R.string.rhythmNormal);
            editor.putString("dataAbnormalState", getString(R.string.rhythmNormal));
            cardAbnormalState.setCardBackgroundColor(getColor(R.color.groupCyan));
        }

        editor.apply();
    }

    //酒精浓度圆环更新
    private void updateProgressBarAlcoholConcentration(Float alcoholConcentration) {
        progressBarAlcoholConcentration.setProgressWithAnimation(alcoholConcentration, (long) 2000);
    }

    //血压柱状图数据更新
    private void updateChartBloodPressure(int highData, int lowData) {
        final String[] chartBloodPressureAxis = new String[]{"收缩压", "舒张压"};

        List<Column> columnList = new ArrayList<>();
        List<SubcolumnValue> subColumnValueList;
        List<AxisValue> axisValue = new ArrayList<>();

        for (int i=0; i<2; i++) {
            subColumnValueList = new ArrayList<>();
            if (i == 0) {
                subColumnValueList.add(new SubcolumnValue((float) highData, Color.parseColor("#FF9000")));
            }else {
                subColumnValueList.add(new SubcolumnValue((float) lowData, Color.parseColor("#99CC00")));
            }
            axisValue.add(new AxisValue(i).setLabel(chartBloodPressureAxis[i]));
            Column column = new Column(subColumnValueList);
            column.setHasLabels(true);
            columnList.add(column);
        }

        //柱状图数据
        ColumnChartData columnChartData = new ColumnChartData(columnList);

        //坐标轴设置
        Axis axisX = new Axis(axisValue);
        Axis axisY = new Axis().setHasLines(true);
        axisX.setTextColor(Color.parseColor("#FFFFFF"));
        axisX.setLineColor(Color.parseColor("#FFFFFF"));
        axisY.setTextColor(Color.parseColor("#FFFFFF"));
        axisY.setLineColor(Color.parseColor("#FFFFFF"));
        columnChartData.setAxisXBottom(axisX);
        columnChartData.setAxisYLeft(axisY);

        chartBloodPressure.setZoomEnabled(false);
        chartBloodPressure.setColumnChartData(columnChartData);

        Viewport viewport = chartBloodPressure.getMaximumViewport();
        viewport.top = 180;
        chartBloodPressure.setCurrentViewport(viewport);
    }

    //发送Notification
    private void createNotification() {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    MESSAGE_CHANNEL,
                    MESSAGE_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, MESSAGE_CHANNEL);
        String title = getString(R.string.notificationTitle);
        String text = getString(R.string.notificationContent);
        builder.setSmallIcon(R.drawable.picture_location)
                .setContentTitle(title)
                .setContentText(text);
        PendingIntent contentIntent = toHome();
        builder.setContentIntent(contentIntent);
        builder.setAutoCancel(true);
        notificationManagerCompat.notify(0, builder.build());
    }

    //创建前往主界面的Intent
    private PendingIntent toHome() {
        Intent intent = null;
        try {
            intent = new Intent(this, Class.forName("com.example.drivingmonitor.HomeFunctionManagement.ActivityHome"));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addCategory("android.intent.category.LAUNCHER");
            intent.setAction("android.intent.action.MAIN");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return PendingIntent.getActivity(this, 0, intent, 0);
    }

    //消息下载广播监听
    private BroadcastReceiver realTimeDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String realTimeData = intent.getStringExtra("realTimeData");
            assert realTimeData != null;
            String[] dataList = realTimeData.split(" ");
            updateData(dataList[0], dataList[1], dataList[2], dataList[3],
                    dataList[4], dataList[5], dataList[6], dataList[7]);
            Log.d("更新", "执行一次");
        }
    };

    //销毁Home广播监听
    private BroadcastReceiver destroyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    private BroadcastReceiver stopMonitorReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            monitorState = false;
        }
    };

    private void registerReceiver() {
        registerReceiver(realTimeDataReceiver, new IntentFilter("com.example.drivingmonitor.realTimeData"));
        registerReceiver(destroyReceiver, new IntentFilter("com.example.drivingmonitor.destroyHome"));
        registerReceiver(stopMonitorReceiver, new IntentFilter("com.example.drivingmonitor.stopMonitor"));
    }

    private void startMonitor() {
        monitorState = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                DatabaseConnector databaseConnector = new DatabaseConnector();
                String memberUser = sharedPreferencesFamilyMember.getString("memberUser", getString(R.string.noAbnormalState));
                while (monitorState) {
                    int result = databaseConnector.downloadData(ActivityHome.this, memberUser);
                    if (result == QUERY_SUCCESS) {
                        Log.d("查询", "成功");
                    }else {
                        Log.d("查询", "失败");
                    }
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Log.d("线程", "Over");
//                String memberUid = sharedPreferencesFamilyMember.getString("memberUid", getString(R.string.noAbnormalState));
//                while (monitorState) {
//                    HttpUtils.Download(memberUid, new Callback() {
//                        @Override
//                        public void onFailure(Call call, IOException e) {
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Toast.makeText(ActivityHome.this, "数据获取失败", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                        }
//
//                        @Override
//                        public void onResponse(Call call, Response response) throws IOException {
//                            String responseData = response.body().string();
//                            Log.d("Test", responseData);
//                            JSONObject jsonObject = null;
//                            try {
//                                jsonObject = new JSONObject(responseData);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                            assert jsonObject != null;
//                            try {
//                                final String status = jsonObject.getString("status");
////                                runOnUiThread(new Runnable() {
////                                    @Override
////                                    public void run() {
////                                        Toast.makeText(ActivityHome.this, message, Toast.LENGTH_SHORT).show();
////                                    }
////                                });
//                                if (status.equals("true")) {
//                                    JSONObject jsonData = jsonObject.getJSONObject("data");
//                                    String alcoholConcentration = jsonData.getString("alcohol_concentration");
//                                    String heartRate = jsonData.getString("heart_rate");
//                                    String bloodOxygen = jsonData.getString("blood_oxygen");
//                                    String microCirculation = jsonData.getString("micro_circulation");
//                                    String highPressure = jsonData.getString("high_pressure");
//                                    String lowPressure = jsonData.getString("low_pressure");
//                                    String fatigueState = jsonData.getString("fatigue");
//                                    String abnormalState = jsonData.getString("abnormal");
//                                    String currentLocation = jsonData.getString("location");
//                                    String realTimeData = alcoholConcentration + " " + heartRate + " " + bloodOxygen +
//                                            " " + microCirculation + " " + highPressure + " " + lowPressure +
//                                            " " + fatigueState + " " + abnormalState;
//                                    Log.d("Data", realTimeData);
//                                    Intent intent = new Intent();
//                                    intent.setAction("com.example.drivingmonitor.realTimeData");
//                                    intent.putExtra("realTimeData", realTimeData);
//                                    sendBroadcast(intent);
//                                    Intent location = new Intent();
//                                    intent.setAction("com.example.drivingmonitor.currentLocation");
//                                    intent.putExtra("currentLocation", currentLocation);
//                                    sendBroadcast(location);
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
//
//                    try {
//                        Thread.sleep(5000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
            }
        }).start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        sharedPreferencesAccountInformation = getSharedPreferences("AccountInformation", MODE_PRIVATE);
        drawerAccountName.setText(sharedPreferencesAccountInformation.getString("AccountName", getString(R.string.accountNameInitially)));
        homeAccountName.setText(sharedPreferencesAccountInformation.getString("AccountName", getString(R.string.accountNameInitially)));
        sharedPreferencesFamilyMember = getSharedPreferences("FamilyMember", MODE_PRIVATE);
        String memberUser = sharedPreferencesFamilyMember.getString("memberUser", getString(R.string.noAbnormalState));
        currentMember.setText(memberUser);
        assert memberUser != null;
        if (!memberUser.equals(getString(R.string.noAbnormalState)) && !monitorState) {
            startMonitor();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(realTimeDataReceiver);
        unregisterReceiver(destroyReceiver);
    }
}