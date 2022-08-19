package com.example.drivingmonitor.CustomView;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import java.text.DecimalFormat;

public class TextViewAnimationInt {

    public static void runAnimation(TextView textView, int startNumber, int endNumber) {
        NumberEvaluator numberEvaluator = new NumberEvaluator(startNumber, endNumber);
        ValueAnimator valueAnimator = ValueAnimator.ofObject(numberEvaluator, textView);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(2000);
        valueAnimator.start();
    }

    static class NumberEvaluator implements TypeEvaluator<Object> {
        int startNumber;
        int endNumber;

        public NumberEvaluator(int startNumber, int endNumber) {
            this.startNumber = startNumber;
            this.endNumber = endNumber;
        }

        @Override
        public Object evaluate(float fraction, Object startValue, Object endValue) {
            TextView textView = (TextView) endValue;
            DecimalFormat decimalFormat = new DecimalFormat("#0");
            textView.setText(decimalFormat.format(startNumber + (endNumber - startNumber) * fraction));
            return startValue;
        }
    }
}
