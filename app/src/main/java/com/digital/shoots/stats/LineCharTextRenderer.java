package com.digital.shoots.stats;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.Log;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.renderer.LineChartRenderer;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.Arrays;

public class LineCharTextRenderer extends LineChartRenderer {
    private ILineDataSet mDataSet;
    private Entry mMaxEntry;
    private Entry mMinEntry;
    private int bottomLocation;

    public LineCharTextRenderer(LineDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
    }

    @Override
    protected void drawDataSet(Canvas c, ILineDataSet dataSet) {
        super.drawDataSet(c, dataSet);
        float yMax = dataSet.getYMax();
        float yMin = dataSet.getYMin();
        Log.e("ZZQ", "yMax" + yMax);
        int boundsRangeCount = mXBounds.range + mXBounds.min;
        mDataSet = dataSet;
        for (int j = mXBounds.min; j < boundsRangeCount; j++) {
            Entry entry = dataSet.getEntryForIndex(j);
            Log.e("ZZQ", "entry:" + entry.getY());
            if (entry.getY() == yMax) {
                mMaxEntry = entry;
            }
            if (entry.getY() == yMin) {
                mMinEntry = entry;
            }
        }
        Log.e("ZZQ", "MaxEntry:" + mMaxEntry.getY() + " MinEntry:" + mMinEntry.getY());
    }

    @Override
    public void drawExtras(Canvas c) {
        super.drawExtras(c);
        if (mAnimator == null || mMaxEntry == null || mDataSet == null) {
            Log.e("ZZQ", "return" + (mAnimator == null) + "*" + (mMaxEntry == null) + "*" + (mDataSet == null));
            return;
        }
        float phaseY = mAnimator.getPhaseY();
        float[] mCirclesBuffer = new float[2];
        mCirclesBuffer[0] = mMaxEntry.getX();
        mCirclesBuffer[1] = mMaxEntry.getY() * phaseY;
        Transformer trans = mChart.getTransformer(mDataSet.getAxisDependency());
        trans.pointValuesToPixel(mCirclesBuffer);

        drawMaxBar(c, mCirclesBuffer);
        drawMaxCircles(c, mDataSet, mCirclesBuffer);
        drawMaxText(c, mCirclesBuffer);
    }

    private void drawMaxBar(Canvas c, float[] mCirclesBuffer) {
        Paint barPaint = new Paint();
        float y = mMaxEntry.getY();
        float left = mCirclesBuffer[0] - 10;
        float right = mCirclesBuffer[0] + 10;
        float top = mCirclesBuffer[1] - y;
        float bottom = mCirclesBuffer[1] + y + bottomLocation;
        float temp = mCirclesBuffer[1] / (bottom - top);

        int[] colors = {Color.parseColor("#33f00001"),
                Color.parseColor("#66f00001"),
                Color.parseColor("#33f00001")};
        float[] pos = {0, temp, 1};
        Log.d("ZZQ", "temp:" + temp + "mCirclesBuffer[1]:" + mCirclesBuffer[1] + "height:" + (bottom - top));
        LinearGradient gradient = new LinearGradient(0, 0, 0, mCirclesBuffer[1],
                colors, pos, Shader.TileMode.CLAMP);
        barPaint.setShader(gradient);

        c.drawRect(left, top, right, bottom, barPaint);
    }

    private void drawMaxText(Canvas c, float[] circlesBuffer) {
        Paint txtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        txtPaint.setColor(Color.parseColor("#FC4545"));
        txtPaint.setStyle(Paint.Style.FILL);
        txtPaint.setTextSize(50);
        txtPaint.setTypeface(Typeface.DEFAULT_BOLD);
        String txt1 = String.valueOf((int) mMaxEntry.getY());
        float txt1Width = txtPaint.measureText(txt1) + 20;
        c.drawText(txt1, circlesBuffer[0] - txt1Width, circlesBuffer[1] - 20, txtPaint);

        txtPaint.setColor(Color.parseColor("#FC4545"));
        txtPaint.setStyle(Paint.Style.FILL);
        txtPaint.setTextSize(30);
        String txt2 = "Scores";
        float txt2Width = txtPaint.measureText(txt2) + 20;
        c.drawText(txt2, circlesBuffer[0] - txt2Width, circlesBuffer[1] + 10, txtPaint);


    }

    private void drawMaxCircles(Canvas c, ILineDataSet dataSet, float[] circlesBuffer) {
        float circleRadius = dataSet.getCircleRadius();

        Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(Color.parseColor("#FC4545"));
        circlePaint.setStrokeWidth(5);
        circlePaint.setStyle(Paint.Style.STROKE);

        c.drawCircle(circlesBuffer[0], circlesBuffer[1], circleRadius, circlePaint);
    }

    public void setBottomLocation(int bottomLocation) {
        this.bottomLocation = bottomLocation;
    }
}
