package com.example.mi.myprogresslayout;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private Handler mHander = new Handler();
    int mCurrentProgress = 0;
    ProgressImageView mProgressImageView;

    private Runnable mChangeProgressRunnable = new Runnable() {
        @Override
        public void run() {
            if (mCurrentProgress < 100) {
                mCurrentProgress+=8;
                mHander.postDelayed(this,100);
            } else {
                mProgressImageView.setNeedMask(false);
            }
            mProgressImageView.setProgress(mCurrentProgress);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressImageView = findViewById(R.id.my_progress_imageView);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mHander.postDelayed(mChangeProgressRunnable,2000);
    }
}
