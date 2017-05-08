package com.example.qintong.library;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;

public class InsLoadingView  extends View {
    private static String TAG = "InsLoadingView";
    private static boolean DEBUG = true;

    private float degress;
    private float arcWidth;
    private float cricleWidth;

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
        if (DEBUG)  {
            Log.d(TAG, "onDraw " + getWidth() + "---" + getHeight());
        }
        drawTrack(canvas, new Paint());
        postInvalidate();
    }

    void drawTrack(Canvas canvas, Paint paint) {
        paint.setColor(Color.BLACK);
        canvas.rotate(degress,centerX(),centerY());
        //canvas.drawArc(rectF,-60,30,false,paint);
        canvas.rotate(12,centerX(),centerY());
        RectF rectF=new RectF((float) (getWidth()*0.2),(float) (getWidth()*0.2),(float) (getWidth()*0.8), (float)(getHeight()*0.8));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth((float)(getHeight()*0.02));
        //canvas.drawArc(rectF,0,cricleWidth,false,paint);
        if (DEBUG)  {
            Log.d(TAG, "cricleWidth:" + cricleWidth);
        }
        if (cricleWidth < 0) {
            canvas.drawArc(rectF,cricleWidth,0-cricleWidth,false,paint);
            float adjustCricleWidth = cricleWidth + 360;
            float width = 8;
            while (adjustCricleWidth > 0) {
                width = width - 0.2f;
                adjustCricleWidth = adjustCricleWidth -12;
                canvas.drawArc(rectF,adjustCricleWidth ,width ,false,paint);
            }
        } else {
            for (int i = 0; i <= 4 ; i ++) {
                if (12*i > cricleWidth) {
                    break;
                }
                canvas.drawArc(rectF,cricleWidth - 12*i,8 + i,false,paint);
            }
            if (cricleWidth > 48 ) {
                canvas.drawArc(rectF,0,cricleWidth - 48,false,paint);
            }
            float adjustCricleWidth = 360;
            float width = 8 * (360 - cricleWidth)/360;
            if (DEBUG)  {
                Log.d(TAG, "width:" + width);
            }
            while (width > 0) {
                width = width - 0.2f;
                adjustCricleWidth = adjustCricleWidth  - 12;
                canvas.drawArc(rectF, adjustCricleWidth ,width ,false,paint);
            }
        }

/*        for (int i = 0 ; i < 30 ; i ++) {
            canvas.rotate(12,centerX(),centerY());
             paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.BLUE);
            paint.setStrokeWidth((float)(getHeight()*0.02));
            canvas.drawArc(rectF,0,arcWidth,false,paint);
        }*/

    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final int measuredWidth = resolveSizeAndState(300, widthMeasureSpec, 0);
        final int measuredHeight = resolveSizeAndState(300, heightMeasureSpec, 0);
        setMeasuredDimension(measuredWidth, measuredHeight);
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
        ValueAnimator arcAnim= ValueAnimator.ofFloat(12,0);
        arcAnim.setInterpolator(new LinearInterpolator());
        arcAnim.setDuration(1500);
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
        circleDAnimator.setInterpolator(new LinearInterpolator());
        circleDAnimator.setDuration(12000);
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
