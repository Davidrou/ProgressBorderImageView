package com.example.mi.myprogresslayout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import static android.graphics.Path.Direction.CCW;

public class ProgressImageView extends android.support.v7.widget.AppCompatImageView {

    private int width,height;

    public ProgressImageView(Context context) {

        this(context, null);
    }

    public ProgressImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Path path = new Path();
        Paint paint  =new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        paint.setColor(getResources().getColor(android.R.color.darker_gray));
        path.addRoundRect(new RectF(0, 0, 500, 400), 50, 50,CCW);
        //canvas.clipPath(path);
        //super.onDraw(canvas);
        canvas.save();
        canvas.scale(0.5f, 0.5f, height/2, width/2);
        canvas.drawPath(path, paint);
        canvas.restore();
        canvas.drawPath(path,paint);

    }

}
