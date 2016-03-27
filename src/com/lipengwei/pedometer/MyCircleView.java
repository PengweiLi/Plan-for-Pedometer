package com.lipengwei.pedometer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.FontMetrics;
import android.util.AttributeSet;
import android.view.View;

public class MyCircleView extends View {

    private Paint mCirclePaint;
    private Paint mRingPaint;
    private Paint mTextPaint;
    private Paint mRingBackPaint;
    private int mCircleColor;
    private int mRingColor;
    private int mRingBackColor;
    private float mRadius;
    private float mRingRadius;
    private float mStrokeWidth;
    private int mXCenter;
    private int mYCenter;
    private float mTxtWidth;
    private float mTxtHeight;
    private int mTotalProgress = 100;
    private int mProgress;

    public MyCircleView(Context context, AttributeSet attrs) {
            super(context, attrs);
            initAttrs(context);
            initVariable();
    }

    private void initAttrs(Context context) {
            mRadius = 120;
            mStrokeWidth = 15;
            mCircleColor = 0xFFFFAEB9;
            mRingColor = 0xFF7CFC00;
            mRingBackColor = 0xFFFFFFFF;
            
            mRingRadius = mRadius + mStrokeWidth / 2;
    }

    private void initVariable() {
            mCirclePaint = new Paint();
            mCirclePaint.setAntiAlias(true);
            mCirclePaint.setColor(mCircleColor);
            mCirclePaint.setStyle(Paint.Style.FILL);
            
            mRingPaint = new Paint();
            mRingPaint.setAntiAlias(true);
            mRingPaint.setColor(mRingColor);
            mRingPaint.setStyle(Paint.Style.STROKE);
            mRingPaint.setStrokeWidth(mStrokeWidth);
            
            mRingBackPaint = new Paint();
            mRingBackPaint.setAntiAlias(true);
            mRingBackPaint.setColor(mRingBackColor);
            mRingBackPaint.setStyle(Paint.Style.STROKE);
            mRingBackPaint.setStrokeWidth(mStrokeWidth);
            
            mTextPaint = new Paint();
            mTextPaint.setAntiAlias(true);
            mTextPaint.setStyle(Paint.Style.FILL);
            mTextPaint.setARGB(255, 255, 255, 255);
            mTextPaint.setTextSize(mRingRadius);
            FontMetrics fm = mTextPaint.getFontMetrics();
            mTxtHeight = (int) Math.ceil(fm.descent - fm.ascent);
            
    }

    @Override
    protected void onDraw(Canvas canvas) {

            mXCenter = getWidth() / 2;
            mYCenter = getHeight() / 2;
            
            canvas.drawCircle(mXCenter, mYCenter, mRadius, mCirclePaint);
            canvas.drawCircle(mXCenter, mYCenter, mRingRadius, mRingBackPaint);
            
            if (mProgress >=0 ) {
                    RectF oval = new RectF();
                    oval.left = (mXCenter - mRingRadius);
                    oval.top = (mYCenter - mRingRadius);
                    oval.right = mRingRadius * 2 + (mXCenter - mRingRadius);
                    oval.bottom = mRingRadius * 2 + (mYCenter - mRingRadius);
                    canvas.drawArc(oval, -90, ((float)mProgress / mTotalProgress) * 360, false, mRingPaint);
                    String txt = String.valueOf(mTotalProgress-mProgress);
                    mTxtWidth = mTextPaint.measureText(txt, 0, txt.length());
                    canvas.drawText(txt, mXCenter - mTxtWidth / 2, mYCenter + mTxtHeight / 4, mTextPaint);
            }
    }
    
    public void setProgress(int progress) {
            mProgress = progress;
            invalidate();
            postInvalidate();
    }
    
    public void setTotalProgress(int totalProgress) {
        mTotalProgress = totalProgress;
    }
}
