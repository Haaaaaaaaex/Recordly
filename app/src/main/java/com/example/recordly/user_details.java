package com.example.recordly;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class user_details extends AppCompatActivity {

    TextView firstname, lastname, add, city, phoneNO;
    Activity activity;
    Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        firstname = findViewById(R.id.first_name1);
        lastname = findViewById(R.id.last_name1);
        add = findViewById(R.id.addressREG1);
        city = findViewById(R.id.addressCity1);
        phoneNO = findViewById(R.id.pNumber1);
        next = findViewById(R.id.nextBtn);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String fname = firstname.getText().toString().trim();
                String lname = lastname.getText().toString().trim();
                String address = add.getText().toString().trim();
                String city_add = city.getText().toString().trim();
                String phone = phoneNO.getText().toString().trim();


                if (fname.equals("") || lname.equals("") || address.equals("") || city_add.equals("") || phone.equals("")) {
                    Toast.makeText(user_details.this, "All fields required", Toast.LENGTH_SHORT).show();
                } else if (checkNumber() == true) {

                    Intent intent = new Intent(user_details.this, Register.class);
                    intent.putExtra("FIRST NAME", upperFirstLetter(fname));
                    intent.putExtra("LAST NAME", upperFirstLetter(lname));
                    intent.putExtra("ADDRESS", upperFirstLetter(address));
                    intent.putExtra("CITY", upperFirstLetter(city_add));
                    intent.putExtra("PHONE", phone);
                    startActivity(intent);

                }
            }
        });




    }
    private String upperFirstLetter (String text){
        String output = "";

        String [] textArray = text.trim().split("\\s+");
        for(int i = 0; i < textArray.length; i++){
            textArray[i] = textArray[i].substring(0, 1).toUpperCase() + textArray[i].substring(1);
        }
        for(int i = 0; i < textArray.length; i++){
            output = output + textArray[i]+" ";
        }
        return output.trim();
    }

    private boolean checkNumber() {
        String phone = phoneNO.getText().toString().trim();
        if (phone.length() < 11 || phone.length() > 11) {
            Toast.makeText(user_details.this, "Invalid phone number", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }

    }
}