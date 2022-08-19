package com.example.drivingmonitor.LoginRegisterManagement;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

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
import static com.example.drivingmonitor.LoginRegisterManagement.DatabaseConnector.INSERT_FAIL;
import static com.example.drivingmonitor.LoginRegisterManagement.DatabaseConnector.INSERT_SUCCESS;

public class ActivityRegister extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final ImageView exitRegister = findViewById(R.id.exitRegister);
        final TextInputEditText phoneNumberEdit = findViewById(R.id.phoneNumberRegisterEdit);
        final TextInputEditText passwordEdit = findViewById(R.id.passwordRegisterEdit);
        final TextInputEditText passwordConfirmEdit = findViewById(R.id.passwordConfirmEdit);
        final Button entranceLogin = findViewById(R.id.entranceLogin);
        final CheckBox checkBox = findViewById(R.id.registerProtocolCheckbox);

        exitRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        entranceLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String phoneNumber = Objects.requireNonNull(phoneNumberEdit.getText()).toString();
                final String password = Objects.requireNonNull(passwordEdit.getText()).toString();
                final String passwordConfirm = Objects.requireNonNull(passwordConfirmEdit.getText()).toString();
                if (phoneNumber.length() == 0) {
                    Toast.makeText(ActivityRegister.this, "请输入手机号", Toast.LENGTH_SHORT).show();
                }else if (phoneNumber.length() != 11) {
                    Toast.makeText(ActivityRegister.this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
                }else if (password.length() == 0) {
                    Toast.makeText(ActivityRegister.this, "请输入密码", Toast.LENGTH_SHORT).show();
                }else if (!password.equals(passwordConfirm)) {
                    Toast.makeText(ActivityRegister.this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
                }else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            DatabaseConnector databaseConnector = new DatabaseConnector();
                            int result = databaseConnector.register(phoneNumber, password);
                            if (result == INSERT_SUCCESS) {
                                Log.d("注册", "Success...");
                                Looper.prepare();
                                Toast.makeText(ActivityRegister.this, "注册成功", Toast.LENGTH_SHORT).show();
                                ActivityRegister.this.finish();
                                Looper.loop();
                            }else if (result == INSERT_FAIL) {
                                Log.d("注册", "Fail...");
                                Looper.prepare();
                                Toast.makeText(ActivityRegister.this, "注册失败，请重试", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }else if (result == CONNECTION_FAIL) {
                                Looper.prepare();
                                Toast.makeText(ActivityRegister.this, "连接服务器失败，请重试", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                        }
                    }).start();
//                    HttpUtils.Register(phoneNumber, password, new Callback() {
//                        @Override
//                        public void onFailure(Call call, IOException e) {
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Toast.makeText(ActivityRegister.this, "注册失败，请稍后重试", Toast.LENGTH_SHORT).show();
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
//                                final String msg = jsonObject.getString("msg");
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Toast.makeText(ActivityRegister.this, msg, Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                                if (status.equals("true")) {
//                                    finish();
//                                }
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
                }
            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    entranceLogin.setEnabled(true);
                }else {
                    entranceLogin.setEnabled(false);
                }
            }
        });
    }
}