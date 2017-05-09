package com.example.qintong.library;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;

import static android.graphics.Shader.TileMode.CLAMP;

public class InsLoadingView extends View {
    private static String TAG = "InsLoadingView";
    private static boolean DEBUG = true;
    private double circleDia = 0.9;
    private float strokeWidth = 0.025f;
    private float arcChangeAngle = 0.2f;

    private float degress;
    private float arcWidth;
    private float cricleWidth;
    boolean isFirstCircle = true;

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

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (DEBUG) {
            Log.d(TAG, "onDraw " + getWidth() + "---" + getHeight());
        }
        Paint paint = getPaint(getColor(0), getColor(360) , 360);
        drawTrack(canvas, paint);
        postInvalidate();
    }

    void drawTrack(Canvas canvas, Paint paint) {
        canvas.rotate(degress, centerX(), centerY());
        canvas.rotate(12, centerX(), centerY());
        RectF rectF = new RectF((float) (getWidth() * (1 - circleDia)), (float) (getWidth() * (1 - circleDia)),
                (float) (getWidth() * circleDia), (float) (getHeight() * circleDia));
        if (DEBUG) {
            Log.d(TAG, "cricleWidth:" + cricleWidth);
        }
        if (cricleWidth < 0) {
            //a
            float startArg = cricleWidth+ 360;
            canvas.drawArc(rectF, startArg, 360 - startArg, false, paint);
            float adjustCricleWidth = cricleWidth + 360;
            float width = 8;
            while (adjustCricleWidth > 12) {
                width = width - arcChangeAngle;
                adjustCricleWidth = adjustCricleWidth - 12;
                canvas.drawArc(rectF, adjustCricleWidth, width, false, paint);
            }
        } else {
            //b
            for (int i = 0; i <= 4; i++) {
                if (12 * i > cricleWidth) {
                    break;
                }
                canvas.drawArc(rectF, cricleWidth - 12 * i, 8 + i, false, paint);
            }
            if (cricleWidth > 48) {
                canvas.drawArc(rectF, 0, cricleWidth - 48, false, paint);
            }
            float adjustCricleWidth = 360;
            float width = 8 * (360 - cricleWidth) / 360;
            if (DEBUG) {
                Log.d(TAG, "width:" + width);
            }
            while (width > 0 && adjustCricleWidth > 12) {
                width = width - arcChangeAngle;
                adjustCricleWidth = adjustCricleWidth - 12;
                Log.d(TAG, "adjustCricleWidth:" + adjustCricleWidth);
                canvas.drawArc(rectF, adjustCricleWidth, width, false, paint);
            }
        }
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final int measuredWidth = resolveSizeAndState(300, widthMeasureSpec, 0);
        final int measuredHeight = resolveSizeAndState(300, heightMeasureSpec, 0);
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    protected float centerX() {
        return getWidth() / 2;
    }

    protected float centerY() {
        return getHeight() / 2;
    }

    public ArrayList<ValueAnimator> onCreateAnimators() {
        ArrayList<ValueAnimator> animators = new ArrayList<>();
        ValueAnimator rotateAnim = ValueAnimator.ofFloat(0, 180, 360);
        rotateAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                degress = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        rotateAnim.setInterpolator(new LinearInterpolator());
        rotateAnim.setDuration(10000);
        rotateAnim.setRepeatCount(-1);
        animators.add(rotateAnim);
        ValueAnimator arcAnim = ValueAnimator.ofFloat(12, 0);
        ValueAnimator circleDAnimator = ValueAnimator.ofFloat(0, 360);
        circleDAnimator.setInterpolator(new DecelerateInterpolator());
        circleDAnimator.setDuration(3000);
        circleDAnimator.setRepeatCount(-1);
        circleDAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (isFirstCircle) {
                    cricleWidth = (float) animation.getAnimatedValue();
                } else {
                    cricleWidth = (float) animation.getAnimatedValue() - 360;
                }
                Log.d(TAG, "cricleWidth: " + cricleWidth + " isFirstCircle:" + isFirstCircle);
                postInvalidate();
            }
        });
        circleDAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                isFirstCircle = !isFirstCircle;
            }
        });

        rotateAnim.start();
        arcAnim.start();
        circleDAnimator.start();
        return animators;
    }

    private static int getColor(double degree) {
        if (degree < 0 || degree > 360) {
            Log.d(TAG, "getColor error:" + degree);
        }
        int startColor = Color.YELLOW;
        int endColor = Color.RED;
        double radio = degree/360;
        int redStart = Color.red(startColor);
        int blueStart = Color.blue(startColor);
        int greenStart = Color.green(startColor);
        int redEnd = Color.red(endColor);
        int blueEnd = Color.blue(endColor);
        int greenEnd = Color.green(endColor);
        int red = (int) (redStart + ((redEnd - redStart) * radio + 0.5));
        int greed = (int) (greenStart + ((greenEnd - greenStart) * radio + 0.5));
        int blue = (int) (blueStart + ((blueEnd - blueStart) * radio + 0.5));
        return Color.argb(255,red, greed, blue);
    }

    private Paint getPaint(int startColor, int endColor, double arcWidth) {
        Paint paint = new Paint();
        Shader shader = new LinearGradient( 0f,  0f,  (float)(getWidth() * circleDia*(arcWidth - 48)/360),
                getHeight() * strokeWidth, startColor, endColor, CLAMP);
        //paint = new Paint();
        paint.setShader(shader);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(getHeight() * strokeWidth);
        return paint;
    }
}
