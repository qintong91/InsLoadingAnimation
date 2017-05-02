package com.example.qintong.library;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class InsLoadingView  extends View {

    Indicator mIndicator = new SemiCircleSpinIndicator();
    private boolean mShouldStartAnimationDrawable;

    public InsLoadingView(Context context) {
        super(context);
        postInvalidate();
    }


    public InsLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        postInvalidate();
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
        Log.d("qintong1", "onDraw");
        drawTrack(canvas);
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
            ((Animatable) d).start();
        }
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
}
