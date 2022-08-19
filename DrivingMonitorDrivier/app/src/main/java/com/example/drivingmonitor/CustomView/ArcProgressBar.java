package com.example.drivingmonitor.CustomView;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Point;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.drivingmonitor.R;

public class ArcProgressBar extends View {
    private boolean mAntiAlias;

    //动画绘制
    private long mAnimationDuration;
    private ValueAnimator mAnimator;

    //圆弧进度条绘制
    private Point mArcCenterPointer;
    private float mRadius;
    private Paint mArcForeground;
    private float mArcWidth;
    private Paint mArcBackground;
    private int mArcColor;

    //刻度线绘制
    private Paint mScalePainter;
    private float mScaleWidth;
    private float mScaleInterVal;
    private int mScaleColor;

    //文字内容绘制
    private TextPaint mHintPainter;
    private String mHInt;
    private float mHintSize;
    private int mHintColor;

    //数值内容绘制
    private Paint mValuePainter;
    private float mValue;
    private float mValueSize;
    private int mValueColor;

    //单位内容绘制
    private Paint mUnitPainter;
    private String mUnit;
    private float mUnitSize;
    private int mUnitColor;

    public ArcProgressBar(Context context) {
        this(context, null);
    }

    public ArcProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ArcProgressBar, defStyleAttr, 0);

        typedArray.recycle();
    }
}
