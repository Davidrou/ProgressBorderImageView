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
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;

import static android.graphics.Path.Direction.CCW;
import static android.graphics.Path.Direction.CW;

/**
 * Created by mi on 20-6-12.
 */

public class ProgressLayout extends RelativeLayout {
    private static final String TAG = "ProgressLayout";
    private Paint mPaint;
    private int mCurrentProgress = 0;
    private PathMeasure mPathMeasure;
    private Path mPath;
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
        mPaint = new Paint();
        setWillNotDraw(false);
        mStokeWidth = 10;
        mRadius = 50;
//                canvas.drawRoundRect(100, 100, 500, 300, 50, 50, mPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mPathMeasure = new PathMeasure();
        mPath = new Path();
        //path.addRoundRect(100, 100, 500, 300,50,50, CCW);
        //第一个
        int innerWidth = mWidth - 2 * mStokeWidth;
        int innerHeight = mHeight - 2 * mStokeWidth;

        int startOffset = innerWidth / 2 - mRadius;
        mPath.moveTo(mRadius + mStokeWidth + startOffset, mStokeWidth);
        mPath.lineTo(innerWidth-mRadius+mStokeWidth, mStokeWidth);
        mPath.quadTo(innerWidth+mStokeWidth, mStokeWidth, innerWidth+mStokeWidth, mRadius+mStokeWidth);
        //第二个
        mPath.lineTo(innerWidth+mStokeWidth, innerHeight-mRadius+mStokeWidth);
        mPath.quadTo(innerWidth+mStokeWidth, innerHeight+mStokeWidth, innerWidth+mStokeWidth-mRadius, innerHeight+mStokeWidth);

        //第3个
        mPath.lineTo(mRadius+mStokeWidth, innerHeight+mStokeWidth);
        mPath.quadTo(mStokeWidth, innerHeight+mStokeWidth, mStokeWidth, innerHeight-mRadius+mStokeWidth);

        mPath.lineTo(mStokeWidth, mRadius+mStokeWidth);
        mPath.quadTo(mStokeWidth, mStokeWidth, mRadius+mStokeWidth, mStokeWidth);
        if (startOffset > 0) {
            mPath.lineTo(mRadius+mStokeWidth + startOffset, mStokeWidth);
        }
        mPathMeasure.setPath(mPath, false);
    }

    public void setProgress(int progress) {
        mCurrentProgress = progress;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG, "onDraw");
        int innerWidth = mWidth - 2 * mStokeWidth;
        int innerHeight = mHeight - 2 * mStokeWidth;
        if (mFinalBitmap == null) {
            //todo:getWidth和getMeasuredWidth区别学习
            mFinalBitmap = createFinalBitmap(innerWidth, innerHeight);
            Log.d(TAG, "getWidth " + getWidth());
        }
        mPaint.setColor(getResources().getColor(android.R.color.holo_blue_dark));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStokeWidth);
        mPaint.setAntiAlias(true);
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
        Path mDest = new Path();
        float mLength = mPathMeasure.getLength();
        mPathMeasure.getSegment(0, mLength * mCurrentProgress / 100, mDest, true);
        canvas.drawPath(mDest, mPaint);
        //离屏绘制

        Rect rect = new Rect(0, 0, innerWidth, innerHeight);
        canvas.drawBitmap(mFinalBitmap, rect, rect, mPaint);
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
        canvas.drawPath(mPath, dstPaint);
//        canvas.drawCircle(width/2, height/2, height/2, dstPaint);
        return bitmap;
    }
}
