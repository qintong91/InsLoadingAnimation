package com.qintong.library

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.Shader.TileMode.CLAMP
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.util.SparseArray
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageView

class InsLoadingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr) {

    var status = Status.LOADING
    private var mRotateDuration = 10000
    private var mCircleDuration = 2000
    private val bitmapDia = CIRCLE_DIA - STROKE_WIDTH
    private var mRotateDegree: Float = 0.toFloat()
    private var mCircleWidth: Float = 0.toFloat()
    private var mIsFirstCircle = true
    private var mRotateAnim: ValueAnimator? = null
    private var mCircleAnim: ValueAnimator? = null
    private var mTouchAnim: ValueAnimator? = null
    private var mStartColor = Color.TRANSPARENT
    private var mEndColor = Color.TRANSPARENT
    private var mClickedColor = CLICKED_COLOR
    private var mScale = 1f
    private var mBitmapPaint: Paint? = null
    private var mTrackPaint: Paint? = null
    private var mBitmapRectF: RectF? = null
    private var mTrackRectF: RectF? = null
    private var cap: Paint.Cap = Paint.Cap.ROUND
    private var join: Paint.Join = Paint.Join.ROUND

    private val trackPaint: Paint
        get() {
            val paint = Paint()
            paint.isAntiAlias = true
            val shader = LinearGradient(
                0f, 0f, width.toFloat() * CIRCLE_DIA * (360 - ARC_WIDTH * 4) / 360,
                height * STROKE_WIDTH, mStartColor, mEndColor, CLAMP
            )
            paint.shader = shader
            setPaintStroke(paint)
            return paint
        }

    private val bitmapPaint: Paint
        get() {
            val paint = Paint()
            paint.isAntiAlias = true
            val drawable = drawable
            val matrix = Matrix()
            if (null == drawable) {
                return paint
            }
            val bitmap = drawableToBitmap(drawable)
            val shader = BitmapShader(bitmap, CLAMP, CLAMP)
            val size = Math.min(bitmap.width, bitmap.height)
            val scale = width * 1.0f / size
            matrix.setScale(scale, scale)
            if (bitmap.width > bitmap.height) {
                matrix.postTranslate(-(bitmap.width * scale - width) / 2, 0f)
            } else {
                matrix.postTranslate(0f, -(bitmap.height * scale - height) / 2)
            }
            shader.setLocalMatrix(matrix)
            paint.shader = shader
            return paint
        }

    enum class Status {
        LOADING, CLICKED, UNCLICKED
    }

    init {
        if (attrs != null) {
            parseAttrs(context, attrs)
        }
        onCreateAnimators()
    }

    fun setCircleDuration(circleDuration: Int): InsLoadingView {
        this.mCircleDuration = circleDuration
        mCircleAnim!!.duration = mCircleDuration.toLong()
        return this
    }

    fun setRotateDuration(rotateDuration: Int): InsLoadingView {
        this.mRotateDuration = rotateDuration
        mRotateAnim!!.duration = mRotateDuration.toLong()
        return this
    }

    fun setStartColor(startColor: Int) {
        mStartColor = startColor
        mTrackPaint = null
    }

    fun setEndColor(endColor: Int) {
        mEndColor = endColor
        mTrackPaint = null
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)
        if (DEBUG) {
            Log.d(TAG, String.format("onMeasure widthMeasureSpec: %s -- %s", widthSpecMode, widthSpecSize))
            Log.d(TAG, String.format("onMeasure heightMeasureSpec: %s -- %s", heightSpecMode, heightSpecSize))
        }
        var width: Int
        if (widthSpecMode == MeasureSpec.EXACTLY && heightSpecMode == MeasureSpec.EXACTLY) {
            width = Math.min(widthSpecSize, heightSpecSize)
        } else {
            width = Math.min(widthSpecSize, heightSpecSize)
            width = Math.min(width, MIN_WIDTH)
        }
        setMeasuredDimension(width, width)
    }

    override fun onDraw(canvas: Canvas) {
        initPaints()
        initRectFs()
        canvas.scale(mScale, mScale, centerX(), centerY())
        drawBitmap(canvas)
        when (status) {
            Status.LOADING -> drawTrack(canvas, mTrackPaint)
            Status.UNCLICKED -> drawCircle(canvas, mTrackPaint)
            Status.CLICKED -> drawClickedCircle(canvas)
        }
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        if (DEBUG) {
            Log.d(TAG, "onVisibilityChanged")
        }
        if (visibility == View.VISIBLE) {
            startAnim()
        } else {
            endAnim()
        }
        super.onVisibilityChanged(changedView, visibility)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        var result = false
        if (DEBUG) {
            Log.d(TAG, "onTouchEvent: " + event.action)
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startDownAnim()
                result = true
            }
            MotionEvent.ACTION_UP -> {
                startUpAnim()
            }
            MotionEvent.ACTION_CANCEL -> {
                startUpAnim()
            }
        }
        return super.onTouchEvent(event) || result
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        if (DEBUG) {
            Log.d(TAG, "onSizeChanged")
        }
        mBitmapRectF = null
        mTrackRectF = null
        mBitmapPaint = null
        mTrackPaint = null
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun setImageDrawable(drawable: Drawable?) {
        if (DEBUG) {
            Log.d(TAG, "setImageDrawable")
        }
        mBitmapPaint = null
        super.setImageDrawable(drawable)
    }

    @SuppressLint("CustomViewStyleable")
    private fun parseAttrs(context: Context, attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.InsLoadingViewAttr)
        val startColor = typedArray.getColor(R.styleable.InsLoadingViewAttr_start_color, mStartColor)
        val endColor = typedArray.getColor(R.styleable.InsLoadingViewAttr_end_color, mEndColor)
        val clickedColor = typedArray.getColor(R.styleable.InsLoadingViewAttr_clicked_color, mClickedColor)
        val circleDuration = typedArray.getInt(R.styleable.InsLoadingViewAttr_circle_duration, mCircleDuration)
        val rotateDuration = typedArray.getInt(R.styleable.InsLoadingViewAttr_rotate_duration, mRotateDuration)
        val status1 = typedArray.getInt(R.styleable.InsLoadingViewAttr_status, 0)
        val corners = typedArray.getInt(R.styleable.InsLoadingViewAttr_corners, 0)
        if (corners == 0) {
            cap = Paint.Cap.ROUND
            join = Paint.Join.ROUND
        } else {
            cap = Paint.Cap.SQUARE
            join = Paint.Join.MITER
        }
        if (DEBUG) {
            Log.d(TAG, "parseAttrs start_color: $startColor")
            Log.d(TAG, "parseAttrs end_color: $endColor")
            Log.d(TAG, "parseAttrs rotate_duration: $rotateDuration")
            Log.d(TAG, "parseAttrs circle_duration: $circleDuration")
            Log.d(TAG, "parseAttrs status: $status")
        }
        typedArray.recycle()
        if (circleDuration != mCircleDuration) {
            setCircleDuration(circleDuration)
        }
        if (rotateDuration != mRotateDuration) {
            setRotateDuration(rotateDuration)
        }
        setStartColor(startColor)
        setEndColor(endColor)
        status = sStatusArray!!.get(status1)
        mClickedColor = clickedColor
    }

    private fun initPaints() {
        if (mBitmapPaint == null) {
            mBitmapPaint = bitmapPaint
        }
        if (mTrackPaint == null) {
            mTrackPaint = trackPaint
        }
    }

    private fun initRectFs() {
        if (mBitmapRectF == null) {
            mBitmapRectF = RectF(
                width * (1 - bitmapDia), width * (1 - bitmapDia),
                width * bitmapDia, height * bitmapDia
            )
        }
        if (mTrackRectF == null) {
            mTrackRectF = RectF(
                width * (1 - CIRCLE_DIA), width * (1 - CIRCLE_DIA),
                width * CIRCLE_DIA, height * CIRCLE_DIA
            )
        }
    }

    private fun centerX(): Float {
        return (width / 2).toFloat()
    }

    private fun centerY(): Float {
        return (height / 2).toFloat()
    }

    private fun onCreateAnimators() {
        mRotateAnim = ValueAnimator.ofFloat(0f, 180f, 360f)
        mRotateAnim!!.addUpdateListener { animation ->
            mRotateDegree = animation.animatedValue as Float
            postInvalidate()
        }
        mRotateAnim!!.interpolator = LinearInterpolator()
        mRotateAnim!!.duration = mRotateDuration.toLong()
        mRotateAnim!!.repeatCount = -1
        mCircleAnim = ValueAnimator.ofFloat(0f, 360f)
        mCircleAnim!!.interpolator = DecelerateInterpolator()
        mCircleAnim!!.duration = mCircleDuration.toLong()
        mCircleAnim!!.repeatCount = -1
        mCircleAnim!!.addUpdateListener { animation ->
            mCircleWidth = if (mIsFirstCircle) {
                animation.animatedValue as Float
            } else {
                animation.animatedValue as Float - 360
            }
            postInvalidate()
        }
        mCircleAnim!!.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {

            }

            override fun onAnimationEnd(animation: Animator) {

            }

            override fun onAnimationCancel(animation: Animator) {

            }

            override fun onAnimationRepeat(animation: Animator) {
                mIsFirstCircle = !mIsFirstCircle
            }
        })
        mTouchAnim = ValueAnimator()
        mTouchAnim!!.interpolator = DecelerateInterpolator()
        mTouchAnim!!.duration = 200
        mTouchAnim!!.addUpdateListener { animation ->
            mScale = animation.animatedValue as Float
            postInvalidate()
        }
        startAnim()
    }

    private fun drawBitmap(canvas: Canvas) {
        canvas.drawOval(mBitmapRectF!!, mBitmapPaint!!)
    }

    private fun drawTrack(canvas: Canvas, paint: Paint?) {
        canvas.rotate(mRotateDegree, centerX(), centerY())
        canvas.rotate(ARC_WIDTH, centerX(), centerY())

        if (DEBUG) {
            Log.d(TAG, "circleWidth:$mCircleWidth")
        }
        if (mCircleWidth < 0) {
            //a
            val startArg = mCircleWidth + 360
            canvas.drawArc(mTrackRectF!!, startArg, 360 - startArg, false, paint!!)
            var adjustCircleWidth = mCircleWidth + 360
            var width = 8f
            while (adjustCircleWidth > ARC_WIDTH) {
                width -= ARC_CHANGE_ANGLE
                adjustCircleWidth -= ARC_WIDTH
                canvas.drawArc(mTrackRectF!!, adjustCircleWidth, width, false, paint)
            }
        } else {
            //b
            for (i in 0..4) {
                if (ARC_WIDTH * i > mCircleWidth) {
                    break
                }
                canvas.drawArc(mTrackRectF!!, mCircleWidth - ARC_WIDTH * i, (8 + i).toFloat(), false, paint!!)
            }
            if (mCircleWidth > ARC_WIDTH * 4) {
                canvas.drawArc(mTrackRectF!!, 0f, mCircleWidth - ARC_WIDTH * 4, false, paint!!)
            }
            var adjustCircleWidth = 360f
            var width = 8 * (360 - mCircleWidth) / 360
            if (DEBUG) {
                Log.d(TAG, "width:$width")
            }
            while (width > 0 && adjustCircleWidth > ARC_WIDTH) {
                width -= ARC_CHANGE_ANGLE
                adjustCircleWidth -= ARC_WIDTH
                canvas.drawArc(mTrackRectF!!, adjustCircleWidth, width, false, paint!!)
            }
        }
    }

    private fun drawCircle(canvas: Canvas, paint: Paint?) {
        val rectF = RectF(
            width * (1 - CIRCLE_DIA), width * (1 - CIRCLE_DIA),
            width * CIRCLE_DIA, height * CIRCLE_DIA
        )
        canvas.drawOval(rectF, paint!!)
    }

    private fun drawClickedCircle(canvas: Canvas) {
        val paintClicked = Paint()
        paintClicked.isAntiAlias = true
        paintClicked.color = mClickedColor
        setPaintStroke(paintClicked)
        drawCircle(canvas, paintClicked)
    }

    private fun startDownAnim() {
        mTouchAnim?.setFloatValues(mScale, 0.9f)
        mTouchAnim?.start()
    }

    private fun startUpAnim() {
        mTouchAnim?.setFloatValues(mScale, 1f)
        mTouchAnim?.start()
    }

    private fun startAnim() {
        mRotateAnim?.start()
        mCircleAnim?.start()
    }

    private fun endAnim() {
        mRotateAnim?.end()
        mCircleAnim?.end()
    }

    private fun setPaintStroke(paint: Paint) {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = height * STROKE_WIDTH
        paint.strokeCap = cap
        paint.strokeJoin = join
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        val w = drawable.intrinsicWidth
        val h = drawable.intrinsicHeight
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, w, h)
        drawable.draw(canvas)
        return bitmap
    }

    companion object {
        private val DEBUG = BuildConfig.DEBUG
        private const val TAG = "InsLoadingView"
        private const val ARC_WIDTH = 12f
        private const val MIN_WIDTH = 300
        private const val CIRCLE_DIA = 0.9f
        private const val STROKE_WIDTH = 0.025f
        private const val ARC_CHANGE_ANGLE = 0.2f
        private const val CLICKED_COLOR = Color.LTGRAY

        private var sStatusArray: SparseArray<Status>? = null

        init {
            sStatusArray = SparseArray(3)
            sStatusArray?.put(0, Status.LOADING)
            sStatusArray?.put(1, Status.CLICKED)
            sStatusArray?.put(2, Status.UNCLICKED)
        }
    }
}
