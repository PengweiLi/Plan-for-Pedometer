
package com.lipengwei.pedometer.ui;

import java.util.Calendar;
import java.util.TimeZone;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class MyChartView extends View {
    private int XPoint;
    private int YPoint;
    private int XScale;
    private int YScale;
    private int XLength;
    private int YLength;
    private String[] XLabel;
    private String[] YLabel;
    private int[] Data;

    private Paint mTextPaint;
    private Paint mCirclePaint;
    private Paint mAxlePaint;
    private Paint mPolygonPaint;

    private int mCircleColor;
    private int mAxleColor;
    private int mPolygonColor;
    private int mMax;

    private String[] week = {
            "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"
    };

    public MyChartView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initAttrs(context);
        initVariable();
    }

    private void init() {

        YLength = getHeight() - 100;
        Log.i("Lipengwei", "YLength = " + YLength);
        YPoint = getBottom() - 100;
        Log.i("Lipengwei", "YPoint = " + YPoint);
        XPoint = getLeft() + 20;
        Log.i("Lipengwei", "XPoint = " + XPoint);
        XLength = getWidth() - XPoint;
        XScale = XLength / 7 + 1;
        YScale = YLength / 11 + 1;
    }

    private void initAttrs(Context context) {
        mCircleColor = 0xFFFFFFFF;
        mPolygonColor = 0xFF00CD00;
        mAxleColor = 0xFF000000;
    }

    private void initVariable() {
        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setColor(mCircleColor);
        mCirclePaint.setStyle(Paint.Style.STROKE);

        mAxlePaint = new Paint();
        mAxlePaint.setAntiAlias(true);
        mAxlePaint.setColor(mAxleColor);
        // mAxlePaint.setStrokeWidth(5);
        mAxlePaint.setStyle(Paint.Style.FILL);

        mPolygonPaint = new Paint();
        mPolygonPaint.setAntiAlias(true);
        mPolygonPaint.setColor(mPolygonColor);
        mPolygonPaint.setStyle(Paint.Style.FILL);
        mPolygonPaint.setStrokeWidth(3);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setARGB(255, 255, 255, 255);
        mTextPaint.setTextSize(30);

    }

    public void SetInfo(int[] AllData)
    {
        Data = AllData;
        setYLabel(Data);
        setXLabel();
    }

    private void setXLabel() {
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        int weekDay = c.get(Calendar.DAY_OF_WEEK) - 1;
        XLabel = new String[7];
        System.arraycopy(week, weekDay, XLabel, 0, 7 - weekDay);
        System.arraycopy(week, 0, XLabel, 7 - weekDay, weekDay);
        for (int i = 0; i < XLabel.length; i++) {
            Log.i("Lipengwei", "XLabel = " + XLabel[i]);
        }

    }

    private void setYLabel(int[] data) {
        for (int i = 0; i < data.length; i++) {
            if (data[i] > mMax) {
                mMax = data[i];
            }
        }

        mMax = (mMax / 100 + 1) * 100;
        YLabel = new String[7];
        YLabel[0] = "";
        for (int i = 1; i < 6; i++) {
            YLabel[i] = String.valueOf(mMax / 5 * i);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        init();
        super.onDraw(canvas);

        canvas.drawLine(XPoint, YPoint - YLength, XPoint, YPoint, mAxlePaint);
        for (int i = 0; i * YScale < YLength; i++) {
            canvas.drawLine(XPoint, YPoint - i * YScale, XPoint + 5, YPoint - i * YScale,
                    mAxlePaint);
            if (i % 2 == 0) {
                canvas.drawText(YLabel[i / 2], XPoint - 53, YPoint - i * YScale + 5, mTextPaint);
            }

        }
        canvas.drawLine(XPoint, YPoint - YLength, XPoint - 3, YPoint - YLength + 6, mAxlePaint);
        canvas.drawLine(XPoint, YPoint - YLength, XPoint + 3, YPoint - YLength + 6, mAxlePaint);

        canvas.drawLine(XPoint, YPoint, XPoint + XLength, YPoint, mAxlePaint);
        for (int i = 0; i * XScale < XLength; i++) {
            canvas.drawLine(XPoint + i * XScale, YPoint, XPoint + i * XScale, YPoint - 5,
                    mAxlePaint);

            canvas.drawText(XLabel[i], XPoint + i * XScale - 10, YPoint + 30, mTextPaint);

            if (i > 0 && Data[i - 1] >= 0)
                canvas.drawLine(XPoint + (i - 1) * XScale, YCoord(Data[i - 1]), XPoint + i
                        * XScale, YCoord(Data[i]), mPolygonPaint);
            canvas.drawCircle(XPoint + i * XScale, YCoord(Data[i]), 5, mCirclePaint);

        }
        canvas.drawLine(XPoint + XLength, YPoint, XPoint + XLength - 6, YPoint - 3, mAxlePaint);
        canvas.drawLine(XPoint + XLength, YPoint, XPoint + XLength - 6, YPoint + 3, mAxlePaint);
    }

    private int YCoord(int y) {
        return YPoint - y * YScale / (mMax / 10);
    }
}
