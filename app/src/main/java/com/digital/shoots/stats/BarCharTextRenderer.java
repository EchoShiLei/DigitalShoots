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
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.renderer.BarChartRenderer;
import com.github.mikephil.charting.renderer.LineChartRenderer;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class BarCharTextRenderer extends BarChartRenderer {
    private IBarDataSet mDataSet;
    private Entry mMaxEntry;
    private Entry mMinEntry;
    private int bottomLocation;

    public BarCharTextRenderer(BarDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
    }

    @Override
    protected void drawDataSet(Canvas c, IBarDataSet dataSet, int index) {
        super.drawDataSet(c, dataSet, index);

        float yMax = dataSet.getYMax();
        float yMin = dataSet.getYMin();
        mDataSet = dataSet;
        ;
        Log.d("ZZQ", "index:" + index + " yMax:" + yMax);
        for (int j = 0; j < dataSet.getEntryCount(); j++) {
            Entry entry = dataSet.getEntryForIndex(j);
            if (entry.getY() == yMax) {
                mMaxEntry = entry;
            }
            if (entry.getY() == yMin) {
                mMinEntry = entry;
            }
        }
    }

    @Override
    public void drawExtras(Canvas c) {
        super.drawExtras(c);
        if (mAnimator == null || mMaxEntry == null || mDataSet == null) {
            return;
        }
        float phaseY = mAnimator.getPhaseY();
        float[] mCirclesBuffer = new float[2];
        mCirclesBuffer[0] = mMaxEntry.getX();
        mCirclesBuffer[1] = mMaxEntry.getY() * phaseY;
        Transformer trans = mChart.getTransformer(mDataSet.getAxisDependency());
        trans.pointValuesToPixel(mCirclesBuffer);

        drawMaxText(c, mCirclesBuffer);
    }


    private void drawMaxText(Canvas c, float[] circlesBuffer) {
        Paint txtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        txtPaint.setColor(Color.parseColor("#FC4545"));
        txtPaint.setStyle(Paint.Style.FILL);
        txtPaint.setTextSize(50);
        txtPaint.setTypeface(Typeface.DEFAULT_BOLD);
        String txt1 = String.valueOf((int) mMaxEntry.getY());
        float txt1Width = txtPaint.measureText(txt1);
        c.drawText(txt1, circlesBuffer[0] - txt1Width, circlesBuffer[1] - 50, txtPaint);

        txtPaint.setColor(Color.parseColor("#FC4545"));
        txtPaint.setStyle(Paint.Style.FILL);
        txtPaint.setTextSize(24);
        txtPaint.setTypeface(Typeface.DEFAULT_BOLD);
        String txt2 = "km/h";
        float txt2Width = txtPaint.measureText(txt2);
        c.drawText(txt2, circlesBuffer[0], circlesBuffer[1] - 50, txtPaint);

        txtPaint.setColor(Color.parseColor("#FC4545"));
        txtPaint.setStyle(Paint.Style.FILL);
        txtPaint.setTextSize(30);
        String txt3 = "Best";
        float txt3Width = txtPaint.measureText(txt3);
        c.drawText(txt3, circlesBuffer[0] - txt3Width, circlesBuffer[1] - 20, txtPaint);


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
