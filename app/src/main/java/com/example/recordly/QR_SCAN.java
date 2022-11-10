package com.example.recordly;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class QR_SCAN extends AppCompatActivity {

    Button scan, validate;
    TextView scannedText;

    FirebaseFirestore fireStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_scan);

        scannedText = findViewById(R.id.scan_value);

        scan = findViewById(R.id.scan_btn);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(QR_SCAN.this);
                intentIntegrator.setPrompt("to open flash, use up volume key");//prompt a text to use flash
                intentIntegrator.setBeepEnabled(true);//set beep
                intentIntegrator.setOrientationLocked(true);//to lock orientation
                intentIntegrator.setCaptureActivity(Camera.class);
                intentIntegrator.initiateScan();
            }
        });

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        fireStore = FirebaseFirestore.getInstance();
        //scannedText.setText(intentResult.getContents());
        String a_scan = intentResult.getContents().trim();
        DocumentReference ref = fireStore.collection("users").document(a_scan);
        ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {

            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    Toast.makeText(QR_SCAN.this, "VALID QR CODE", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(QR_SCAN.this, "INVALID QR CODE", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(QR_SCAN.this, "Failed to reach database..", Toast.LENGTH_SHORT).show();
            }
        });


    }
}


