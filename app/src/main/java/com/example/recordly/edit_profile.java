package com.example.recordly;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class edit_profile extends AppCompatActivity {
    @Override
    protected void onStart() {
        setText();
        super.onStart();
    }

    FirebaseFirestore fireStore;
    Button submit;
    EditText first_name, last_name, email,address,city,phone;
    String getFn, getLn, getEm,getAdd,getCity,getPh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        SessionManager sessionManager = new SessionManager(this);
        HashMap<String, String> userDetails = sessionManager.getUserDetailsFromSession();
        String s_email = userDetails.get(SessionManager.KEY_EMAIL);

        submit = findViewById(R.id.EditsubmitBtn);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(edit_profile.this);
                builder.setTitle("Submit ");
                builder.setMessage("Are you sure that the details are correct?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fireStore = FirebaseFirestore.getInstance();
                        DocumentReference ref = fireStore.collection("users").document(s_email);

                        Map<String, Object> updateDetails = new HashMap<>();
                        getFn = first_name.getText().toString();
                        getLn = last_name.getText().toString();
                        getEm = email.getText().toString();
                        getAdd = address.getText().toString();
                        getCity = city.getText().toString();
                        getPh = phone.getText().toString();

                        updateDetails.put("First name ", getFn);
                        updateDetails.put("Last name ", getLn);
                        updateDetails.put("Address ", getAdd);
                        updateDetails.put("City ", getCity);
                        updateDetails.put("Phone number ", getPh);
                        updateDetails.put("Email ", getEm);

                        ref.update(updateDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(edit_profile.this, "Information successfully updated!", Toast.LENGTH_SHORT).show();

                                AlertDialog.Builder builder = new AlertDialog.Builder(edit_profile.this);
                                builder.setTitle("Success!");
                                builder.setMessage("Information successfully updated! \n Please login again");
                                builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        SessionManager sessionManager = new SessionManager(edit_profile.this);
                                        sessionManager.LogoutUserFromSession();
                                        Intent intent = new Intent(edit_profile.this, Login.class);
                                        startActivity(intent);
                                    }
                                });
                                builder.create().show();
                            }
                        });
                    }
                });
                builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(edit_profile.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.create().show();

            }

        });









    }
    //PASS INPUT INFORMATION TO NEXT REGISTRATION PAGE

    void setText(){
        SessionManager sessionManager = new SessionManager(this);
        HashMap<String, String> userDetails = sessionManager.getUserDetailsFromSession();
        String s_first_name = userDetails.get(SessionManager.KEY_FIRSTNAME);
        String s_email = userDetails.get(SessionManager.KEY_EMAIL);
        String s_last_name = userDetails.get(SessionManager.KEY_LASTNAME);
        String s_address = userDetails.get(SessionManager.KEY_ADDRESS);
        String s_city_add = userDetails.get(SessionManager.KEY_CITY);
        String s_phone = userDetails.get(SessionManager.KEY_PHONE_NUMBER);

        first_name = findViewById(R.id.editfirst_name1);
        last_name = findViewById(R.id.editlast_name1);
        email = findViewById(R.id.editEmail1);
        address = findViewById(R.id.editaddress1);
        city = findViewById(R.id.editaddressCity1);
        phone = findViewById(R.id.editNumber1);
        first_name.setText(s_first_name);
        last_name.setText(s_last_name);
        email.setText(s_email);
        address.setText(s_address);
        city.setText(s_city_add);
        phone.setText(s_phone);
    }
}