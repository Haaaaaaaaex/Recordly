package com.example.recordly;

import androidx.appcompat.app.AppCompatActivity;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class changepassDialog extends AppCompatActivity {
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepass_dialog);

        btn = findViewById(R.id.password_okay_dialogBtn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SessionManager sessionManager = new SessionManager(changepassDialog.this);
                sessionManager.LogoutUserFromSession();
                Intent intent = new Intent(changepassDialog.this, Login.class);
                startActivity(intent);
                finish();
            }
        });
    }
    public void startAnimation(){
        done1 = findViewById(R.id.pass_check);
        bg = findViewById(R.id.chnge_pass_bg);

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