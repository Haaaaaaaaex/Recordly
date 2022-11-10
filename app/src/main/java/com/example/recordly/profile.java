package com.example.recordly;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;

public class profile extends AppCompatActivity {
    TextView full_name,email, phone, add, city, edit,save;
    ImageView back;

    private StorageReference mStorageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);





        SessionManager sessionManager = new SessionManager(this);
        HashMap<String, String> userDetails = sessionManager.getUserDetailsFromSession();

        String first_name = userDetails.get(SessionManager.KEY_FIRSTNAME);
        String last_name = userDetails.get(SessionManager.KEY_LASTNAME);
        String s_email = userDetails.get(SessionManager.KEY_EMAIL);
        String s_phone = userDetails.get(SessionManager.KEY_PHONE_NUMBER);
        String s_add = userDetails.get(SessionManager.KEY_ADDRESS);
        String s_city = userDetails.get(SessionManager.KEY_CITY);

        full_name = findViewById(R.id.profile_name);
        full_name.setText(first_name+" "+last_name);

        mStorageReference = FirebaseStorage.getInstance().getReference().child("images/"+s_email+".png");
        try{
            final File localfile = File.createTempFile(first_name+last_name,"png");
            mStorageReference.getFile(localfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                    Bitmap bitmap = BitmapFactory.decodeFile(localfile.getAbsolutePath());
                    ((ImageView)findViewById(R.id.profile_image)).setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(profile.this, "Profile image not found on the database", Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }


        email = findViewById(R.id.profile_email);
        email.setText(s_email);

        phone = findViewById(R.id.profile_phone);
        phone.setText(s_phone);

        add = findViewById(R.id.address_input);
        add.setText(s_add);

        city = findViewById(R.id.address_city_input);
        city.setText(s_city);

        back = findViewById(R.id.back_arrow);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(profile.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }



}