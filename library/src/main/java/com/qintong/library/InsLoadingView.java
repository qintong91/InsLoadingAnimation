package com.qintong.library;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
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
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import static android.graphics.Shader.TileMode.CLAMP;

public class InsLoadingView extends ImageView {
    private static String TAG = "InsLoadingView";
    private static boolean DEBUG = BuildConfig.DEBUG;
    private static final float ARC_WIDTH = 12;
    private static final int MIN_WIDTH = 300;
    private static final float circleDia = 0.9f;
    private static final float strokeWidth = 0.025f;
    private static final float arcChangeAngle = 0.2f;
    private static final int sClickedColor = Color.LTGRAY;

    public enum Status {LOADING, CLICKED, UNCLICKED}

    private static SparseArray<Status> sStatusArray;

    static {
        sStatusArray = new SparseArray<>(3);
        sStatusArray.put(0, Status.LOADING);
        sStatusArray.put(1, Status.CLICKED);
        sStatusArray.put(2, Status.UNCLICKED);
    }

    private Status mStatus = Status.LOADING;
    private int mRotateDuration = 10000;
    private int mCircleDuration = 2000;
    private float bitmapDia = circleDia - strokeWidth;
    private float degress;
    private float cricleWidth;
    private boolean isFirstCircle = true;
    private ValueAnimator mRotateAnim;
    private ValueAnimator mCircleAnim;
    private ValueAnimator mTouchAnim;
    private int mStartColor = Color.parseColor("#FFF700C2");
    private int mEndColor = Color.parseColor("#FFFFD900");
    private float mScale = 1f;
    private Paint mBitmapPaint;
    private Paint mTrackPaint;
    private RectF mBitmapRectF;
    private RectF mTrackRectF;

    public InsLoadingView(Context context) {
        super(context);
        init(context , null);
    }

    public InsLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public InsLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public InsLoadingView setCircleDuration(int circleDuration) {
        this.mCircleDuration = circleDuration;
        mCircleAnim.setDuration(mCircleDuration);
        return this;
    }

    public InsLoadingView setRotateDuration(int rotateDuration) {
        this.mRotateDuration = rotateDuration;
        mRotateAnim.setDuration(mRotateDuration);
        return this;
    }

    public void setStatus(Status status) {
        this.mStatus = status;
    }

    public Status getStatus() {
        return mStatus;
    }

    public void setStartColor(int startColor) {
        mStartColor = startColor;
        mTrackPaint = null;
    }

    public void setEndColor(int endColor) {
        mEndColor = endColor;
        mTrackPaint = null;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        final int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        final int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        if (DEBUG) {
            Log.d(TAG, String.format("onMeasure widthMeasureSpec: %s -- %s", widthSpecMode, widthSpecSize));
            Log.d(TAG, String.format("onMeasure heightMeasureSpec: %s -- %s", heightSpecMode, heightSpecSize));
        }
        int width;
        if (widthSpecMode == MeasureSpec.EXACTLY && heightSpecMode == MeasureSpec.EXACTLY) {
            width = Math.min(widthSpecSize, heightSpecSize);
        } else {
            width = Math.min(widthSpecSize, heightSpecSize);
            width = Math.min(width, MIN_WIDTH);
        }
        setMeasuredDimension(width, width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        initPaints();
        initRectFs();
        canvas.scale(mScale, mScale, centerX(), centerY());
        drawBitmap(canvas);
        switch (mStatus) {
            case LOADING:
                drawTrack(canvas, mTrackPaint);
                break;
            case UNCLICKED:
                drawCircle(canvas, mTrackPaint);
                break;
            case CLICKED:
                drawClickedircle(canvas);
                break;
        }
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        if (DEBUG) {
            Log.d(TAG, "onVisibilityChanged");
        }
        if (visibility == View.VISIBLE) {
            startAnim();
        } else {
            endAnim();
        }
        super.onVisibilityChanged(changedView, visibility);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = false;
        if (DEBUG) {
            Log.d(TAG, "onTouchEvent: " + event.getAction());
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                startDownAnim();
                result = true;
                break;
            }
            case MotionEvent.ACTION_UP: {
                startUpAnim();
                break;
            }
            case MotionEvent.ACTION_CANCEL: {
                startUpAnim();
                break;
            }
        }
        return super.onTouchEvent(event) || result;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (DEBUG) {
            Log.d(TAG, "onSizeChanged");
        }
        mBitmapRectF = null;
        mTrackRectF = null;
        mBitmapPaint = null;
        mTrackPaint = null;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        if (DEBUG) {
            Log.d(TAG, "setImageDrawable");
        }
        mBitmapPaint = null;
        super.setImageDrawable(drawable);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            praseAttrs(context, attrs);
        }
        onCreateAnimators();
    }

    private void praseAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.InsLoadingViewAttr);
        int startColor = typedArray.getColor(R.styleable.InsLoadingViewAttr_start_color, mStartColor);
        int endColor = typedArray.getColor(R.styleable.InsLoadingViewAttr_start_color, mEndColor);
        int circleDuration = typedArray.getInt(R.styleable.InsLoadingViewAttr_circle_duration, mCircleDuration);
        int rotateDuration = typedArray.getInt(R.styleable.InsLoadingViewAttr_rotate_duration, mRotateDuration);
        int status = typedArray.getInt(R.styleable.InsLoadingViewAttr_status, 0);
        if (DEBUG) {
            Log.d(TAG, "praseAttrs start_color: " + startColor);
            Log.d(TAG, "praseAttrs end_color: " + endColor);
            Log.d(TAG, "praseAttrs rotate_duration: " + rotateDuration);
            Log.d(TAG, "praseAttrs circle_duration: " + circleDuration);
            Log.d(TAG, "praseAttrs status: " + status);
        }
        if (circleDuration != mCircleDuration) {
            setCircleDuration(circleDuration);
        }
        if (rotateDuration != mRotateDuration) {
            setRotateDuration(rotateDuration);
        }
        setStartColor(startColor);
        setEndColor(endColor);
        setStatus(sStatusArray.get(status));
    }

    private void initPaints() {
        if (mBitmapPaint == null) {
            mBitmapPaint = getmBitmapPaint();
        }
        if (mTrackPaint == null) {
            mTrackPaint = getTrackPaint();
        }
    }

    private void initRectFs() {
        if (mBitmapRectF == null) {
            mBitmapRectF = new RectF(getWidth() * (1 - bitmapDia), getWidth() * (1 - bitmapDia),
                    getWidth() * bitmapDia, getHeight() * bitmapDia);
        }
        if (mTrackRectF == null) {
            mTrackRectF = new RectF(getWidth() * (1 - circleDia), getWidth() * (1 - circleDia),
                    getWidth() * circleDia, getHeight() * circleDia);
        }
    }

    private float centerX() {
        return getWidth() / 2;
    }

    private float centerY() {
        return getHeight() / 2;
    }

    private void onCreateAnimators() {
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
        mTouchAnim = new ValueAnimator();
        mTouchAnim.setInterpolator(new DecelerateInterpolator());
        mTouchAnim.setDuration(200);
        mTouchAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mScale = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        startAnim();
    }

    private void drawBitmap(Canvas canvas) {
        canvas.drawOval(mBitmapRectF, mBitmapPaint);
    }

    private void drawTrack(Canvas canvas, Paint paint) {
        canvas.rotate(degress, centerX(), centerY());
        canvas.rotate(ARC_WIDTH, centerX(), centerY());

        if (DEBUG) {
            Log.d(TAG, "cricleWidth:" + cricleWidth);
        }
        if (cricleWidth < 0) {
            //a
            float startArg = cricleWidth + 360;
            canvas.drawArc(mTrackRectF, startArg, 360 - startArg, false, paint);
            float adjustCricleWidth = cricleWidth + 360;
            float width = 8;
            while (adjustCricleWidth > ARC_WIDTH) {
                width = width - arcChangeAngle;
                adjustCricleWidth = adjustCricleWidth - ARC_WIDTH;
                canvas.drawArc(mTrackRectF, adjustCricleWidth, width, false, paint);
            }
        } else {
            //b
            for (int i = 0; i <= 4; i++) {
                if (ARC_WIDTH * i > cricleWidth) {
                    break;
                }
                canvas.drawArc(mTrackRectF, cricleWidth - ARC_WIDTH * i, 8 + i, false, paint);
            }
            if (cricleWidth > ARC_WIDTH * 4) {
                canvas.drawArc(mTrackRectF, 0, cricleWidth - ARC_WIDTH * 4, false, paint);
            }
            float adjustCricleWidth = 360;
            float width = 8 * (360 - cricleWidth) / 360;
            if (DEBUG) {
                Log.d(TAG, "width:" + width);
            }
            while (width > 0 && adjustCricleWidth > ARC_WIDTH) {
                width = width - arcChangeAngle;
                adjustCricleWidth = adjustCricleWidth - ARC_WIDTH;
                canvas.drawArc(mTrackRectF, adjustCricleWidth, width, false, paint);
            }
        }
    }

    private void drawCircle(Canvas canvas, Paint paint) {
        RectF rectF = new RectF(getWidth() * (1 - circleDia), getWidth() * (1 - circleDia),
                getWidth() * circleDia, getHeight() * circleDia);
        canvas.drawOval(rectF, paint);
    }

    private void drawClickedircle(Canvas canvas) {
        Paint paintClicked = new Paint();
        paintClicked.setColor(sClickedColor);
        setPaintStroke(paintClicked);
        drawCircle(canvas, paintClicked);
    }

    private void startDownAnim() {
        mTouchAnim.setFloatValues(mScale, 0.9f);
        mTouchAnim.start();

    }

    private void startUpAnim() {
        mTouchAnim.setFloatValues(mScale, 1);
        mTouchAnim.start();
    }

    private void startAnim() {
        mRotateAnim.start();
        mCircleAnim.start();
    }

    private void endAnim() {
        mRotateAnim.end();
        mCircleAnim.end();
    }

    private Paint getTrackPaint() {
        Paint paint = new Paint();
        Shader shader = new LinearGradient(0f, 0f, (getWidth() * circleDia * (360 - ARC_WIDTH * 4) / 360),
                getHeight() * strokeWidth, mStartColor, mEndColor, CLAMP);
        paint.setShader(shader);
        setPaintStroke(paint);
        return paint;
    }

    private void setPaintStroke(Paint paint) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(getHeight() * strokeWidth);
    }

    private Paint getmBitmapPaint() {
        Paint paint = new Paint();
        Drawable drawable = getDrawable();
        Matrix matrix = new Matrix();
        if (null == drawable) {
            return paint;
        }
        Bitmap bitmap = drawableToBitmap(drawable);
        BitmapShader tshader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        int bSize = Math.min(bitmap.getWidth(), bitmap.getHeight());
        float scale = getWidth() * 1.0f / bSize;
        matrix.setScale(scale, scale);
        if (bitmap.getWidth() > bitmap.getHeight()) {
            matrix.postTranslate(-(bitmap.getWidth() * scale - getWidth()) / 2, 0);
        } else {
            matrix.postTranslate(0, -(bitmap.getHeight() * scale - getHeight()) / 2);
        }
        tshader.setLocalMatrix(matrix);
        paint.setShader(tshader);
        return paint;
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
