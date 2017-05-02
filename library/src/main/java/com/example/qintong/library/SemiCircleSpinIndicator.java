package com.example.qintong.library;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;

/**
 * Created by Jack on 2015/10/20.
 */
public class SemiCircleSpinIndicator extends Indicator {

    private float degress;
    private float arcWidth;
    @Override
    public void draw(Canvas canvas, Paint paint) {
        canvas.rotate(degress,centerX(),centerY());
        //canvas.drawArc(rectF,-60,30,false,paint);

        float circleSpacing=4;
        for (int i = 0 ; i < 30 ; i ++) {
            canvas.rotate(12,centerX(),centerY());
            RectF rectF=new RectF(0,0,getWidth(),getHeight());
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(4);
            canvas.drawArc(rectF,0,arcWidth,false,paint);
        }
    }

    @Override
    public ArrayList<ValueAnimator> onCreateAnimators() {
        ArrayList<ValueAnimator> animators=new ArrayList<>();
        ValueAnimator rotateAnim= ValueAnimator.ofFloat(0,180,360);
        addUpdateListener(rotateAnim,new ValueAnimator.AnimatorUpdateListener() {
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
        ValueAnimator arcAnim= ValueAnimator.ofFloat(0,12,0);
        arcAnim.setInterpolator(new LinearInterpolator());
        arcAnim.setDuration(3000);
        arcAnim.setRepeatCount(-1);
        addUpdateListener(arcAnim,new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                arcWidth= (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        animators.add(arcAnim);


        return animators;
    }

}
