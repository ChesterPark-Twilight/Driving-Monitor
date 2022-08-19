package com.example.drivingmonitor.LoginRegisterManagement;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.drivingmonitor.HomeFunctionManagement.ActivityHome;
import com.example.drivingmonitor.HttpUtils.HttpUtils;
import com.example.drivingmonitor.R;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.example.drivingmonitor.LoginRegisterManagement.DatabaseConnector.CONNECTION_FAIL;
import static com.example.drivingmonitor.LoginRegisterManagement.DatabaseConnector.QUERY_FAIL;
import static com.example.drivingmonitor.LoginRegisterManagement.DatabaseConnector.QUERY_SUCCESS;

public class ActivityLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final ImageView exitLogin = findViewById(R.id.exitLogin);
        final TextInputEditText userEdit = findViewById(R.id.userLoginEdit);
        final TextInputEditText passwordEdit = findViewById(R.id.passwordLoginEdit);
        final CheckBox rememberPassword = findViewById(R.id.passwordRememberCheckbox);
        final TextView entranceRegister = findViewById(R.id.entranceRegister);
        final Button entranceHome = findViewById(R.id.entranceHome);
        final CheckBox checkBox = findViewById(R.id.loginProtocolCheckbox);

        final SharedPreferences sharedPreferences = getSharedPreferences("RememberAccount", MODE_PRIVATE);
        String userRemember = sharedPreferences.getString("rememberUser", "");
        String passwordRemember = sharedPreferences.getString("rememberPassword", "");
        String uidRemember = sharedPreferences.getString("rememberUid", "");

        exitLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

//        final Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                final String user = Objects.requireNonNull(userEdit.getText()).toString();
//                final String password = Objects.requireNonNull(passwordEdit.getText()).toString();
//                if (user.length() == 0) {
//                    Toast.makeText(ActivityLogin.this, "请输入账号", Toast.LENGTH_SHORT).show();
//                } else if (password.length() == 0) {
//                    Toast.makeText(ActivityLogin.this, "请输入密码", Toast.LENGTH_SHORT).show();
//                } else {
//                    DatabaseConnector DatabaseConnector = new DatabaseConnector();
//                    int result = DatabaseConnector.login(user, password);
//                    if (result == QUERY_SUCCESS) {
//                        Log.d("Login", "Success...");
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (rememberPassword.isChecked()) {
//                                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                                    editor.putString("rememberUser", user);
//                                    editor.putString("rememberPassword", password);
//                                    editor.apply();
//                                }
//                                Toast.makeText(ActivityLogin.this, "登录成功", Toast.LENGTH_SHORT).show();
//                                Intent intent = new Intent(ActivityLogin.this, ActivityHome.class);
//                                intent.putExtra("user", user);
//                                startActivity(intent);
//                                finish();
//                            }
//                        });
//                    }else if (result == QUERY_FAIL) {
//                        Log.d("Query", "Fail...");
//                        Looper.prepare();
//                        Toast.makeText(ActivityLogin.this, "输入的账号密码有误，请重试", Toast.LENGTH_SHORT).show();
//                        Looper.loop();
//                    }else if (result == CONNECTION_FAIL) {
//                        Log.d("Connection", "Fail...");
//                        Looper.prepare();
//                        Toast.makeText(ActivityLogin.this, "连接服务器失败，请重试", Toast.LENGTH_SHORT).show();
//                        Looper.loop();
//                    }
//                }
//            }
//        });

        entranceRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityLogin.this, ActivityRegister.class);
                startActivity(intent);
            }
        });

        entranceHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final String user = Objects.requireNonNull(userEdit.getText()).toString();
                        final String password = Objects.requireNonNull(passwordEdit.getText()).toString();
                        if (user.length() == 0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ActivityLogin.this, "请输入账号", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else if (password.length() == 0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ActivityLogin.this, "请输入密码", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            DatabaseConnector DatabaseConnector = new DatabaseConnector();
                            int result = DatabaseConnector.login(user, password);
                            if (result == QUERY_SUCCESS) {
                                Log.d("Login", "Success...");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (rememberPassword.isChecked()) {
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("rememberUser", user);
                                            editor.putString("rememberPassword", password);
                                            editor.apply();
                                        }
                                        Toast.makeText(ActivityLogin.this, "登录成功", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(ActivityLogin.this, ActivityHome.class);
                                        intent.putExtra("user", user);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                            }else if (result == QUERY_FAIL) {
                                Log.d("Query", "Fail...");
                                Looper.prepare();
                                Toast.makeText(ActivityLogin.this, "输入的账号密码有误，请重试", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }else if (result == CONNECTION_FAIL) {
                                Log.d("Connection", "Fail...");
                                Looper.prepare();
                                Toast.makeText(ActivityLogin.this, "连接服务器失败，请重试", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
//                            HttpUtils.Login(user, password, new Callback() {
//                                @Override
//                                public void onFailure(Call call, IOException e) {
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            Toast.makeText(ActivityLogin.this, "登陆失败，请稍后重试", Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
//                                }
//
//                                @Override
//                                public void onResponse(Call call, Response response) throws IOException {
//                                    String responseData = response.body().string();
//                                    JSONObject jsonObject = null;
//                                    try {
//                                        jsonObject = new JSONObject(responseData);
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
//                                    assert jsonObject != null;
//                                    try {
//                                        final String status = jsonObject.getString("status");
//                                        final String message = jsonObject.getString("msg");
//                                        runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                Toast.makeText(ActivityLogin.this, message, Toast.LENGTH_SHORT).show();
//                                            }
//                                        });
//                                        if (status.equals("true")) {
//                                            JSONObject jsonData = jsonObject.getJSONObject("data");
//                                            String uid = jsonData.getString("uid");
//                                            if (rememberPassword.isChecked()) {
//                                                SharedPreferences.Editor editor = sharedPreferences.edit();
//                                                editor.putString("rememberUser", user);
//                                                editor.putString("rememberPassword", password);
//                                                editor.putString("rememberUid", uid);
//                                                editor.apply();
//                                            }
//                                            Intent intent = new Intent(ActivityLogin.this, ActivityHome.class);
//                                            intent.putExtra("uid", uid);
//                                            startActivity(intent);
//                                            finish();
//                                        }
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            });
                        }
                    }
                }).start();
            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    entranceHome.setEnabled(true);
                }else {
                    entranceHome.setEnabled(false);
                }
            }
        });

        assert userRemember != null;
        if (!userRemember.equals("")) {
            userEdit.setText(userRemember);
            passwordEdit.setText(passwordRemember);
            Intent intent = getIntent();
            if (intent.getBooleanExtra("loginDirectly", false)) {
                Intent destination = new Intent(ActivityLogin.this, ActivityHome.class);
//                destination.putExtra("uid", uidRemember);
                destination.putExtra("user", userRemember);
                startActivity(destination);
                finish();
            }
        }
    }
}