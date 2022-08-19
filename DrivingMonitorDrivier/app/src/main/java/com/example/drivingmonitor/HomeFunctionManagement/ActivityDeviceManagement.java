package com.example.drivingmonitor.HomeFunctionManagement;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.drivingmonitor.HttpUtils.HttpUtils;
import com.example.drivingmonitor.LoginRegisterManagement.ActivityLogin;
import com.example.drivingmonitor.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ActivityDeviceManagement extends AppCompatActivity {
    private static final String DEMO_DATA = "1 72 96 81 124 66";

    private static final String TAG = "Bluetooth Discovery";
    private static final String RASPBERRY_ADDRESS = "E4:5F:01:55:9C:89";
    private static final int ENABLE_BLUETOOTH = 1;

    private TextView stateRaspberry;
    private TextView stateCamera;
    private TextView stateSensor;
    private TextView stateWatch;

    private ImageView imageStateRaspberry;
    private ImageView imageStateCamera;
    private ImageView imageStateSensor;
    private ImageView imageStateWatch;

    private ProgressBar progressBarRaspberry;
    private ProgressBar progressBarCamera;
    private ProgressBar progressBarSensor;
    private ProgressBar progressBarWatch;

    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothSocket communicationSocket;

    private IntentFilter intentFilter = new IntentFilter();
    private boolean bluetoothSwitchState = false;

//    private List<BluetoothDevice> bluetoothFoundDeviceList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_management);
        final TextView textView = findViewById(R.id.activityTitle);
        textView.setText(R.string.deviceManagement);
        ImageView imageView = findViewById(R.id.customIcon);
        imageView.setVisibility(View.VISIBLE);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                startActivity(intent);
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        String[] demoDataList = new String[]{"1.2 72 96 81 124 66 6 6", "2.3 76 97 81 116 56 2 6", "3.5 78 96 88 120 65 6 2"};
//                        for (int i=0; i<3; i++) {
//                            try {
//                                Thread.sleep(6000);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                            sendSocketMessageBroadcast(demoDataList[i]);
//                        }
//                    }
//                }).start();

            }
        });

        stateRaspberry = findViewById(R.id.stateRaspberry);
        stateCamera = findViewById(R.id.stateCamera);
        stateSensor = findViewById(R.id.stateSensor);
        stateWatch = findViewById(R.id.stateWatch);

        imageStateRaspberry = findViewById(R.id.stateImageRaspberry);
        imageStateCamera = findViewById(R.id.stateImageCamera);
        imageStateSensor = findViewById(R.id.stateImageSensor);
        imageStateWatch = findViewById(R.id.stateImageWatch);

        progressBarRaspberry = findViewById(R.id.progressBarRaspberry);
        progressBarCamera = findViewById(R.id.progressBarCamera);
        progressBarSensor = findViewById(R.id.progressBarSensor);
        progressBarWatch = findViewById(R.id.progressBarWatch);

        CardView cardRaspberry = findViewById(R.id.cardRaspberry);
        cardRaspberry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stateRaspberry.setText(R.string.connecting);
                imageStateRaspberry.setVisibility(View.INVISIBLE);
                progressBarRaspberry.setVisibility(View.VISIBLE);
                connectRaspberry();

//                Timer timer = new Timer();
//                TimerTask timerTaskRaspberry = new TimerTask() {
//                    @Override
//                    public void run() {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(ActivityDeviceManagement.this, "树莓派连接成功！", Toast.LENGTH_LONG).show();
//                                stateRaspberry.setText(R.string.connection);
//                                imageStateRaspberry.setImageResource(R.drawable.state_success);
//                                progressBarRaspberry.setVisibility(View.INVISIBLE);
//                                imageStateRaspberry.setVisibility(View.VISIBLE);
//                                stateCamera.setText(R.string.connectingDevice);
//                                imageStateCamera.setVisibility(View.INVISIBLE);
//                                progressBarCamera.setVisibility(View.VISIBLE);
//                                stateSensor.setText(R.string.connectingDevice);
//                                imageStateSensor.setVisibility(View.INVISIBLE);
//                                progressBarSensor.setVisibility(View.VISIBLE);
//                                stateWatch.setText(R.string.connectingDevice);
//                                imageStateWatch.setVisibility(View.INVISIBLE);
//                                progressBarWatch.setVisibility(View.VISIBLE);
//                            }
//                        });
//                    }
//                };
//                TimerTask timerTaskCamera = new TimerTask() {
//                    @Override
//                    public void run() {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                stateCamera.setText(R.string.connectionDevice);
//                                imageStateCamera.setImageResource(R.drawable.state_success);
//                                progressBarCamera.setVisibility(View.INVISIBLE);
//                                imageStateCamera.setVisibility(View.VISIBLE);
//                            }
//                        });
//                    }
//                };
//                TimerTask timerTaskSensor = new TimerTask() {
//                    @Override
//                    public void run() {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                stateSensor.setText(R.string.connectionDevice);
//                                imageStateSensor.setImageResource(R.drawable.state_success);
//                                progressBarSensor.setVisibility(View.INVISIBLE);
//                                imageStateSensor.setVisibility(View.VISIBLE);
////                                new Thread(new Runnable() {
////                                    @Override
////                                    public void run() {
////                                        String[] demoDataList = new String[]{"1.2 72 96 81 124 66", "2.3 76 97 81 116 56", "3.5 78 96 88 120 65"};
////                                        for (int i=0; i<3; i++) {
////                                            try {
////                                                Thread.sleep(3600);
////                                            } catch (InterruptedException e) {
////                                                e.printStackTrace();
////                                            }
////                                            sendSocketMessageBroadcast(demoDataList[i]);
////                                        }
////                                    }
////                                }).start();
//                            }
//                        });
//                    }
//                };
//                TimerTask timerTaskWatch = new TimerTask() {
//                    @Override
//                    public void run() {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                stateWatch.setText(R.string.connectionDevice);
//                                imageStateWatch.setImageResource(R.drawable.state_success);
//                                progressBarWatch.setVisibility(View.INVISIBLE);
//                                imageStateWatch.setVisibility(View.VISIBLE);
//                            }
//                        });
//                    }
//                };
//                timer.schedule(timerTaskRaspberry, 1000);
//                timer.schedule(timerTaskCamera, 2000);
//                timer.schedule(timerTaskSensor, 3000);
//                timer.schedule(timerTaskWatch, 2500);
            }
        });

        CardView cardCamera = findViewById(R.id.cardCamera);
        cardCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stateCamera.setText(R.string.connectingDevice);
                imageStateCamera.setVisibility(View.INVISIBLE);
                progressBarCamera.setVisibility(View.VISIBLE);
                Timer timer = new Timer();
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                stateCamera.setText(R.string.disconnection);
                                imageStateCamera.setVisibility(View.VISIBLE);
                                progressBarCamera.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                };
                timer.schedule(timerTask, 2000);
            }
        });

        CardView cardSensor = findViewById(R.id.cardSensor);
        cardSensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stateSensor.setText(R.string.connectingDevice);
                imageStateSensor.setVisibility(View.INVISIBLE);
                progressBarSensor.setVisibility(View.VISIBLE);
                Timer timer = new Timer();
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                stateSensor.setText(R.string.disconnection);
                                imageStateSensor.setVisibility(View.VISIBLE);
                                progressBarSensor.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                };
                timer.schedule(timerTask, 2000);
            }
        });

        CardView cardWatch = findViewById(R.id.cardWatch);
        cardWatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stateWatch.setText(R.string.connectingDevice);
                imageStateWatch.setVisibility(View.INVISIBLE);
                progressBarWatch.setVisibility(View.VISIBLE);
                Timer timer = new Timer();
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                stateWatch.setText(R.string.disconnection);
                                imageStateWatch.setVisibility(View.VISIBLE);
                                progressBarWatch.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                };
                timer.schedule(timerTask, 2000);
            }
        });

        registerReceiver();
        turnOnBluetooth();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ENABLE_BLUETOOTH:
                if (resultCode == RESULT_OK) {
                    //蓝牙启用后操作
                    connectRaspberry();
                }else {
                    Toast.makeText(this, "蓝牙启用失败...", Toast.LENGTH_SHORT).show();
                }
        }
    }

    //打开蓝牙
    private void turnOnBluetooth() {
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "设备不支持蓝牙...", Toast.LENGTH_SHORT).show();
        }else if (!bluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, ENABLE_BLUETOOTH);
        }else {
            //蓝牙启用后操作
            connectRaspberry();
        }
    }

    //蓝牙开始扫描
//    private void turnOnDiscovery() {
//        if (bluetoothAdapter.isEnabled() && !bluetoothAdapter.isDiscovering()) {
//            bluetoothAdapter.startDiscovery();
//        }
//    }

    //蓝牙停止扫描
    private void turnOffDiscovery() {
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
    }

    //连接Raspberry
    private void connectRaspberry() {
        try {
            turnOffDiscovery();
            BluetoothDevice Raspberry = bluetoothAdapter.getRemoteDevice(RASPBERRY_ADDRESS);
            Method getConnectionState = BluetoothDevice.class.getDeclaredMethod("isConnected", (Class[]) null);
            getConnectionState.setAccessible(true);
            boolean connectionState = (boolean) getConnectionState.invoke(Raspberry, (Object[]) null);
            if (connectionState) {
                establishSocket(Raspberry);
                Log.d("Connection", "Raspberry连接成功");
            }else {
//                Toast.makeText(this, "请先在右上角蓝牙设置中连接Raspberry", Toast.LENGTH_SHORT).show();
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    //建立通信Socket
    private void establishSocket(BluetoothDevice bluetoothDevice) {
        try {
            communicationSocket = (BluetoothSocket) bluetoothDevice.getClass().
                    getDeclaredMethod("createRfcommSocket", new Class[]{int.class}).invoke(bluetoothDevice, 1);
            Log.d("Connection", "创建Socket成功");

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (communicationSocket != null) {
                            communicationSocket.connect();
                            Log.d("Connection", "两方Socket连接成功");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ActivityDeviceManagement.this, "树莓派连接成功！", Toast.LENGTH_LONG).show();
                                    stateRaspberry.setText(R.string.connection);
                                    imageStateRaspberry.setImageResource(R.drawable.state_success);
                                    progressBarRaspberry.setVisibility(View.INVISIBLE);
                                    imageStateRaspberry.setVisibility(View.VISIBLE);
                                    stateCamera.setText(R.string.connectingDevice);
                                    imageStateCamera.setVisibility(View.INVISIBLE);
                                    progressBarCamera.setVisibility(View.VISIBLE);
                                    stateSensor.setText(R.string.connectingDevice);
                                    imageStateSensor.setVisibility(View.INVISIBLE);
                                    progressBarSensor.setVisibility(View.VISIBLE);
                                    stateWatch.setText(R.string.connectingDevice);
                                    imageStateWatch.setVisibility(View.INVISIBLE);
                                    progressBarWatch.setVisibility(View.VISIBLE);
                                    Timer timer = new Timer();
                                    TimerTask timerTaskCamera = new TimerTask() {
                                        @Override
                                        public void run() {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    stateCamera.setText(R.string.connectionDevice);
                                                    imageStateCamera.setImageResource(R.drawable.state_success);
                                                    progressBarCamera.setVisibility(View.INVISIBLE);
                                                    imageStateCamera.setVisibility(View.VISIBLE);
                                                }
                                            });
                                        }
                                    };
                                    TimerTask timerTaskSensor = new TimerTask() {
                                        @Override
                                        public void run() {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    stateSensor.setText(R.string.connectionDevice);
                                                    imageStateSensor.setImageResource(R.drawable.state_success);
                                                    progressBarSensor.setVisibility(View.INVISIBLE);
                                                    imageStateSensor.setVisibility(View.VISIBLE);
                                                }
                                            });
                                        }
                                    };
                                    TimerTask timerTaskWatch = new TimerTask() {
                                        @Override
                                        public void run() {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    stateWatch.setText(R.string.connectionDevice);
                                                    imageStateWatch.setImageResource(R.drawable.state_success);
                                                    progressBarWatch.setVisibility(View.INVISIBLE);
                                                    imageStateWatch.setVisibility(View.VISIBLE);
                                                }
                                            });
                                        }
                                    };
                                    timer.schedule(timerTaskCamera, 1000);
                                    timer.schedule(timerTaskSensor, 2500);
                                    timer.schedule(timerTaskWatch, 2000);
                                }
                            });
                            ReceiveAndSendMessage();
                            Log.d("Thread", "线程结束");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            Log.d("Connection", "请求连接失败");
        }
    }

    //收发消息
    private void ReceiveAndSendMessage() {
        try {
            InputStream inputStream = communicationSocket.getInputStream(); //获得输入流
            while (communicationSocket.isConnected()) {
                if (inputStream.available() == 0) {
                    inputStream = communicationSocket.getInputStream();
                    Log.d("Monitor", "正在监听端口信息");
                    Thread.sleep(1000); //监听时间间隔
                }else {
                    int available = inputStream.available();
                    byte[] buffer = new byte[available];
                    int result = inputStream.read(buffer, 0, available);
                    String string = new String(buffer);
                    Log.d("Message", string + "    Result: " + result);
                    sendSocketMessageBroadcast(string);
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    stateRaspberry.setText(R.string.disconnection);
                    imageStateRaspberry.setImageResource(R.drawable.state_fail);
                    stateCamera.setText(R.string.disconnection);
                    imageStateCamera.setImageResource(R.drawable.state_fail);
                    stateSensor.setText(R.string.disconnection);
                    imageStateSensor.setImageResource(R.drawable.state_fail);
                    stateWatch.setText(R.string.disconnection);
                    imageStateWatch.setImageResource(R.drawable.state_fail);
                }
            });
            Log.d("Monitor", "停止监听端口信息");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    //监听器注册
    private void registerReceiver() {
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(monitorDiscovery, intentFilter);
    }

    //扫描监视器
    private BroadcastReceiver monitorDiscovery = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            assert action != null;
            switch (action) {
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int bluetoothState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    switch (bluetoothState) {
                        case BluetoothAdapter.STATE_ON:
                            Toast.makeText(ActivityDeviceManagement.this, "蓝牙已启用", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "蓝牙启用");
                            bluetoothSwitchState = true;
                            break;
                        case BluetoothAdapter.STATE_OFF:
                            Toast.makeText(ActivityDeviceManagement.this, "蓝牙已关闭", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "蓝牙关闭");
                            bluetoothSwitchState = false;
                            stateRaspberry.setText(R.string.disconnection);
                            imageStateRaspberry.setImageResource(R.drawable.state_fail);
                            progressBarRaspberry.setVisibility(View.INVISIBLE);
                            imageStateRaspberry.setVisibility(View.VISIBLE);
                            imageStateCamera.setImageResource(R.drawable.state_fail);
                            progressBarCamera.setVisibility(View.INVISIBLE);
                            imageStateCamera.setVisibility(View.VISIBLE);
                            imageStateSensor.setImageResource(R.drawable.state_fail);
                            progressBarSensor.setVisibility(View.INVISIBLE);
                            imageStateSensor.setVisibility(View.VISIBLE);
                            imageStateWatch.setImageResource(R.drawable.state_fail);
                            progressBarWatch.setVisibility(View.INVISIBLE);
                            imageStateWatch.setVisibility(View.VISIBLE);
                            break;
                    }
                    break;
//                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
//                    Toast.makeText(ActivityDeviceManagement.this, "蓝牙扫描开始", Toast.LENGTH_SHORT).show();
//                    Log.d(TAG, "蓝牙扫描开始");
//                    break;
//                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
//                    Toast.makeText(ActivityDeviceManagement.this, "蓝牙扫描完成", Toast.LENGTH_SHORT).show();
//                    Log.d(TAG, "蓝牙扫描结束");
//                    for (int i=0; i<bluetoothFoundDeviceList.size(); i++) {
//                        if (bluetoothFoundDeviceList.get(i).getName() != null) {
//                            Log.d(TAG, bluetoothFoundDeviceList.get(i).getName()+" "+bluetoothFoundDeviceList.get(i).getAddress()+" "+bluetoothFoundDeviceList.get(i).getBondState());
//                        }
//
//                    }
//                    break;
//                case BluetoothDevice.ACTION_FOUND:
//                    BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                    if (!bluetoothFoundDeviceList.contains(bluetoothDevice)) {
//                        bluetoothFoundDeviceList.add(bluetoothDevice);
//                    }
//                    Log.d(TAG, String.valueOf(bluetoothFoundDeviceList.size()));
//                    break;
            }
        }
    };

    //发送树莓派数据广播
    private void sendSocketMessageBroadcast(String socketMessage) {
        Intent intent = new Intent();
        intent.setAction("com.example.drivingmonitor.raspberryData");
        intent.putExtra("socketMessage", socketMessage);
        sendBroadcast(intent);
        Log.d("Broadcast", "成功发送一条信息信息广播");
    }

    //获取已绑定设备
    private void getBondedDevice() {
        Set<BluetoothDevice> bondedDeviceSet = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice bluetoothDevice : bondedDeviceSet) {
            //获取连接状态
            try {
                Method getConnectionState = BluetoothDevice.class.getDeclaredMethod("isConnected", (Class[]) null);
                getConnectionState.setAccessible(true);
                boolean connectionState = (boolean) getConnectionState.invoke(bluetoothDevice, (Object[]) null);
                if (connectionState) {
                    Log.d("Device", bluetoothDevice.getName() + " " + bluetoothDevice.getAddress() + " " + bluetoothDevice.getType());
                    establishSocket(bluetoothDevice);
                    break;
                }
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!bluetoothSwitchState) {
            unregisterReceiver(monitorDiscovery);
        }
    }
}