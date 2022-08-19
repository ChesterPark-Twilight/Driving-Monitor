package com.example.drivingmonitor.DrawerFunctionManagement;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.drivingmonitor.LoginRegisterManagement.ActivityLogin;
import com.example.drivingmonitor.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class ActivityAccount extends AppCompatActivity {
    public static final int MODIFY_NAME = 1;
    public static final int MODIFY_SEX = 2;
    public static final int MODIFY_AGE = 3;
    public static final int MODIFY_DRIVING_AGE = 4;

    private SharedPreferences sharedPreferencesAccountInformation;
    private SharedPreferences sharedPreferencesRememberAccount;

    TextView accountName;
    TextView accountSex;
    TextView accountAge;
    TextView accountDrivingAge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        TextView textView = findViewById(R.id.activityTitle);
        textView.setText(R.string.accountManagement);

        Init();

        //退出登录点击事件
        ConstraintLayout exitAccount = findViewById(R.id.exitAccount);
        exitAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityAccount.this, ActivityLogin.class);
                startActivity(intent);
                ActivityAccount.this.finish();
                Intent destroyHome = new Intent().setAction("com.example.drivingmonitor.destroyHome");
                sendBroadcast(destroyHome);
                sharedPreferencesRememberAccount.edit().clear().apply();
            }
        });

        final ConstraintLayout modifyName = findViewById(R.id.modifyName);
        modifyName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showModifyWindow(MODIFY_NAME);
            }
        });

        final ConstraintLayout modifySex = findViewById(R.id.modifySex);
        modifySex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showModifyWindow(MODIFY_SEX);
            }
        });

        final ConstraintLayout modifyAge = findViewById(R.id.modifyBirthday);
        modifyAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showModifyWindow(MODIFY_AGE);
            }
        });

        final ConstraintLayout modifyDrivingAge = findViewById(R.id.modifyDrivingAge);
        modifyDrivingAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showModifyWindow(MODIFY_DRIVING_AGE);
            }
        });
    }

    private void Init() {
        sharedPreferencesAccountInformation = ActivityAccount.this.getSharedPreferences("AccountInformation", MODE_PRIVATE);
        sharedPreferencesRememberAccount = getSharedPreferences("RememberAccount", MODE_PRIVATE);

        accountName = findViewById(R.id.accountName);
        accountSex = findViewById(R.id.accountSex);
        accountAge = findViewById(R.id.accountAge);
        accountDrivingAge = findViewById(R.id.accountDrivingAge);

        accountName.setText(sharedPreferencesAccountInformation.getString("AccountName", getString(R.string.accountNameInitially)));
        accountSex.setText(sharedPreferencesAccountInformation.getString("AccountSex", "未设置"));
        accountAge.setText(sharedPreferencesAccountInformation.getString("AccountAge", "未设置"));
        accountDrivingAge.setText(sharedPreferencesAccountInformation.getString("AccountDrivingAge", "未设置"));
    }

    private void showModifyWindow(final int tag) {
        View view = View.inflate(ActivityAccount.this, R.layout.account_modify_window, null);

        //定义弹出窗口，绑定视图并显示
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ActivityAccount.this, R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(view);

        //视图控件绑定
        ImageButton cancel = view.findViewById(R.id.cancelModify);
        ImageButton confirm = view.findViewById(R.id.confirmModify);
        final TextView modifyTitle = view.findViewById(R.id.modifyTitle);
        final EditText editAccountName = view.findViewById(R.id.editAccountName);
        final RadioGroup chooseAccountSex = view.findViewById(R.id.chooseAccountSex);
        final RadioButton chooseMan = view.findViewById(R.id.chooseMan);
        final RadioButton chooseWoman = view.findViewById(R.id.chooseWoman);
        final LinearLayout pickAccountAge = view.findViewById(R.id.pickAccountAge);
        final NumberPicker pickAge = view.findViewById(R.id.pickAge);
        final LinearLayout pickAccountDrivingAge = view.findViewById(R.id.pickAccountDrivingAge);
        final NumberPicker pickDrivingAge = view.findViewById(R.id.pickDrivingAge);

        //数据选择数组创建
        final List<String> ageArrayList = new ArrayList<>();
        for (int i=18; i<70; i++) {
            ageArrayList.add(String.valueOf(i));
        }
        String[] ageList = ageArrayList.toArray(new String[0]);
        final List<String> drivingAgeArrayList  =new ArrayList<>();
        for (int i=0; i<50; i++) {
            if (i == 0) {
                drivingAgeArrayList.add("<1");
            }else {
                drivingAgeArrayList.add(String.valueOf(i));
            }
        }
        String[] drivingAgeList = drivingAgeArrayList.toArray(new String[0]);

        //弹窗复用设置
        switch (tag) {
            case MODIFY_NAME:
                modifyTitle.setText("修改昵称");
                editAccountName.setVisibility(View.VISIBLE);
                break;
            case MODIFY_SEX:
                modifyTitle.setText("修改性别");
                chooseAccountSex.setVisibility(View.VISIBLE);
                break;
            case MODIFY_AGE:
                modifyTitle.setText("修改年龄");
                pickAccountAge.setVisibility(View.VISIBLE);
                pickAge.setDisplayedValues(ageList);
                pickAge.setMinValue(1);
                pickAge.setMaxValue(ageList.length);
                break;
            case MODIFY_DRIVING_AGE:
                modifyTitle.setText("修改驾龄");
                pickAccountDrivingAge.setVisibility(View.VISIBLE);
                pickDrivingAge.setDisplayedValues(drivingAgeList);
                pickDrivingAge.setMinValue(1);
                pickDrivingAge.setMaxValue(drivingAgeList.length);
                break;
            default:
                break;
        }

        //弹窗显示
        bottomSheetDialog.show();

        //取消按钮事件绑定
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });

        //确认按钮绑定事件
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferencesAccountInformation.edit();
                Toast toast = Toast.makeText(view.getContext(), "", Toast.LENGTH_SHORT);
                switch (tag) {
                    case MODIFY_NAME:
                        String newName = editAccountName.getText().toString();
                        editor.putString("AccountName", newName);
                        editor.apply();
                        toast.setText("昵称修改成功");
                        toast.show();
                        accountName.setText(newName);
                        break;
                    case MODIFY_SEX:
                        if (chooseMan.isChecked()) {
                            accountSex.setText(R.string.man);
                            editor.putString("AccountSex", getString(R.string.man));
                        }else if (chooseWoman.isChecked()){
                            accountSex.setText(R.string.woman);
                            editor.putString("AccountSex", getString(R.string.woman));
                        }
                        editor.apply();
                        toast.setText("性别修改成功");
                        toast.show();
                        break;
                    case MODIFY_AGE:
                        String newAge = ageArrayList.get(pickAge.getValue()-1);
                        editor.putString("AccountAge", newAge);
                        editor.apply();
                        toast.setText("年龄修改成功");
                        toast.show();
                        accountAge.setText(newAge);
                        break;
                    case MODIFY_DRIVING_AGE:
                        String newDrivingAge = drivingAgeArrayList.get(pickDrivingAge.getValue()-1);
                        editor.putString("AccountDrivingAge", newDrivingAge);
                        editor.apply();
                        toast.setText("驾龄修改成功");
                        toast.show();
                        accountDrivingAge.setText(newDrivingAge);
                        break;
                    default:
                        break;
                }
                bottomSheetDialog.dismiss();
            }
        });
    }
}