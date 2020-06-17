package com.example.mi.myprogresslayout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;

/**
 * Created by mi on 20-6-12.
 */

public class ProgressLayout extends RelativeLayout {
    private static final String TAG = "ProgressLayout";
    private Paint mPaint;
    private int mCurrentProgress = 0;
    private PathMeasure mPathMeasure;
    private Path mPathBackGround, mPathClipBitmap,mPathOuter;
    private Bitmap mFinalBitmap;
    private int mWidth, mHeight, mRadius, mStokeWidth;

    private Runnable mChangeProgressRunnuable = new Runnable() {
        @Override
        public void run() {
            if (mCurrentProgress < 100) {
                mCurrentProgress++;
            } else {
                mCurrentProgress = 0;
            }
            Log.d(TAG, "mCurrentProgress" + mCurrentProgress);
            invalidate();
            mHander.postDelayed(this, 20);
        }
    };

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mHander.post(mChangeProgressRunnuable);
    }

    private Handler mHander = new Handler();

    public ProgressLayout(Context context) {
        this(context, null);
    }

    public ProgressLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mStokeWidth = 20*2;//StokeWidth需要是用户设置的两倍 因为有一半会被图片遮挡
        mRadius = 30;
        setWillNotDraw(false);
        mPaint = new Paint();
        mPaint.setColor(getResources().getColor(android.R.color.holo_blue_dark));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStokeWidth);
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mPathMeasure = new PathMeasure();
        mPathBackGround = new Path();
        //path.addRoundRect(100, 100, 500, 300,50,50, CCW);
        //第一个
        int innerWidth = mWidth - 2 * mStokeWidth;
        int innerHeight = mHeight - 2 * mStokeWidth;
//
        int startOffset = innerWidth / 2 - mRadius;
//        mPathBackGround.moveTo(mRadius + mStokeWidth + startOffset, mStokeWidth / 2);
//        mPathBackGround.lineTo(innerWidth - mRadius + mStokeWidth / 2 + mStokeWidth, mStokeWidth / 2);
//        mPathBackGround.quadTo(innerWidth + mStokeWidth + mStokeWidth / 2, mStokeWidth / 2,
//                innerWidth + mStokeWidth + mStokeWidth / 2, mRadius + mStokeWidth / 2);
//
//        //第二个
//        mPathBackGround.lineTo(innerWidth + mStokeWidth / 2 + mStokeWidth, innerHeight - mRadius + mStokeWidth + mStokeWidth / 2);
//        mPathBackGround.quadTo(innerWidth + mStokeWidth / 2 + mStokeWidth, innerHeight + mStokeWidth / 2 + mStokeWidth,
//                innerWidth + mStokeWidth - mRadius, innerHeight + mStokeWidth / 2 + mStokeWidth);
//
//        //第3个
//        mPathBackGround.lineTo(mRadius + mStokeWidth / 2, innerHeight + mStokeWidth / 2 + mStokeWidth);
//        mPathBackGround.quadTo(mStokeWidth / 2, innerHeight + mStokeWidth / 2 + mStokeWidth,
//                mStokeWidth / 2, innerHeight - mRadius + mStokeWidth);
//
//        mPathBackGround.lineTo(mStokeWidth / 2, mRadius + mStokeWidth);
//        mPathBackGround.quadTo(mStokeWidth / 2, mStokeWidth / 2, mRadius + mStokeWidth, mStokeWidth / 2);
//        if (startOffset > 0) {
//            mPathBackGround.lineTo(mRadius + mStokeWidth + startOffset, mStokeWidth / 2);
//        }
//        mPathMeasure.setPath(mPathBackGround, false);

//        mPathClipBitmap = new Path();
//
////        int temp =mRadius;
////        mRadius = mRadius * innerHeight / mHeight;
//        //第一个
//        mPathClipBitmap.moveTo(mRadius + mStokeWidth + startOffset, mStokeWidth);
//        mPathClipBitmap.lineTo(innerWidth - mRadius + mStokeWidth, mStokeWidth);
//        mPathClipBitmap.quadTo(innerWidth + mStokeWidth , mStokeWidth, innerWidth + mStokeWidth, mRadius + mStokeWidth);
//        //第二个
//        mPathClipBitmap.lineTo(innerWidth + mStokeWidth, innerHeight - mRadius + mStokeWidth);
//        mPathClipBitmap.quadTo(innerWidth + mStokeWidth, innerHeight + mStokeWidth, innerWidth + mStokeWidth - mRadius, innerHeight + mStokeWidth);
//
//        //第3个
//        mPathClipBitmap.lineTo(mRadius + mStokeWidth, innerHeight + mStokeWidth);
//        mPathClipBitmap.quadTo(mStokeWidth, innerHeight + mStokeWidth, mStokeWidth, innerHeight - mRadius + mStokeWidth);
//
//        mPathClipBitmap.lineTo(mStokeWidth, mRadius + mStokeWidth);
//        mPathClipBitmap.quadTo(mStokeWidth, mStokeWidth, mRadius + mStokeWidth, mStokeWidth);
//        if (startOffset > 0) {
//            mPathClipBitmap.lineTo(mRadius + mStokeWidth + startOffset, mStokeWidth);
//        }

        mPathOuter = new Path();
        //第一个
        mPathOuter.moveTo(mRadius  + startOffset, 0);
        mPathOuter.lineTo(mWidth - mRadius, 0);
        mPathOuter.quadTo(mWidth , 0, mWidth, mRadius);
        //第二个
        mPathOuter.lineTo(mWidth, mHeight - mRadius);
        mPathOuter.quadTo(mWidth, mHeight, mWidth - mRadius, mHeight);

        //第3个
        mPathOuter.lineTo(mRadius, mHeight);
        mPathOuter.quadTo(0, mHeight, 0, mHeight - mRadius);

        mPathOuter.lineTo(0, mRadius);
        mPathOuter.quadTo(0, 0, mRadius, 0);
        if (startOffset > 0) {
            mPathOuter.lineTo(mRadius  + startOffset, 0);
        }
        mPathMeasure.setPath(mPathOuter, false);
    }

    public void setProgress(int progress) {
        mCurrentProgress = progress;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG, "onDraw");
        int innerWidth = mWidth - 2 * mStokeWidth;
        int innerHeight = mHeight - 2 * mStokeWidth;

        //先构建裁剪后的圆角图片
        if (mFinalBitmap == null) {
            //todo:getWidth和getMeasuredWidth区别学习
            mFinalBitmap = createFinalBitmap(mWidth, mHeight);
            Log.d(TAG, "getWidth " + getWidth());
        }

        //A:直接画
//        Path path =new Path();
//        path.moveTo(100,100);
//        path.lineTo(450,100);
//        path.quadTo(500,100,500,150);
//        path.lineTo(500,300);
//        path.lineTo(100,100);
//        canvas.drawPath(path, mPaint);


        //B:Use circle Mask
//        canvas.drawRoundRect(100, 100, 500, 300, 50, 50, mPaint);
//        mPaint.setColor(getResources().getColor(android.R.color.holo_red_dark));
//        mPaint.setStyle(Paint.Style.FILL);
//        int radius = (int) Math.sqrt(200/2 * 200/2 + 400/2 * 400/2);
//        int centerX = (500-100)/2+100;
//        int centerY = (300-100)/2+100;
//        RectF rectf_head = new RectF(centerX-radius, centerY-radius, centerX+radius, centerY+radius);//确定外切矩形范围
//        canvas.drawArc(rectf_head, -90 ,360-(360*mCurrentProgress/100), true,mPaint);

        //C: Use PathMeasure
        //1.计算外边框当前的Path 画Path 因为画笔有宽度 所以需要缩放
        Path mDest = new Path();
        float mLength = mPathMeasure.getLength();
        mPathMeasure.getSegment(0, mLength * mCurrentProgress / 100, mDest, true);
        canvas.save();
        //实际上最终的宽度为View的宽度减去两边Paint的边界的一半 因为边界是以Path为中心向内外两个方向画的
        float scaleX = (mWidth-mStokeWidth*1.0f)/mWidth;
        float scaleY = (mHeight-mStokeWidth*1.0f)/mHeight;
        canvas.scale(scaleX, scaleY, mWidth/2, mHeight/2);
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
        canvas.drawBitmap(createDstBitmap(width, height), 0, 0, mPaint);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawBitmap(createSrcBitmap(width, height), 0, 0, mPaint);
        mPaint.setXfermode(null);
        canvas.restoreToCount(layerID);
        return bitmap;
    }


    public Bitmap createDstBitmap(int width, int height) {
        return BitmapFactory.decodeResource(getResources(), R.drawable.babybus_home);
    }

    public Bitmap createSrcBitmap(int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint dstPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dstPaint.setStyle(Paint.Style.FILL);
        dstPaint.setColor(Color.parseColor("#ec6941"));
        canvas.drawPath(mPathOuter, dstPaint);
        return bitmap;
    }
}
