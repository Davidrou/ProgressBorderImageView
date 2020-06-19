package com.example.mi.myprogresslayout;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private Handler mHander = new Handler();
    int mCurrentProgress = 0;
    int mCurrentProgressForImage2 = 0;
    ProgressImageView mProgressImageView, mProgressImageView2, mProgressImageView3;

    private Runnable mChangeProgressRunnable = new Runnable() {
        @Override
        public void run() {
            if (mCurrentProgress < 100) {
                mCurrentProgress += 8;
            } else {
                mProgressImageView.setNeedMask(false);
            }
            mProgressImageView.setProgress(mCurrentProgress);

            if (mCurrentProgressForImage2 < 100) {
                mCurrentProgressForImage2 += 4;
                Log.d("LZW","mCurrentProgressForImage2:"+mCurrentProgressForImage2);
            }

            if(mCurrentProgressForImage2 >=100) {
                mProgressImageView2.setNeedMask(false);
                Log.d("LZW", "mProgressImageView2 setNeedMask false");
            }
            mProgressImageView2.setProgress(mCurrentProgressForImage2);
            if (mCurrentProgress < 100 || mCurrentProgressForImage2 < 100) {
                mHander.postDelayed(this, 100);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressImageView = findViewById(R.id.my_progress_imageView);
        mProgressImageView2 = findViewById(R.id.my_progress_imageView2);
        mProgressImageView3 = findViewById(R.id.my_progress_imageView3);
        mProgressImageView3.setProgress(100);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mHander.postDelayed(mChangeProgressRunnable, 2000);
    }
}
