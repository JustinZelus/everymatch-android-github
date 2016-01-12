package com.everymatch.saas.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Dor on 26/11/2015.
 */
public class PieProgressView extends View {

    private static final Float START_ANGEL = 270f;
    private float mAngle = 0f;
    private float mPhase = 0f;
    private RectF mCircleBox = new RectF();
    private Paint mArcPaint;
    private boolean mBoxSetup = false;
    private long mDuration;
    private boolean canceled;
    private  ValueAnimator valueAnimator;

    public PieProgressView(Context context) {
        super(context);
        init();
    }

    public PieProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PieProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mBoxSetup = false;
        mArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mArcPaint.setStyle(Paint.Style.FILL);
        mArcPaint.setColor(Color.BLACK);
    }

    public void setColor(int color){
        mArcPaint.setColor(color);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!mBoxSetup) {
            mBoxSetup = true;
            setupBox();
        }

        drawOuterCircle(canvas);
        drawInnerCircle(canvas);
    }

    private void drawOuterCircle(Canvas c) {
        mArcPaint.setStyle(Paint.Style.STROKE);
        float r = getRadius();
        c.drawCircle(getWidth() / 2, getHeight() / 2, r, mArcPaint);
    }

    private void drawInnerCircle(Canvas c) {
        mArcPaint.setStyle(Paint.Style.FILL);
        float angle = mAngle * mPhase;
        c.drawArc(mCircleBox, START_ANGEL, angle, true, mArcPaint);
    }

    private void setupBox() {

        int width = getWidth();
        int height = getHeight();

        float diameter = getDiameter();

        mCircleBox = new RectF(width / 2 - diameter / 2, height / 2 - diameter / 2, width / 2 + diameter / 2, height / 2 + diameter / 2);
    }

    public void showProgress(float toShow, float total, long duration) {
        this.mDuration = duration;
        mAngle = calcAngle(toShow / total * 100f);
        startAnim();
    }

    public void startAnim() {
        mPhase = 0f;

        valueAnimator = new ValueAnimator();
        valueAnimator.setFloatValues(0, 1);
        valueAnimator.setDuration(mDuration);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mPhase = (Float) animation.getAnimatedValue();
                invalidate();
            }
        });

        valueAnimator.start();
    }

    public float getDiameter() {
        return Math.min(getWidth(), getHeight());
    }

    public float getRadius() {
        return getDiameter() / 2f;
    }

    private float calcAngle(float percent) {
        return percent / 100f * 360f;
    }

    public void cancelAnimation() {
        this.canceled = true;
        valueAnimator.cancel();
    }
}
