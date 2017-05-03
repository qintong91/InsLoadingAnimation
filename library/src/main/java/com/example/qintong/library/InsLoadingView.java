package com.example.qintong.library;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;

public class InsLoadingView  extends View {

    Indicator mIndicator = new SemiCircleSpinIndicator();
    private float degress;
    private float arcWidth;
    private float cricleWidth;

    private boolean mShouldStartAnimationDrawable = true;

    public InsLoadingView(Context context) {
        super(context);
        postInvalidate();
        onCreateAnimators();
    }


    public InsLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        postInvalidate();
        onCreateAnimators();
     }

    public InsLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        postInvalidate();
     }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public InsLoadingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
     }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d("qintong1", "onDraw " + getWidth() + "---" + getHeight());
        //drawTrack(canvas);
        drawxxx(canvas, new Paint());
        postInvalidate();
    }

    void drawTrack(Canvas canvas) {
        final Drawable d = mIndicator;
        if (d != null) {
            // Translate canvas so a indeterminate circular progress bar with padding
            // rotates properly in its animation
            final int saveCount = canvas.save();
            canvas.translate(getPaddingLeft(), getPaddingTop());
            d.draw(canvas);
            canvas.restoreToCount(saveCount);
            if (mShouldStartAnimationDrawable) {
                ((Animatable) d).start();
                mShouldStartAnimationDrawable = false;
            }
        }
    }

    void drawxxx(Canvas canvas, Paint paint) {
        paint.setColor(Color.BLACK);
        canvas.rotate(degress,centerX(),centerY());
        //canvas.drawArc(rectF,-60,30,false,paint);
        canvas.rotate(12,centerX(),centerY());
        RectF rectF=new RectF((float) (getWidth()*0.1),(float) (getWidth()*0.1),(float) (getWidth()*0.9), (float)(getHeight()*0.9));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth((float)(getHeight()*0.02));
        canvas.drawArc(rectF,0,cricleWidth,false,paint);
        for (int i = 0 ; i < 30 ; i ++) {
            canvas.rotate(12,centerX(),centerY());
             paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.BLUE);
            paint.setStrokeWidth((float)(getHeight()*0.02));
            canvas.drawArc(rectF,0,arcWidth,false,paint);
        }

    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final int measuredWidth = resolveSizeAndState(300, widthMeasureSpec, 0);
        final int measuredHeight = resolveSizeAndState(300, heightMeasureSpec, 0);
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        updateDrawableState();
    }

    private void updateDrawableState() {
        final int[] state = getDrawableState();
        if (mIndicator != null && mIndicator.isStateful()) {
            mIndicator.setState(state);
        }
    }
    protected float centerX() {
        return getWidth()/2;
    }

    protected float centerY() {
        return getHeight()/2;
    }

    public ArrayList<ValueAnimator> onCreateAnimators() {
        ArrayList<ValueAnimator> animators=new ArrayList<>();
        ValueAnimator rotateAnim= ValueAnimator.ofFloat(0,180,360);
        rotateAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                degress= (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        rotateAnim.setInterpolator(new LinearInterpolator());
        rotateAnim.setDuration(10000);
        rotateAnim.setRepeatCount(-1);
        animators.add(rotateAnim);
        ValueAnimator arcAnim= ValueAnimator.ofFloat(12,0,12);
        arcAnim.setInterpolator(new DeceAcceInterpolator());
        arcAnim.setDuration(3000);
        arcAnim.setRepeatCount(-1);
        arcAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                arcWidth= (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        animators.add(arcAnim);
        ValueAnimator circleDAnimator= ValueAnimator.ofFloat(-360,360);
        circleDAnimator.setInterpolator(new DeceAcceInterpolator());
        circleDAnimator.setDuration(3000);
        circleDAnimator.setRepeatCount(-1);
        circleDAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                cricleWidth = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });

        rotateAnim.start();
        arcAnim.start();
        circleDAnimator.start();
        return animators;
    }

    public class DeceAcceInterpolator implements Interpolator {
        @Override
        public float getInterpolation(float input) {
            return ((4*input-2)*(4*input-2)*(4*input-2))/16f + 0.5f;
        }
    }
}
