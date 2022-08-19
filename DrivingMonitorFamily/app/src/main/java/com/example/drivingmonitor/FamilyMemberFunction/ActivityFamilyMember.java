package com.example.drivingmonitor.FamilyMemberFunction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.drivingmonitor.HttpUtils.HttpUtils;
import com.example.drivingmonitor.LoginRegisterManagement.ActivityRegister;
import com.example.drivingmonitor.LoginRegisterManagement.DatabaseConnector;
import com.example.drivingmonitor.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.example.drivingmonitor.LoginRegisterManagement.DatabaseConnector.CONNECTION_FAIL;
import static com.example.drivingmonitor.LoginRegisterManagement.DatabaseConnector.QUERY_FAIL;
import static com.example.drivingmonitor.LoginRegisterManagement.DatabaseConnector.QUERY_SUCCESS;

public class ActivityFamilyMember extends AppCompatActivity {
    private List<CardMemberClass> listContact = new ArrayList<>();
    private ListView listView;

    private SharedPreferences sharedPreferencesContactCatalog;
    private SharedPreferences sharedPreferencesFamilyMember;

    private FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_member);
        TextView textView = findViewById(R.id.activityTitle);
        textView.setText(R.string.familyMember);

        //绑定悬浮按钮并定义其点击事件
        floatingActionButton = findViewById(R.id.FABAddContact);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddWindow();
            }
        });

        Init();

        //绑定listView及其适配器
        listView = findViewById(R.id.listContact);
        CardMemberAdapter cardContactAdapter = new CardMemberAdapter(listContact);
        listView.setAdapter(cardContactAdapter);

        //定义长按时间
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                showDeleteWindow(i);
                return false;
            }
        });
    }

    //初始化函数
    private void Init() {
        sharedPreferencesContactCatalog = getSharedPreferences("ContactCatalog", MODE_PRIVATE);
        String contactQuantity = sharedPreferencesContactCatalog.getString("contactQuantity", "0");
        assert contactQuantity != null;
        for (int i = 0; i<Integer.parseInt(contactQuantity); i++) {
            String storageName = sharedPreferencesContactCatalog.getString(i +"Name", "");
            String storageNumber = sharedPreferencesContactCatalog.getString(i+"Number", "");
            listContact.add(new CardMemberClass(storageName, storageNumber));
        }

        sharedPreferencesFamilyMember = getSharedPreferences("FamilyMember", MODE_PRIVATE);
        String memberUser = sharedPreferencesFamilyMember.getString("memberUser", getString(R.string.noAbnormalState));
        assert memberUser != null;
        if (!memberUser.equals(getString(R.string.noAbnormalState))) {
            floatingActionButton.setVisibility(View.GONE);
        }
    }

    //添加弹窗
    private void showAddWindow() {
        View view = View.inflate(ActivityFamilyMember.this, R.layout.member_add_window, null);

        //定义弹出窗口，绑定视图并显示
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ActivityFamilyMember.this, R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

        ImageButton cancel = view.findViewById(R.id.cancelAddContact);
        ImageButton confirm = view.findViewById(R.id.confirmAddContact);

        final EditText contactName = view.findViewById(R.id.editContactName);
        final EditText contactNumber = view.findViewById(R.id.editContactNumber);

        //取消按钮点击事件
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });

        //确认按钮点击事件
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (contactName.length() == 0) {    //判断是否输入姓名
                    Toast toast = Toast.makeText(view.getContext(), "", Toast.LENGTH_SHORT);
                    toast.setText("请输入账号");
                    toast.show();
                }else if (contactNumber.length() == 0) {    //判断是否输入联系方式
                    Toast toast = Toast.makeText(view.getContext(), "", Toast.LENGTH_SHORT);
                    toast.setText("请输入密码");
                    toast.show();
                }else if (contactName.length() != 11) {   //判断输入的联系方式是否有误
                    Toast toast = Toast.makeText(view.getContext(), "", Toast.LENGTH_SHORT);
                    toast.setText("请输入正确的账号");
                    toast.show();
                }else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final String storageUser = contactName.getText().toString();
                            final String storagePassword = contactNumber.getText().toString();
                            DatabaseConnector databaseConnector = new DatabaseConnector();
                            int result = databaseConnector.loginFamily(storageUser, storagePassword);
                            if (result == QUERY_SUCCESS) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String storageTag = String.valueOf(listContact.size());
                                        updateContact(storageTag, storageUser, storagePassword);
                                        Toast toast = Toast.makeText(view.getContext(), "", Toast.LENGTH_SHORT);
                                        toast.setText("亲情号设置成功");
                                        toast.show();
                                        SharedPreferences.Editor editor = sharedPreferencesFamilyMember.edit();
                                        editor.putString("memberUser", storageUser);
                                        editor.apply();
                                        floatingActionButton.setVisibility(View.GONE);
                                        bottomSheetDialog.dismiss();
                                    }
                                });
                            }else if (result == QUERY_FAIL){
                                Looper.prepare();
                                Toast toast = Toast.makeText(view.getContext(), "", Toast.LENGTH_SHORT);
                                toast.setText("输入的亲情号账号密码有误，请重试");
                                toast.show();
                                Looper.loop();
                            }else if (result == CONNECTION_FAIL) {
                                Looper.prepare();
                                Toast toast = Toast.makeText(view.getContext(), "", Toast.LENGTH_SHORT);
                                toast.setText("设置失败，请重试");
                                toast.show();
                                Looper.loop();
                            }
                        }
                    }).start();
//                    final String storageUser = contactName.getText().toString();
//                    final String storagePassword = contactNumber.getText().toString();
//                    HttpUtils.Login(storageUser, storagePassword, new Callback() {
//                        @Override
//                        public void onFailure(Call call, IOException e) {
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Toast.makeText(ActivityFamilyMember.this, "亲情号设置失败，请稍后重试", Toast.LENGTH_SHORT).show();
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
//                                if (status.equals("true")) {
//                                    JSONObject jsonData = jsonObject.getJSONObject("data");
//                                    final String uid = jsonData.getString("uid");
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            String storageTag = String.valueOf(listContact.size());
//                                            updateContact(storageTag, storageUser, storagePassword);
//                                            Toast.makeText(view.getContext(), "亲情号设置成功", Toast.LENGTH_SHORT).show();
//                                            SharedPreferences.Editor editor = sharedPreferencesFamilyMember.edit();
//                                            editor.putString("memberUser", storageUser);
//                                            editor.putString("memberUid", uid);
//                                            editor.apply();
//                                            floatingActionButton.setVisibility(View.GONE);
//                                            bottomSheetDialog.dismiss();
//                                        }
//                                    });
//                                }else {
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            Toast.makeText(view.getContext(), "输入的账号或密码有误", Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
                }
            }
        });
    }

    //发送停止监测信号
    private void sendStopSignal() {
        Intent intent = new Intent();
        intent.setAction("com.example.drivingmonitor.stopMonitor");
        sendBroadcast(intent);
    }

    //删除弹窗
    private void showDeleteWindow(final int position) {
        View view = View.inflate(ActivityFamilyMember.this, R.layout.member_delete_window, null);

        //定义弹出窗口，绑定视图并显示
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ActivityFamilyMember.this, R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

        TextView deleteContact = view.findViewById(R.id.deleteContact);

        deleteContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateContact(String.valueOf(position), listContact.get(position).getName(), listContact.get(position).getNumber());
                Toast toast = Toast.makeText(view.getContext(), "", Toast.LENGTH_SHORT);
                toast.setText("亲情号删除成功");
                toast.show();
                sharedPreferencesFamilyMember.edit().clear().apply();
                sendStopSignal();
                floatingActionButton.setVisibility(View.VISIBLE);
                bottomSheetDialog.dismiss();
            }
        });
    }

    //更新联系人
    private void updateContact(@org.jetbrains.annotations.NotNull String tag, String name, String number) {
        if (tag.equals(String.valueOf(listContact.size()))) {
            listContact.add(new CardMemberClass(name, number));
            SharedPreferences.Editor editorCatalog = sharedPreferencesContactCatalog.edit();
            editorCatalog.putString(tag+"Name", name);
            editorCatalog.putString(tag+"Number", number);
            editorCatalog.putString("contactQuantity", String.valueOf(listContact.size()));
            editorCatalog.apply();
            listView.setAdapter(new CardMemberAdapter(listContact));
        }else if (Integer.parseInt(tag) < listContact.size()) {
            listContact.remove(Integer.parseInt(tag));
            SharedPreferences.Editor editorCatalog = sharedPreferencesContactCatalog.edit();
            editorCatalog.clear();
            for (int i=0; i<listContact.size(); i++) {
                editorCatalog.putString(i+"Name", listContact.get(i).getName());
                editorCatalog.putString(i+"Number", listContact.get(i).getNumber());
            }
            editorCatalog.putString("contactQuantity", String.valueOf(listContact.size()));
            editorCatalog.apply();
            listView.setAdapter(new CardMemberAdapter(listContact));
        }
    }

}