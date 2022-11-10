package com.example.recordly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class userChangePassword extends AppCompatActivity {
    EditText  pass_new1, pass_new2;
    Button changePass;
    FirebaseFirestore fireStore;

    String fp_email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_change_password);


        pass_new1 = findViewById(R.id.newpass_user1);
        pass_new2 = findViewById(R.id.renewpass_user1);
        changePass = findViewById(R.id.changepassBtn_user);
        getAndSetIntentdata();
        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fireStore = FirebaseFirestore.getInstance();


                String new_password = pass_new1.getText().toString().trim();
                String verify_new_password = pass_new2.getText().toString().trim();

                DocumentReference fp_ref = fireStore.collection("users").document(fp_email);

                Map<String, Object> updatePass = new HashMap<>();
                updatePass.put("Password ", new_password);

               if(new_password.length()<8||verify_new_password.length()<8){
                    Toast.makeText(userChangePassword.this, "Password must be eight characters long", Toast.LENGTH_SHORT).show();
                }else if(!new_password.equals(verify_new_password)){
                    Toast.makeText(userChangePassword.this, "New password didn't match", Toast.LENGTH_SHORT).show();
                }else {
                    fp_ref.update(updatePass).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Intent intent = new Intent(userChangePassword.this, Login.class);
                            Toast.makeText(userChangePassword.this, "Password changed successfully!", Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                        }
                    });
                }

            }
        });
    }
    void getAndSetIntentdata() {
        Bundle extras = getIntent().getExtras();
        if (extras!=null){
            fp_email = extras.getString("FP EMAIL");
        }
    }
}