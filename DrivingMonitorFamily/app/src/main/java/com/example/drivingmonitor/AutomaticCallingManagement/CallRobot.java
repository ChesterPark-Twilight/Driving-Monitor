package com.example.drivingmonitor.AutomaticCallingManagement;

import android.content.Intent;
import android.net.Uri;

public class CallRobot {
    private String phoneNumber;

    public CallRobot(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Intent Call() {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri dialStatement = Uri.parse("tel:" + phoneNumber);
        intent.setData(dialStatement);

        return intent;
    }
}
