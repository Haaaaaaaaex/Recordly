package com.example.recordly;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class splash_screen extends Activity {

    TextView title;

    private static int splash_timeout = 4720;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        title = findViewById(R.id.introtitle1);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent splashintent = new Intent(splash_screen.this, Login.class);
                    startActivity(splashintent);
                    finish();
                }
            },splash_timeout);

        Animation intro = AnimationUtils.loadAnimation(splash_screen.this,R.anim.introanim1);
        title.startAnimation(intro);

    }
}

