package com.example.recordly;

import android.app.DatePickerDialog;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ViewRecord extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    String rec_email, rec_date;
    TextView viewRec, datePickText;
    Button search;
    ImageView back;

    private FirebaseFirestore fireStore = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_records);


        back = (ImageView) findViewById(R.id.back_arrow_REC);
        search = (Button) findViewById(R.id.searchBtn);

        viewRec = findViewById(R.id.records_text);
        datePickText = findViewById(R.id.datePickerText);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              loadRecord();
            }
        });

        datePickText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewRecord.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }


    //CALENDAR VIEW
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = DateFormat.getDateInstance(DateFormat.MEDIUM).format(c.getTime());


        datePickText.setText(currentDateString);

        month = month + 1;
        String rec_DATE = makeDateString(dayOfMonth, month, year);

        rec_date = datePickText.getText().toString().trim();
        search.setVisibility(View.VISIBLE);
    }

    private String makeDateString(int dayOfMonth, int month, int year) {
        return dayOfMonth + " " + month + " " + year;
    }

    public void loadRecord() {

        SessionManager sessionManager = new SessionManager(this);
        HashMap<String, String> userDetails = sessionManager.getUserDetailsFromSession();
        rec_email = userDetails.get(SessionManager.KEY_EMAIL);

            fireStore.collection("attendance").document(rec_date)
                    .collection(rec_email).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                            //Date
                            String allRec = "";
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                String Date = documentSnapshot.getString("Date");
                                String Class_started = documentSnapshot.getString("Class started");
                                String Clock_in_duration = documentSnapshot.getString("Clock in duration");
                                String Break_time = documentSnapshot.getString("Break time");
                                String Class_attended = documentSnapshot.getString("Class attended");

                                allRec += "\nClass attended: \n" + Class_attended + "\nTime Started: " + Class_started
                                        + "\nDate: " + Date
                                        + "\nSession duration: " + Clock_in_duration
                                        + "\nBreak time: " + Break_time
                                        + "\n \n";
                            }
                            viewRec.setText(allRec);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ViewRecord.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            });

    }

}