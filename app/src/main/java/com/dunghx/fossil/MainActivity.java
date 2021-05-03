package com.dunghx.fossil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String NUMBER_FORMAT = "00";
    private static final int MAX_PERCENT = 100;
    private static final int MIN_PERCENT = 0;

    private ProgressBar mProgressBar;
    private TextView mTxtStart, mTxtSetTimer, mTxtTimer;
    private Animation mBlinkAnimation;
    private boolean isRunning;
    private CountDownTimer mCountDownTimer;
    private long mTotalTime, mTimeRemaining;
    private NumberFormat mNumberFormat;
    private LinearLayout mLayoutStartContainer;
    private LinearLayout mLayoutCancelContainer;

    private Vibrator mVibe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initAnimation();
    }

    private void initViews() {
        mProgressBar = findViewById(R.id.timer_progress_bar);
        mTxtStart = findViewById(R.id.timer_button_start);
        mTxtSetTimer = findViewById(R.id.timer_tv_set_time);
        mTxtTimer = findViewById(R.id.timer_tv_time);
        mLayoutStartContainer = findViewById(R.id.timer_start_container);
        mLayoutCancelContainer = findViewById(R.id.timer_cancel_container);
        mVibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        mLayoutStartContainer.setOnClickListener(this);
        mLayoutCancelContainer.setOnClickListener(this);
        mTxtSetTimer.setOnClickListener(this);
    }

    private void initAnimation() {
        mBlinkAnimation = AnimationUtils.loadAnimation(this, R.anim.blink);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.timer_start_container:
                if (!isRunning) {
                    handleStartTimerAction();
                } else {
                    handlePauseTimerAction();
                }
                break;
            case R.id.timer_cancel_container:
                handleCancelTimerAction();
                break;
            case R.id.timer_tv_set_time:
                mTxtTimer.clearAnimation();
                mTxtTimer.setVisibility(View.INVISIBLE);
                openDialogTimer();
                break;
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void handleCancelTimerAction() {
        cancelCountDownTimer();
        mTimeRemaining = 0;
        mTotalTime = 0;
        mProgressBar.setProgress(MIN_PERCENT);
        mTxtTimer.setText(getResources().getString(R.string.timer_default));
        mTxtTimer.clearAnimation();
        mLayoutStartContainer.setBackground(getResources().getDrawable(R.drawable.bg_button_unfocus));
        mTxtStart.setText(getResources().getString(R.string.timer_start));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void handlePauseTimerAction() {
        cancelCountDownTimer();
        mTxtStart.setText(getResources().getString(R.string.timer_start));
        mLayoutStartContainer.setBackground(getResources().getDrawable(R.drawable.bg_button_unfocus));
        mTxtTimer.startAnimation(mBlinkAnimation);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void handleStartTimerAction() {
        if (mTimeRemaining <= 0) return;
        setCountDown(mTimeRemaining);
        mLayoutStartContainer.setBackground(getResources().getDrawable(R.drawable.bg_button_focus));
        mTxtStart.setText(getResources().getString(R.string.timer_pause));
        isRunning = true;
        mTxtTimer.clearAnimation();
    }

    private void openDialogTimer() {
        DialogManager.createDialogTimer(this, (min, sec) -> {
            cancelCountDownTimer();
            mTotalTime = TimerUtil.calculateTimeMilliseconds(min, sec);
            mTimeRemaining = mTotalTime;
            mNumberFormat = new DecimalFormat(NUMBER_FORMAT);
            mTxtTimer.setVisibility(View.VISIBLE);
            mTxtTimer.setText(String.format("%s:%s", mNumberFormat.format(min), mNumberFormat.format(sec)));
        });
    }

    private void setCountDown(long millisInFuture) {
        mCountDownTimer = new CountDownTimer(millisInFuture, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long min = TimerUtil.calculateMinutesByMilli(millisUntilFinished);
                long sec = TimerUtil.calculateSecondsByMilli(millisUntilFinished);
                mTxtTimer.setText(String.format("%s:%s", mNumberFormat.format(min), mNumberFormat.format(sec)));
                mTimeRemaining = TimerUtil.calculateTimeMilliseconds(min, sec);

                int progressPercent = TimerUtil.calculatePercent(mTimeRemaining, mTotalTime);
                mProgressBar.setProgress(MAX_PERCENT - progressPercent);
            }

            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onFinish() {
                String title = getResources().getString(R.string.timer_finish);
                String message = getResources().getString(R.string.timer_finish_message);
                mProgressBar.setProgress(MAX_PERCENT);
                isRunning = false;
                mTxtTimer.setText(title);
                mTxtTimer.startAnimation(mBlinkAnimation);
                mTxtStart.setText(getResources().getString(R.string.timer_start));
                mLayoutStartContainer.setBackground(getResources().getDrawable(R.drawable.bg_button_unfocus));
                mTimeRemaining = 0;
                NotifyManager.displayNotification(getApplicationContext());
                mVibe.vibrate(1000);
                DialogManager.createSimpleDialog(MainActivity.this, title, message);
            }
        };
        mCountDownTimer.start();
    }

    private void cancelCountDownTimer() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        isRunning = false;
    }
}