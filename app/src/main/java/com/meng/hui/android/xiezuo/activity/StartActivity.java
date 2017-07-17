package com.meng.hui.android.xiezuo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.meng.hui.android.xiezuo.R;
import com.meng.hui.android.xiezuo.core.MyActivity;

/**
 * Created by mzk10 on 2017/7/12.
 */

public class StartActivity extends MyActivity {

    private ImageView iv_anim_welcome_scale;
    private ImageView iv_anim_welcome;

    private Animation anim_welcome_scale;
    private Animation anim_welcome;

    private Handler handler = new Handler();

    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_start);
        iv_anim_welcome_scale = findViewById(R.id.iv_anim_welcome_icon);
        iv_anim_welcome = findViewById(R.id.iv_anim_welcome);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        anim_welcome_scale = AnimationUtils.loadAnimation(this, R.anim.anim_welcome_scale);
        anim_welcome_scale.setDuration(1000);
        anim_welcome_scale.setInterpolator(this, android.R.interpolator.bounce);
        anim_welcome_scale.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        iv_anim_welcome.setVisibility(View.VISIBLE);
                        iv_anim_welcome.startAnimation(anim_welcome);
                    }
                }, 200);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        anim_welcome = AnimationUtils.loadAnimation(StartActivity.this, R.anim.anim_welcome_rotate);
        anim_welcome.setInterpolator(StartActivity.this, android.R.interpolator.linear);
        anim_welcome.setDuration(3000);
        anim_welcome.setRepeatCount(Animation.INFINITE);
        anim_welcome.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(StartActivity.this, BookListActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, 5000);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    public void startAction(Bundle savedInstanceState) {
        iv_anim_welcome_scale.startAnimation(anim_welcome_scale);
    }

    @Override
    public void onBackClicked() {

    }

    @Override
    public void onClick(View view) {

    }
}
