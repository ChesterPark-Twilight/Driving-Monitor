package com.example.drivingmonitor.HomeFunctionManagement;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.drivingmonitor.CustomFunction.CallRobot;
import com.example.drivingmonitor.CustomView.TextViewAnimationFloat;
import com.example.drivingmonitor.CustomView.TextViewAnimationInt;
import com.example.drivingmonitor.DrawerFunctionManagement.ActivityAccount;
import com.example.drivingmonitor.DrawerFunctionManagement.ActivityEmergencyContact;
import com.example.drivingmonitor.DrawerFunctionManagement.ActivityLocationService;
import com.example.drivingmonitor.HttpUtils.HttpUtils;
import com.example.drivingmonitor.LoginRegisterManagement.ActivityLogin;
import com.example.drivingmonitor.LoginRegisterManagement.DatabaseConnector;
import com.example.drivingmonitor.R;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Time;
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

import static com.example.drivingmonitor.LoginRegisterManagement.DatabaseConnector.CONNECTION_FAIL;
import static com.example.drivingmonitor.LoginRegisterManagement.DatabaseConnector.UPDATE_FAIL;
import static com.example.drivingmonitor.LoginRegisterManagement.DatabaseConnector.UPDATE_SUCCESS;

public class ActivityHome extends AppCompatActivity {
    public final static int DRUNK_WARNING = 1;
    public final static int FATIGUE_WARNING = 2;
    public final static int ABNORMAL_WARNING = 3;

//    private String UID;
    private String USER;

    private DrawerLayout drawerLayout;
    private SwipeRefreshLayout homeRefreshLayout;

    private TextView drawerAccountName;
    private TextView homeAccountName;

    private CircularProgressBar progressBarAlcoholConcentration;

    private ColumnChartView chartBloodPressure;

    private SharedPreferences sharedPreferencesAccountInformation;
    private SharedPreferences sharedPreferencesHealthData;

    private final String[] chartBloodPressureAxis = new String[]{"收缩压", "舒张压"};

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

    private LocationClient locationClient;
    private String currentLocation = "location";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        LocationClient.setAgreePrivacy(true);   //同意定位政策

        Init();
    }

    private void Init() {
//        UID = getIntent().getStringExtra("uid");
//        assert UID != null;
//        Log.d("UID", UID);
        USER = getIntent().getStringExtra("user");

        drawerLayout = findViewById(R.id.drawerLayout);
        homeRefreshLayout = findViewById(R.id.homeRefreshLayout);
        homeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //关闭刷新显示
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateData("1", "0.1", "0",
                                        "0", "1", "1",
                                        "1", "1");
                                homeRefreshLayout.setRefreshing(false);
                            }
                        });
                    }
                }).start();
            }
        });

        drawerAccountName = findViewById(R.id.drawerAccountName);
        homeAccountName = findViewById(R.id.homeAccountName);

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
        final TextView entranceDeviceManagement = findViewById(R.id.entranceDeviceManagement);
        final CircleImageView entranceAccount = findViewById(R.id.entranceAccount);

        final Button entranceEmergencyContact = findViewById(R.id.entranceEmergencyContact);
        final Button entranceLocationService = findViewById(R.id.entranceLocationService);

        headSculpture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        entranceAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityHome.this, ActivityAccount.class);
                startActivity(intent);
            }
        });

        entranceDeviceManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityHome.this, ActivityDeviceManagement.class);
                startActivity(intent);
            }
        });

        entranceEmergencyContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityHome.this, ActivityEmergencyContact.class);
                startActivity(intent);
            }
        });

        entranceLocationService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityHome.this, ActivityLocationService.class);
                startActivity(intent);
            }
        });

        registerReceiver();
    }

    //更新数据函数
    private void updateData(final String newHeartRate, final String newAlcoholConcentration, final String newFatigueState,
                            final String newAbnormalState, final String newHighBloodPressure, final String newLowBloodPressure,
                            final String newMicroCirculation, final String newBloodOxygen) {

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
                playWarningVoice(DRUNK_WARNING);
                automaticCall();
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
            playWarningVoice(FATIGUE_WARNING);
        }else if (tempData == 2) {
            dataFatigueState.setText(R.string.fatigueExcessive);
            editor.putString("dataFatigueState", getString(R.string.fatigueExcessive));
            cardFatigueState.setCardBackgroundColor(getColor(R.color.groupOrange));
            playWarningVoice(FATIGUE_WARNING);
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
            playWarningVoice(ABNORMAL_WARNING);
        }else if (tempData == 2) {
            dataAbnormalState.setText(R.string.smoke);
            editor.putString("dataAbnormalState", getString(R.string.smoke));
            cardAbnormalState.setCardBackgroundColor(getColor(R.color.groupOrange));
            playWarningVoice(ABNORMAL_WARNING);
        }else if (tempData == 3) {
            dataAbnormalState.setText(R.string.drink);
            editor.putString("dataAbnormalState", getString(R.string.drink));
            cardAbnormalState.setCardBackgroundColor(getColor(R.color.groupOrange));
            playWarningVoice(ABNORMAL_WARNING);
        }else {
            dataAbnormalState.setText(R.string.rhythmNormal);
            editor.putString("dataAbnormalState", getString(R.string.rhythmNormal));
            cardAbnormalState.setCardBackgroundColor(getColor(R.color.groupCyan));
        }

        editor.apply();

        new Thread(new Runnable() {
            @Override
            public void run() {
                DatabaseConnector databaseConnector = new DatabaseConnector();
                getLocation();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d("定位", currentLocation);
                int result = databaseConnector.upLoadData(USER, newAlcoholConcentration, newHeartRate, newBloodOxygen, newMicroCirculation,
                        newHighBloodPressure, newLowBloodPressure, newFatigueState, newAbnormalState, currentLocation);
                if (result == CONNECTION_FAIL) {
                    Log.d("Upload", "网络原因");
                }else if (result == UPDATE_FAIL) {
                    Log.d("Upload", "失败");
                }else if (result == UPDATE_SUCCESS) {
                    Log.d("Upload", "成功");
                }
            }
        }).start();
//        HttpUtils.Upload(UID, newAlcoholConcentration, newHeartRate, newBloodOxygen, newMicroCirculation,
//                newHighBloodPressure, newLowBloodPressure, newFatigueState, newAbnormalState, currentLocation,
//                new Callback() {
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(ActivityHome.this, "信息上传失败", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onResponse(Call call, Response response) throws IOException {
//                        String responseData = response.body().string();
//                        JSONObject jsonObject = null;
//                        try {
//                            jsonObject = new JSONObject(responseData);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        assert jsonObject != null;
//                        try {
//                            final String status = jsonObject.getString("status");
//                            if (status.equals("false")) {
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Toast.makeText(ActivityHome.this, "信息上传失败", Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
    }

    //酒精浓度圆环更新
    private void updateProgressBarAlcoholConcentration(Float alcoholConcentration) {
        progressBarAlcoholConcentration.setProgressWithAnimation(alcoholConcentration, (long) 2000);
    }

    //血压柱状图数据更新
    private void updateChartBloodPressure(int highData, int lowData) {
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

    //蓝牙通信信息广播监听器
    private BroadcastReceiver bluetoothDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String socketMessage = intent.getStringExtra("socketMessage");
            assert socketMessage != null;
            String[] messageList = socketMessage.split(" ");  //将数据取出
            Log.d("测试", socketMessage);
            updateData(messageList[1], messageList[0],  messageList[6], messageList[7],
                    messageList[4], messageList[5], messageList[3], messageList[2]);
//            updateData(messageList[0], "1.0",  "1", "0",
//                    messageList[3], messageList[4], messageList[2], messageList[1]);
        }
    };

    //销毁Home广播监听
    private BroadcastReceiver destroyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    //注册监听器
    private void registerReceiver() {
        registerReceiver(bluetoothDataReceiver, new IntentFilter("com.example.drivingmonitor.raspberryData"));
        registerReceiver(destroyReceiver, new IntentFilter("com.example.drivingmonitor.destroyHome"));
        Log.d("Receive", "成功注册监听器");
    }

    //获得位置函数
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
                changeCurrentLocation(bdLocation);
            }
        };
        locationClient.registerLocationListener(bdAbstractLocationListener);

        locationClient.start();
    }

    private void changeCurrentLocation(BDLocation bdLocation) {
        this.currentLocation = bdLocation.getAddrStr() + " " + bdLocation.getLatitude() + " " + bdLocation.getLongitude();
    }

    //播放提示语音
    private void playWarningVoice(int type) {
        AssetManager assetManager = getResources().getAssets();
        MediaPlayer mediaPlayer = new MediaPlayer();
        String fileName;
        if (type == FATIGUE_WARNING) {
            fileName = "FatigueDrivingWarning.mp3";
        }else if (type == DRUNK_WARNING){
            fileName = "DrunkDrivingWarning.mp3";
        }else {
            fileName = "AbnormalActionWarning.mp3";
        }
        try {
            AssetFileDescriptor assetFileDescriptor = assetManager.openFd(fileName);
            mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(),
                    assetFileDescriptor.getLength());
            mediaPlayer.prepare();
            mediaPlayer.start();
            Log.d("播放", "成功");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //依次拨打紧急联系号
    private void automaticCall() {
        SharedPreferences sharedPreferencesContactCatalog = getSharedPreferences("ContactCatalog", MODE_PRIVATE);
        String contactQuantity = sharedPreferencesContactCatalog.getString("contactQuantity", "0");
        if (contactQuantity != null && !contactQuantity.equals("0")) {
            for (int i=0; i<Integer.parseInt(contactQuantity); i++) {
                String contactNumber = sharedPreferencesContactCatalog.getString(i+"Number", "");
                startActivity(new CallRobot().Call(contactNumber));
            }
        }else {
            startActivity(new CallRobot().Call("110"));
        }
    }

    //发送短信
    private void sendMessage(String number, String content) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(number, null, content, null, null);
    }

    @Override
    protected void onStart() {
        super.onStart();
        sharedPreferencesAccountInformation = getSharedPreferences("AccountInformation", MODE_PRIVATE);
        drawerAccountName.setText(sharedPreferencesAccountInformation.getString("AccountName", getString(R.string.accountNameInitially)));
        homeAccountName.setText(sharedPreferencesAccountInformation.getString("AccountName", getString(R.string.accountNameInitially)));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bluetoothDataReceiver);
        unregisterReceiver(destroyReceiver);
    }
}