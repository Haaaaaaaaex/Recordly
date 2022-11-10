package com.example.recordly;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class UpdateSchedule extends AppCompatActivity {

    EditText subject_input, section_input;
    Button updateBtn, updateTimeButton, updateTimeButtonEnd, delBtn;
    TextView time_input, end_input;

    int hours, minute;
    int hours1, minute1;

    String id, s_subject, s_section, s_time_start, s_time_end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_schedule);

        //TextViews
        subject_input = findViewById(R.id.update_course_subject);
        section_input = findViewById(R.id.update_section_name);

        //timepickerView
        time_input = findViewById(R.id.update_schedule_time);
        end_input = findViewById(R.id.update_schedule_time_end);

        //BUTTON
        updateTimeButton = findViewById(R.id.update_timeBtn);
        updateTimeButtonEnd = findViewById(R.id.update_timeBtnEnd);
        updateBtn = findViewById(R.id.update_Btn);
        delBtn = findViewById(R.id.del_update_Btn);

        //changes here


        //onClick of two time picker dialog
        //first button
        updateTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        UpdateSchedule.this,
                        android.R.style.Theme_Holo_Dialog_MinWidth,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minuteSelect) {
                                hours = hourOfDay;
                                minute = minuteSelect;
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(0,0,0,hours,minute);
                                time_input.setText(DateFormat.format("hh:mm:aa",calendar));
                            }
                        },12,0,false
                );
                timePickerDialog.show();
            }
        });
        //2nd button
        updateTimeButtonEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        UpdateSchedule.this,
                        android.R.style.Theme_Holo_Dialog_MinWidth,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay1, int minuteSelect1) {
                                hours1 = hourOfDay1;
                                minute1 = minuteSelect1;
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(0,0,0,hours1,minute1);
                                end_input.setText(DateFormat.format("hh:mm:aa",calendar));
                            }
                        },12,0,false
                );
                timePickerDialog.show();
            }
        });
        getAndSetIntentdata();

        //update data
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DB_Helper_Local dbHelper = new DB_Helper_Local(UpdateSchedule.this);
                s_subject = subject_input.getText().toString().trim();
                s_section = section_input.getText().toString().trim();
                s_time_start = time_input.getText().toString().trim();
                s_time_end = end_input.getText().toString().trim();
                Boolean update = dbHelper.updateData(id, s_subject, s_section, s_time_start, s_time_end);
                if (update == true) {
                    Toast.makeText(UpdateSchedule.this, "Record updated! Press back and check", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //delete data button
        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDelete();
            }
        });
        //end line
    }

    //get the Intent Data
    void getAndSetIntentdata(){
        if(getIntent().hasExtra("id") && getIntent().hasExtra("SUBJECT") && getIntent().hasExtra("SECTION")
                && getIntent().hasExtra("START TIME")&& getIntent().hasExtra("TIME END")){
            id = getIntent().getStringExtra("id");
            s_subject = getIntent().getStringExtra("SUBJECT");
            s_section = getIntent().getStringExtra("SECTION");
            s_time_start = getIntent().getStringExtra("START TIME");
            s_time_end = getIntent().getStringExtra("TIME END");

            //Set Intent Data
            subject_input.setText(s_subject);
            section_input.setText(s_section);
            time_input.setText(s_time_start);
            end_input.setText(s_time_end);

        }else{
            Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();
        }
    }

    void AlertDelete(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Schedule");
        builder.setMessage("Are you sure you want to delete schedule from class " + s_section +" ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DB_Helper_Local dbHelper = new DB_Helper_Local(UpdateSchedule.this);
                dbHelper.deleteOneRow(id);
                finish();
            }
        });
        builder.setNeutralButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(UpdateSchedule.this,"You canceled the operation",Toast.LENGTH_SHORT).show();
            }
        });
        builder.create().show();
    }
}
