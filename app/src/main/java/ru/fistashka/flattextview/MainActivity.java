package ru.fistashka.flattextview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import ru.fistashka.flattextview.databinding.ActivityMainBinding;

/*
* Special for TrueConf, 16.03.2023 01:46
* */

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    private ActivityMainBinding activityMainBinding;

    private final Handler handler = new Handler(Looper.getMainLooper());

    private Timer runAnimationTimer = null;
    private ObjectAnimator animator;

    private int displayHeight = 0;

    private int blueColorValue = 0;
    private int redColorValue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());

        getDisplayHeight();
        getBlueAndRedColors();
    }

    private void getBlueAndRedColors() {
        blueColorValue = ContextCompat.getColor(getApplicationContext(), R.color.blue);
        redColorValue = ContextCompat.getColor(getApplicationContext(), R.color.red);

        Log.d(TAG, "getBlueAndRedColors: get blue and red colors;");
    }

    private void getDisplayHeight() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        displayHeight = size.y;

        Log.d(TAG, "getDisplayHeight: get display height.");
    }

    @Override
    protected void onResume() {
        super.onResume();

        setOnTouchAndClickListeners();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setOnTouchAndClickListeners() {
        activityMainBinding.mainConstraintLayout.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                int x = (int) motionEvent.getX();
                int y = (int) motionEvent.getY();
                activityMainBinding.helloTextView.setX(x);
                activityMainBinding.helloTextView.setY(y);

                changeTextViewColor();
                destroyAnimator();
                destroyRunAnimationTimer();
                createRunAnimationTimer();

                Log.d(TAG, "onTouch: touched.");
            }
            return true;
        });

        activityMainBinding.helloTextView.setOnClickListener(view -> {
            destroyRunAnimationTimer();
            destroyAnimator();
        });
    }

    private void createRunAnimationTimer() {
        long runAnimationTimerValue = 5000L;

        runAnimationTimer = new Timer();
        runAnimationTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> startAnimation(true));
            }
        }, runAnimationTimerValue);

        Log.d(TAG, "createRunAnimationTimer: create run animation timer;");
    }

    private void changeTextViewColor() {
        String currentLocale = Locale.getDefault().toString();

        if(currentLocale.contains("en")) activityMainBinding.helloTextView.setTextColor(redColorValue);
        else if(currentLocale.contains("ru")) activityMainBinding.helloTextView.setTextColor(blueColorValue);

        Log.d(TAG, "changeTextViewColor: text color changed.");
    }

    private void startAnimation(boolean movingToBottom) {
        long animationDuration = 3000L;

        animator = ObjectAnimator.ofFloat(activityMainBinding.helloTextView, "y", movingToBottom ? displayHeight : 0);
        animator.setDuration(animationDuration);
        animator.start();

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                startAnimation(!movingToBottom);
            }
        });

        Log.d(TAG, "startAnimation: moving to " + (movingToBottom ? "bottom" : "top"));
    }

    private void destroyRunAnimationTimer() {
        if(runAnimationTimer != null) {
            runAnimationTimer.cancel();
            Log.d(TAG, "destroyRunAnimationTimer: destroy run animation timer.");
        }
    }

    private void destroyAnimator() {
        if(animator != null) {
            animator.pause();
            Log.d(TAG, "destroyAnimator: timer destroyed.");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        destroyRunAnimationTimer();
        destroyAnimator();
    }
}