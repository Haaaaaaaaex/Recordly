package com.example.recordly;

import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.example.recordly.R;

public class success_animation extends AppCompatActivity {
    ImageView done1, bg;
    AnimatedVectorDrawableCompat avd;
    AnimatedVectorDrawable avd2;
    TextView textView;
    Button btn;

    @Override
    protected void onStart() {
        super.onStart();
        startAnimation();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert_dialog);

        btn = findViewById(R.id.login_okay_dialogBtn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(success_animation.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
    public void startAnimation(){
        done1 = findViewById(R.id.check);
        bg = findViewById(R.id.bg1);

        Drawable drawable = done1.getDrawable();
        if(drawable instanceof AnimatedVectorDrawableCompat){
            avd = (AnimatedVectorDrawableCompat) drawable;
            avd.start();
        }else if(drawable instanceof AnimatedVectorDrawable){
            avd2 = (AnimatedVectorDrawable) drawable;
            avd2.start();
        }
    }
}
