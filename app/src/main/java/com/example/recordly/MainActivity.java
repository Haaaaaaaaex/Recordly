package com.example.recordly;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageClickListener;
import com.synnapps.carouselview.ImageListener;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    //carousel array
    private int mImages [] = new int[] {
            R.drawable.cvsu3_cropped, R.drawable.cvsu4, R.drawable.cvsu, R.drawable.cvsu2,
    };

    private String mImagesTitle [] = new String[]{
            "Truth, Excellence, Service", "Truth, Excellence, Service", "Truth, Excellence, Service", "Truth, Excellence, Service"
    };

    @Override
    protected void onStart() {
        time();
        super.onStart();
    }

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar1;


    TextView subtitle;
    ImageView iscan, isched, ihistory, iprofile;

    ImageView sync1, sync2, check1, check2;
    AnimatedVectorDrawableCompat avd;
    AnimatedVectorDrawable avd2;

    private StorageReference mStorageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

        SessionManager sessionManager = new SessionManager(this);
        HashMap <String, String> userDetails = sessionManager.getUserDetailsFromSession();

        String first_name = userDetails.get(SessionManager.KEY_FIRSTNAME);
        String s_email = userDetails.get(SessionManager.KEY_EMAIL);
        String last_name = userDetails.get(SessionManager.KEY_LASTNAME);
        String address = userDetails.get(SessionManager.KEY_ADDRESS);
        String city_add = userDetails.get(SessionManager.KEY_CITY);
        String phone = userDetails.get(SessionManager.KEY_PHONE_NUMBER);


        subtitle = findViewById(R.id.subtitle_1);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar1 = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar1);

        navigationView.bringToFront();

        ActionBarDrawerToggle Toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar1,
                R.string.open,R.string.close);

        drawerLayout.addDrawerListener(Toggle);
        Toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);



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
                Toast.makeText(MainActivity.this, mImagesTitle[position],Toast.LENGTH_SHORT).show();
            }
        });


        mStorageReference = FirebaseStorage.getInstance().getReference().child("images/"+s_email+".png");
        try{
            final File localfile = File.createTempFile(first_name+last_name,"png");
            mStorageReference.getFile(localfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                    Bitmap bitmap = BitmapFactory.decodeFile(localfile.getAbsolutePath());
                    ((ImageView)findViewById(R.id.homeDP)).setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, "Profile image not found on the database", Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }



    @Override
    public void onBackPressed() {
            //super.onBackPressed();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        if(item.getItemId() == R.id.home_nav){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        if(item.getItemId() == R.id.profile_nav){
            Intent intent = new Intent(this, profile.class);
            startActivity(intent);
        }

        if(item.getItemId() == R.id.sched_nav){
            Intent intent = new Intent(this, Schedule.class);
            startActivity(intent);
        }

        if(item.getItemId() == R.id.sign_in_nav){
            Intent intent = new Intent(this, attendance.class);
            startActivity(intent);
        }

        if(item.getItemId() == R.id.history_nav){
            Intent intent = new Intent(this, ViewRecord.class);
            startActivity(intent);
        }

        if(item.getItemId() == R.id.changePass_nav){
            Intent intent = new Intent(this, changePassword.class);
            startActivity(intent);
        }

        if(item.getItemId() == R.id.editProfile_nav){
            Intent intent = new Intent(this, edit_profile.class);
            startActivity(intent);

        }

        if(item.getItemId() == R.id.checkQR_nav){
            Intent intent = new Intent(this, QR_show.class);
            startActivity(intent);
        }

        if(item.getItemId() == R.id.about_us_nav){
            Intent intent = new Intent(this, about_devs.class);
            startActivity(intent);
        }

        if(item.getItemId() == R.id.logout_nav){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to logout?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SessionManager sessionManager = new SessionManager(MainActivity.this);
                    sessionManager.LogoutUserFromSession();
                    Intent intent = new Intent(MainActivity.this, Login.class);
                    startActivity(intent);
                }
            });
            builder.setNeutralButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //
                }
            });
            builder.show();
        }
        return true;
    }

    public void time() {
        Calendar c = Calendar.getInstance();
        SessionManager sessionManager = new SessionManager(this);
        HashMap<String, String> greetings = sessionManager.getUserDetailsFromSession();

        String s_full_name = greetings.get(SessionManager.KEY_FIRSTNAME) + " "+greetings.get(SessionManager.KEY_LASTNAME);
        String s_first_name = greetings.get(SessionManager.KEY_FIRSTNAME);
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        if (timeOfDay >= 0 && timeOfDay < 12) {
            subtitle.setText("Good Morning, \n" + s_first_name+"!");

        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            subtitle.setText("Good Afternoon, \n" + s_first_name+"!");

        } else if (timeOfDay >= 16 && timeOfDay < 24) {
            subtitle.setText("Good Evening, \n" + s_first_name+"!");


        }

    }
}



