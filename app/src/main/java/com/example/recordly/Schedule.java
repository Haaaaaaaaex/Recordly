package com.example.recordly;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class Schedule extends AppCompatActivity {

    RecyclerView recyclerView;
    FloatingActionButton floatingActionButton, home, delAll;
    ImageView empty_image;
    TextView no_data;
    DB_Helper_Local dbHelper;
    ArrayList<String> sched_id, subject, section, time, time_end;
    sched_adapter sched_adapter1;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar1;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);


        /*-------------------------------------END OF DRAWER LAYOUT----------------------------------------*/
        empty_image = findViewById(R.id.empty_image);
        no_data = findViewById(R.id.no_data);
        recyclerView = findViewById(R.id.recyclerView);
        floatingActionButton = findViewById(R.id.addFab);
        home = findViewById(R.id.addFabHome);
        delAll = findViewById(R.id.addFabDelete);
        dbHelper = new DB_Helper_Local(Schedule.this);
        sched_id = new ArrayList<>();
        subject = new ArrayList<>();
        section = new ArrayList<>();
        time = new ArrayList<>();
        time_end = new ArrayList<>();

        storeDatainArrays();
        sched_adapter1 = new sched_adapter(Schedule.this, this,sched_id,subject,section,time, time_end);
        recyclerView.setAdapter(sched_adapter1);
        recyclerView.setLayoutManager(new LinearLayoutManager(Schedule.this));

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Schedule.this, AddScheduleActivity.class);
                startActivity(intent);
                finish();
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHome();
            }
        });

        delAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Schedule.this);
                builder.setTitle("Delete All Schedule");
                builder.setMessage("Are you sure you want to delete all saved schedule?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbHelper.deleteAllData();
                        recreate();
                    }
                });
                builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(Schedule.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.create().show();
            }
        });





    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            recreate();
        }
    }

    public void openHome(){
        Intent intent = new Intent(Schedule.this, MainActivity.class);
        startActivity(intent);
    }


    void storeDatainArrays(){
        Cursor cursor = dbHelper.readAllData();
        if(cursor.getCount() == 0){
            empty_image.setVisibility(View.VISIBLE);
            no_data.setVisibility(View.VISIBLE);
        }
        else{
            while(cursor.moveToNext()){
                sched_id.add(cursor.getString(0));
                subject.add(cursor.getString(1));
                section.add(cursor.getString(2));
                time.add(cursor.getString(3));
                time_end.add(cursor.getString(4));
            }
            empty_image.setVisibility(View.GONE);
            no_data.setVisibility(View.GONE);
        }
    }



}

