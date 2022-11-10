package com.example.recordly;


import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddScheduleActivity extends AppCompatActivity {


    EditText courseSubject, classSection;
    Button timeBtn, saveBtn, endBtn;
    int hours, minute;
    int hours1, minute1;
    TextView schedTime, schedEnd;
    DB_Helper_Local dbHelper;
    FirebaseFirestore fireStore;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_sched);

        fireStore = FirebaseFirestore.getInstance();

        timeBtn = findViewById(R.id.timeBtn);
        saveBtn = findViewById(R.id.saveBtn);
        classSection = findViewById(R.id.section_name);
        courseSubject = findViewById(R.id.course_subject);
        schedTime = findViewById(R.id.schedule_time);
        schedEnd = findViewById(R.id.schedule_time_end);
        endBtn = findViewById(R.id.timeBtnEnd);


        dbHelper = new DB_Helper_Local(this);

        timeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        AddScheduleActivity.this,
                        android.R.style.Theme_Holo_Dialog_MinWidth,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minuteSelect) {
                                hours = hourOfDay;
                                minute = minuteSelect;
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(0,0,0,hours,minute);
                                schedTime.setText(DateFormat.format("hh:mm:aa",calendar));
                            }
                        },12,0,false
                );
                timePickerDialog.show();
            }
        });

        endBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        AddScheduleActivity.this,
                        android.R.style.Theme_Holo_Dialog_MinWidth,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay1, int minuteSelect1) {
                                hours1 = hourOfDay1;
                                minute1 = minuteSelect1;
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(0,0,0,hours1,minute1);
                                schedEnd.setText(DateFormat.format("hh:mm:aa",calendar));
                            }
                        },12,0,false
                );
                timePickerDialog.show();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subject = courseSubject.getText().toString();
                String section = classSection.getText().toString();
                String time = schedTime.getText().toString();
                String time_end = schedEnd.getText().toString();
                if (subject.equals("") || section.equals("") || time.equals("") || time_end.equals("")) {
                    Toast.makeText(AddScheduleActivity.this, "All fields required", Toast.LENGTH_SHORT).show();
                } else {
                    Boolean insert = dbHelper.insertSchedule(subject, section, time, time_end);
                    if (insert == true) {


                        SessionManager sessionManager = new SessionManager(AddScheduleActivity.this);
                        HashMap<String, String> userDetails = sessionManager.getUserDetailsFromSession();
                        String s_email = userDetails.get(SessionManager.KEY_EMAIL);

                        Map<String, String> items = new HashMap<>();
                        items.put("Subject", subject);
                        items.put("Session start", time);
                        items.put("Session end", time_end);
                        items.put("Section", section);
                        fireStore.collection("users").document(s_email)
                                .collection("schedule")
                                .document()
                                .set(items);

                        Toast.makeText(AddScheduleActivity.this, "Schedule added successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                        Intent intent = new Intent(AddScheduleActivity.this, Schedule.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(AddScheduleActivity.this, "Schedule process failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }
}