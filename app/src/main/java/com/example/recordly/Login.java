package com.example.recordly;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageClickListener;
import com.synnapps.carouselview.ImageListener;


public class Login extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;
    Button registerButton;
    boolean doubleBackToExitPressedOnce = false;
    float v = 0;

    //carousel array
    private int mImages [] = new int[] {
    R.drawable.cvsu3_cropped, R.drawable.cvsu4, R.drawable.cvsu, R.drawable.cvsu2,
    };

    private String mImagesTitle [] = new String[]{
      "Truth, Excellence, Service", "Truth, Excellence, Service", "Truth, Excellence, Service", "Truth, Excellence, Service"
    };

    //login ui & animation intro
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //top section
        CarouselView carouselView = findViewById(R.id.imageView);
        carouselView.setPageCount(mImages.length);
        carouselView.setImageListener(new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                imageView.setImageResource(mImages[position]);
            }
        });
        carouselView.setImageClickListener(new ImageClickListener() {
            @Override
            public void onClick(int position) {
                Toast.makeText(Login.this, mImagesTitle[position],Toast.LENGTH_SHORT).show();
            }
        });




        //middle to bottom sec
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        registerButton = findViewById(R.id.register);


        tabLayout.addTab(tabLayout.newTab().setText("LOGIN"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final LoginAdapter adapter = new LoginAdapter(getSupportFragmentManager(), this, tabLayout.getTabCount());

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        registerButton.setTranslationY(300);
        tabLayout.setTranslationY(300);
        registerButton.setAlpha(v);
        tabLayout.setAlpha(v);
        registerButton.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(400).start();

        registerButton = (Button) findViewById(R.id.register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegister();
            }
        });
    }
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    public void openRegister(){
        Intent intent = new Intent(this, user_details.class);
        startActivity(intent);
    }

}


