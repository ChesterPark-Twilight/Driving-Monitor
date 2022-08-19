package com.example.drivingmonitor.CustomFunction;

import android.content.Intent;
import android.net.Uri;

public class CallRobot {

    public CallRobot() {
    }

    public Intent Call(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri dialStatement = Uri.parse("tel:" + phoneNumber);
        intent.setData(dialStatement);

        return intent;
    }
}
