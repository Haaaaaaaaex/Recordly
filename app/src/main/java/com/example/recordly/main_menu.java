package com.example.recordly;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageClickListener;
import com.synnapps.carouselview.ImageListener;

public class main_menu extends AppCompatActivity {
    //carousel array
    private int mImages [] = new int[] {
            R.drawable.cvsu3_cropped, R.drawable.cvsu4, R.drawable.cvsu, R.drawable.cvsu2,
    };

    private String mImagesTitle [] = new String[]{
            "Truth, Excellence, Service", "Truth, Excellence, Service", "Truth, Excellence, Service", "Truth, Excellence, Service"
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

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
                Toast.makeText(main_menu.this, mImagesTitle[position],Toast.LENGTH_SHORT).show();
            }
        });

    }



}
