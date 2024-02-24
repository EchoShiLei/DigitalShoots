package com.digital.shoots.stats;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Typeface;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.renderer.LineChartRenderer;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class LineCharTextRenderer extends LineChartRenderer {
    private ILineDataSet mDataSet;
    private Entry mMaxEntry;

    public LineCharTextRenderer(LineDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
    }

    @Override
    protected void drawDataSet(Canvas c, ILineDataSet dataSet) {
        super.drawDataSet(c, dataSet);
        float yMax = dataSet.getYMax();
        int boundsRangeCount = mXBounds.range + mXBounds.min;
//        if (boundsRangeCount < 1) {
//            return;
//        }
        mDataSet = dataSet;
        for (int j = mXBounds.min; j < boundsRangeCount; j++) {
            Entry entry = dataSet.getEntryForIndex(j);
            if (entry.getY() == yMax) {
                mMaxEntry = entry;
                return;
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

        drawMaxCircles(c, mDataSet, mCirclesBuffer);
        drawMaxText(c, mCirclesBuffer);
//        drawMaxBar(c, mCirclesBuffer);
    }

    private void drawMaxBar(Canvas c, float[] mCirclesBuffer) {
        Paint barPaint = new Paint();
        float y = mMaxEntry.getY();
        float left = mCirclesBuffer[0] - 8;
        float right = mCirclesBuffer[0] + 8;
        float top = mCirclesBuffer[1] - y + 40;
        float bottom = mCirclesBuffer[1] + y - 40;

        LinearGradient gradient = new LinearGradient(0, 0, 0, bottom,
                Color.BLUE, Color.RED, Shader.TileMode.MIRROR);
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
}
