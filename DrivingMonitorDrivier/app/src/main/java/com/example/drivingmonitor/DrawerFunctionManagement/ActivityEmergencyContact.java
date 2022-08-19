package com.example.drivingmonitor.DrawerFunctionManagement;

import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.drivingmonitor.CustomFunction.CallRobot;
import com.example.drivingmonitor.EmergencyContactFunction.CardContactAdapter;
import com.example.drivingmonitor.EmergencyContactFunction.CardContactClass;
import com.example.drivingmonitor.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ActivityEmergencyContact extends AppCompatActivity {

    private List<CardContactClass> listContact = new ArrayList<>();
    private ListView listView;

    private SharedPreferences sharedPreferencesContactCatalog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contact);
        TextView textView = findViewById(R.id.activityTitle);
        textView.setText(R.string.emergencyContact);

        //绑定悬浮按钮并定义其点击事件
        final FloatingActionButton floatingActionButton = findViewById(R.id.FABAddContact);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddWindow();
            }
        });

        Init();

        //绑定listView及其适配器
        listView = findViewById(R.id.listContact);
        CardContactAdapter cardContactAdapter = new CardContactAdapter(listContact);
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
            listContact.add(new CardContactClass(storageName, storageNumber));
        }
    }

    //添加弹窗
    private void showAddWindow() {
        View view = View.inflate(ActivityEmergencyContact.this, R.layout.contact_add_window, null);

        //定义弹出窗口，绑定视图并显示
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ActivityEmergencyContact.this, R.style.BottomSheetDialogTheme);
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
            public void onClick(View view) {
                if (contactName.length() == 0) {    //判断是否输入姓名
                    Toast toast = Toast.makeText(view.getContext(), "", Toast.LENGTH_SHORT);
                    toast.setText("请输入姓名");
                    toast.show();
                }else if (contactNumber.length() == 0) {    //判断是否输入联系方式
                    Toast toast = Toast.makeText(view.getContext(), "", Toast.LENGTH_SHORT);
                    toast.setText("请输入联系方式");
                    toast.show();
                }else if (contactNumber.length() != 11) {   //判断输入的联系方式是否有误
                    Toast toast = Toast.makeText(view.getContext(), "", Toast.LENGTH_SHORT);
                    toast.setText("请输入正确的联系方式");
                    toast.show();
                }else { //保存联系人
                    String storageName = contactName.getText().toString();
                    String storageNumber = contactNumber.getText().toString();
                    String storageTag = String.valueOf(listContact.size());
                    updateContact(storageTag, storageName, storageNumber);
                    Toast toast = Toast.makeText(view.getContext(), "", Toast.LENGTH_SHORT);
                    toast.setText("紧急联系人创建成功");
                    toast.show();
                    bottomSheetDialog.dismiss();
                }
            }
        });
    }

    //删除弹窗
    private void showDeleteWindow(final int position) {
        View view = View.inflate(ActivityEmergencyContact.this, R.layout.contact_delete_window, null);

        //定义弹出窗口，绑定视图并显示
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ActivityEmergencyContact.this, R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

        TextView deleteContact = view.findViewById(R.id.deleteContact);

        deleteContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateContact(String.valueOf(position), listContact.get(position).getName(), listContact.get(position).getNumber());
                Toast toast = Toast.makeText(view.getContext(), "", Toast.LENGTH_SHORT);
                toast.setText("紧急联系人删除成功");
                toast.show();
                bottomSheetDialog.dismiss();
            }
        });
    }

    //更新联系人
    private void updateContact(@org.jetbrains.annotations.NotNull String tag, String name, String number) {
        if (tag.equals(String.valueOf(listContact.size()))) {
            listContact.add(new CardContactClass(name, number));
            SharedPreferences.Editor editorCatalog = sharedPreferencesContactCatalog.edit();
            editorCatalog.putString(tag+"Name", name);
            editorCatalog.putString(tag+"Number", number);
            editorCatalog.putString("contactQuantity", String.valueOf(listContact.size()));
            editorCatalog.apply();
            listView.setAdapter(new CardContactAdapter(listContact));
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
            listView.setAdapter(new CardContactAdapter(listContact));
        }
    }
}