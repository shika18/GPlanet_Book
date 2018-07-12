package com.example.shika.gbook;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import me.wangyuwei.particleview.ParticleView;

public class SplashActivity extends AppCompatActivity {
    private static int Splash_timeout = 20000;
    //hi

    ParticleView mParticleView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();

      /*  new Handler().postDelayed(new Runnable(){
            public void run(){
                Intent homeIntent = new Intent(BeginActivity.this,SignIn.class);
               startActivity(homeIntent);
                finish();
            }
        },Splash_timeout); */
        mParticleView = (ParticleView)findViewById(R.id.parsplash);
        mParticleView.startAnim();

        mParticleView.setOnParticleAnimListener(new ParticleView.ParticleAnimListener() {
            @Override
            public void onAnimationEnd() {
                Intent homeIntent = new Intent(SplashActivity.this,MainActivity.class);
                startActivity(homeIntent);
                finish();}
        });
    }
}
