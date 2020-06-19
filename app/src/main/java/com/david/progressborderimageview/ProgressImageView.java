package com.david.progressborderimageview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;


public class ProgressImageView extends android.support.v7.widget.AppCompatImageView {

    private static final String TAG = "ProgressImageView";
    private Paint mPaint;
    private int mCurrentProgress = 0;
    private int mNextSetProgress = 0;
    private PathMeasure mPathMeasure;
    private Path mPathOuter;
    private Bitmap mFinalBitmap;
    private int mWidth, mHeight,mStartOffset;
    private boolean mNeedMask = true;
    private ValueAnimator mChangeProgressAnimator;
    private int mBorderColor = Color.BLUE;
    private int mCornerRadius = 30;
    private int mBorderWidth = 20;
    private int mMaskColor = 0x66000000;

    public ProgressImageView(Context context) {

        this(context, null);
    }

    public ProgressImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.ProgressImageView, 0, 0);

        for (int i = 0; i < ta.getIndexCount(); i++) {
            int attr = ta.getIndex(i);
            if (attr == R.styleable.ProgressImageView_border_width) {
                mBorderWidth = ta.getDimensionPixelSize(attr, mBorderWidth);
                mBorderWidth*= 2;//StokeWidth需要是用户设置的两倍 因为有一半会被图片遮挡 TOdo:优化点
            } else if (attr == R.styleable.ProgressImageView_border_color) {
                mBorderColor = ta.getColor(attr, mBorderColor);
            } else if (attr == R.styleable.ProgressImageView_corner_radius) {
                mCornerRadius = ta.getDimensionPixelSize(attr, mCornerRadius);
            } else if (attr == R.styleable.ProgressImageView_mask_color) {
                mMaskColor = ta.getColor(attr, mMaskColor);
            }else if (attr == R.styleable.ProgressImageView_need_mask) {
                mNeedMask = ta.getBoolean(attr, mNeedMask);
            }
        }
        ta.recycle();

        mCornerRadius = 30;
        setWillNotDraw(false);
        mPaint = new Paint();
        mPaint.setColor(mBorderColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mBorderWidth);
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mPathMeasure = new PathMeasure();
        mStartOffset = mWidth / 2 - mCornerRadius; //todo:这里起始点为上边的中间 后续需要动态配置
        mPathOuter = new Path();
        //上面的路径
        mPathOuter.moveTo(mCornerRadius + mStartOffset, 0);
        mPathOuter.lineTo(mWidth - mCornerRadius, 0);
        mPathOuter.quadTo(mWidth, 0, mWidth, mCornerRadius);
        //右边的路径
        mPathOuter.lineTo(mWidth, mHeight - mCornerRadius);
        mPathOuter.quadTo(mWidth, mHeight, mWidth - mCornerRadius, mHeight);

        //下面的路径
        mPathOuter.lineTo(mCornerRadius, mHeight);
        mPathOuter.quadTo(0, mHeight, 0, mHeight - mCornerRadius);

        //左边的路径
        mPathOuter.lineTo(0, mCornerRadius);
        mPathOuter.quadTo(0, 0, mCornerRadius, 0);
        if (mStartOffset > 0) {
            mPathOuter.lineTo(mCornerRadius + mStartOffset, 0);
        }
        mPathMeasure.setPath(mPathOuter, false);
    }

    /**
     * 设置进度后不会马上绘制当前的进度 需要使用属性动画逐渐绘制 这样效果会比较流畅
     * @param progress
     */
    public void setProgress(int progress) {
        mNextSetProgress = progress;
        startChangeProgressAnim();
    }


    private void startChangeProgressAnim() {
        if (mChangeProgressAnimator != null && mChangeProgressAnimator.isRunning()) {
            mChangeProgressAnimator.cancel();
        }
        mChangeProgressAnimator = ValueAnimator.ofInt(mCurrentProgress, mNextSetProgress);
        mChangeProgressAnimator.setDuration(200);
        mChangeProgressAnimator.start();
        mChangeProgressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int curValue = (int) animation.getAnimatedValue();
                mCurrentProgress = curValue;
                invalidate();
            }
        });
    }

    public void setNeedMask(boolean needMask) {
        mNeedMask = needMask;
        if (mFinalBitmap != null) {
            mFinalBitmap = createFinalBitmap(mWidth, mHeight);
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG, "onDraw");

//        先构建裁剪后的圆角图片 防止内存抖动使用变量进行设置 后续需要考虑用户动态更改图片的需求
        if (mFinalBitmap == null) {
            //todo:getWidth和getMeasuredWidth区别学习
            mFinalBitmap = createFinalBitmap(mWidth, mHeight);
            Log.d(TAG, "getWidth " + getWidth());
        }

//
        //1.计算外边框当前的Path 画Path 因为画笔有宽度 所以需要缩放
        Path mDest = new Path();
        float mLength = mPathMeasure.getLength();
        mPathMeasure.getSegment(0, mLength * mCurrentProgress / 100, mDest, true);
        canvas.save();
        //实际上最终的宽度为View的宽度减去两边Paint的边界的一半 因为边界是以Path为中心向内外两个方向画的
        float scaleX = (mWidth - mBorderWidth * 1.0f) / mWidth;
        float scaleY = (mHeight - mBorderWidth * 1.0f) / mHeight;
        canvas.scale(scaleX, scaleY, mWidth / 2, mHeight / 2);
        canvas.drawPath(mDest, mPaint);

        //2.画imageView 使用相同的Scale和Path 那么边框的内边界会被遮挡 原因是如果按照实际比例去缩放会导致图片圆角和边框圆角之间有空白
        Rect rect = (new Rect(0, 0, mWidth, mHeight));
        canvas.drawBitmap(mFinalBitmap, rect, rect, mPaint);
        canvas.restore();
    }

    public Bitmap createFinalBitmap(int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        //todo:layer做啥的 不加外面就是黑色的
        int layerID = canvas.saveLayer(0, 0, width, height, mPaint, Canvas.ALL_SAVE_FLAG);
        //1.画ImageView
        super.onDraw(canvas);
        //2.画遮罩
        if (mNeedMask) {
            Paint paint = new Paint();
            paint.setColor(mMaskColor);
//            paint.setAlpha(100);
            canvas.drawRect(new Rect(0, 0, width, height), paint);
        }
        //3.设置混合模式为IN 并且画边框
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawBitmap(createSrcBitmap(width, height), 0, 0, mPaint);
        mPaint.setXfermode(null);
        canvas.restoreToCount(layerID);
        return bitmap;
    }


    public Bitmap createSrcBitmap(int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint dstPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dstPaint.setStyle(Paint.Style.FILL);
        canvas.drawPath(mPathOuter, dstPaint);
        return bitmap;
    }

}
