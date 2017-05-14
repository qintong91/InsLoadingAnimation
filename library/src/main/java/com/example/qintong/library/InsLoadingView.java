package com.example.qintong.library;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import java.util.ArrayList;

import static android.graphics.Shader.TileMode.CLAMP;

public class InsLoadingView extends ImageView {
    private static String TAG = "InsLoadingView";
    private static boolean DEBUG = true;
    private long mRotateDuration = 10000;
    private long mCircleDuration = 2000;
    private float circleDia = 0.9f;
    private float strokeWidth = 0.025f;
    private float arcChangeAngle = 0.2f;
    private float arcWidth = 12;
    float bitmapDia = circleDia - strokeWidth;
    private float degress;
    private float cricleWidth;
    boolean isFirstCircle = true;
    private ValueAnimator mRotateAnim;
    private ValueAnimator mCircleAnim;

    public InsLoadingView(Context context) {
        super(context);
        onCreateAnimators();
    }

    public InsLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        onCreateAnimators();
    }

    public InsLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        onCreateAnimators();
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        Paint bitmapPaint = new Paint();
        setBitmapShader(bitmapPaint);
        RectF rectF = new RectF(getWidth() * (1 - bitmapDia), getWidth() * (1 - bitmapDia),
                getWidth() * bitmapDia, getHeight() * bitmapDia);
        canvas.drawOval(rectF, bitmapPaint);
        Paint paint = getPaint(getColor(0), getColor(360), 360);
        drawTrack(canvas, paint);
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        Log.d(TAG, "onVisibilityChanged");
        if (visibility == View.VISIBLE) {
            startAnim();
        } else {
            endAnim();
        }
        super.onVisibilityChanged(changedView, visibility);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        final int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        final int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        if (DEBUG) {
            Log.d(TAG, "onMeasure widthMeasureSpec:" + widthSpecMode + "--" +widthSpecSize);
            Log.d(TAG, "onMeasure heightMeasureSpec:" + heightSpecMode + "--" +heightSpecSize);
        }
        int width;
        if (widthSpecMode == MeasureSpec.EXACTLY && heightSpecMode == MeasureSpec.EXACTLY) {
            width = Math.min(widthSpecSize, heightSpecSize);
        } else {
            width = Math.min(widthSpecSize, heightSpecSize);
            width = Math.min(width, 300);
        }
        setMeasuredDimension(width, width);
    }

    protected float centerX() {
        return getWidth() / 2;
    }

    protected float centerY() {
        return getHeight() / 2;
    }

    public void onCreateAnimators() {
        mRotateAnim = ValueAnimator.ofFloat(0, 180, 360);
        mRotateAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                degress = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        mRotateAnim.setInterpolator(new LinearInterpolator());
        mRotateAnim.setDuration(mRotateDuration);
        mRotateAnim.setRepeatCount(-1);
        mCircleAnim = ValueAnimator.ofFloat(0, 360);
        mCircleAnim.setInterpolator(new DecelerateInterpolator());
        mCircleAnim.setDuration(mCircleDuration);
        mCircleAnim.setRepeatCount(-1);
        mCircleAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (isFirstCircle) {
                    cricleWidth = (float) animation.getAnimatedValue();
                } else {
                    cricleWidth = (float) animation.getAnimatedValue() - 360;
                }
                postInvalidate();
            }
        });
        mCircleAnim.addListener(new Animator.AnimatorListener() {
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
        startAnim();
    }

    private void drawTrack(Canvas canvas, Paint paint) {
        canvas.rotate(degress, centerX(), centerY());
        canvas.rotate(arcWidth, centerX(), centerY());
        RectF rectF = new RectF(getWidth() * (1 - circleDia), getWidth() * (1 - circleDia),
                getWidth() * circleDia, getHeight() * circleDia);
        if (DEBUG) {
            Log.d(TAG, "cricleWidth:" + cricleWidth);
        }
        if (cricleWidth < 0) {
            //a
            float startArg = cricleWidth + 360;
            canvas.drawArc(rectF, startArg, 360 - startArg, false, paint);
            float adjustCricleWidth = cricleWidth + 360;
            float width = 8;
            while (adjustCricleWidth > arcWidth) {
                width = width - arcChangeAngle;
                adjustCricleWidth = adjustCricleWidth - arcWidth;
                canvas.drawArc(rectF, adjustCricleWidth, width, false, paint);
            }
        } else {
            //b
            for (int i = 0; i <= 4; i++) {
                if (arcWidth * i > cricleWidth) {
                    break;
                }
                canvas.drawArc(rectF, cricleWidth - arcWidth * i, 8 + i, false, paint);
            }
            if (cricleWidth > 48) {
                canvas.drawArc(rectF, 0, cricleWidth - 48, false, paint);
            }
            float adjustCricleWidth = 360;
            float width = 8 * (360 - cricleWidth) / 360;
            if (DEBUG) {
                Log.d(TAG, "width:" + width);
            }
            while (width > 0 && adjustCricleWidth > arcWidth) {
                width = width - arcChangeAngle;
                adjustCricleWidth = adjustCricleWidth - arcWidth;
                canvas.drawArc(rectF, adjustCricleWidth, width, false, paint);
            }
        }
    }

    private void startAnim() {
        mRotateAnim.start();
        mCircleAnim.start();
    }

    private void endAnim() {
        mRotateAnim.end();
        mCircleAnim.end();
    }

    private static int getColor(double degree) {
        if (degree < 0 || degree > 360) {
            Log.w(TAG, "getColor error:" + degree);
        }
        int startColor = Color.parseColor("#FFF700C2");
        int endColor = Color.parseColor("#FFFFD900");
        double radio = degree / 360;
        int redStart = Color.red(startColor);
        int blueStart = Color.blue(startColor);
        int greenStart = Color.green(startColor);
        int redEnd = Color.red(endColor);
        int blueEnd = Color.blue(endColor);
        int greenEnd = Color.green(endColor);
        int red = (int) (redStart + ((redEnd - redStart) * radio + 0.5));
        int greed = (int) (greenStart + ((greenEnd - greenStart) * radio + 0.5));
        int blue = (int) (blueStart + ((blueEnd - blueStart) * radio + 0.5));
        return Color.argb(255, red, greed, blue);
    }

    private Paint getPaint(int startColor, int endColor, double arcWidth) {
        Paint paint = new Paint();
        Shader shader = new LinearGradient(0f, 0f, (float) (getWidth() * circleDia * (arcWidth - 48) / 360),
                getHeight() * strokeWidth, startColor, endColor, CLAMP);
        paint.setShader(shader);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(getHeight() * strokeWidth);
        return paint;
    }

    /**
     * 设置BitmapShader
     * https://my.oschina.net/zhangqie/blog/794363
     */
    private void setBitmapShader(Paint paint) {
        Drawable drawable = getDrawable();
        Matrix matrix = new Matrix();
        if (null == drawable) {
            return;
        }
        Bitmap bitmap = drawableToBitmap(drawable);
        BitmapShader tshader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        float scale = 1.0f;
        int bSize = Math.min(bitmap.getWidth(), bitmap.getHeight());
        scale = getWidth() * 1.0f / bSize;
        matrix.setScale(scale, scale);
        if (bitmap.getWidth() > bitmap.getHeight()) {
            matrix.postTranslate(-(bitmap.getWidth()*scale - getWidth())/2, 0);
        } else {
            matrix.postTranslate(0, -(bitmap.getHeight()*scale - getHeight())/2);
        }
        tshader.setLocalMatrix(matrix);
        paint.setShader(tshader);
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            return bitmapDrawable.getBitmap();
        }
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }
}
