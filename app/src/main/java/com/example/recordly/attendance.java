package com.example.recordly;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class attendance extends AppCompatActivity {


    TextView timerText;
    Button stopStartButton;
    Button clockOut;
    Timer timer;
    Button className;
    TimerTask timerTask;
    TimerTask breakTimerTask;
    TextView breakTime;
    Double time = 0.0;
    Double bTime = 0.0;

    ImageView back;

    TextView scannedText;

    boolean onpause = false;
    boolean timerStarted = false;
    boolean valid = false;

    String id, a_subject, a_section, a_time_start, a_time_end, scanValue;
    String AES = "AES";
    String a_decrypt;
    FirebaseFirestore fireStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        String reDate = DateFormat.getDateInstance(DateFormat.SHORT).format(calendar.getTime());
        fireStore = FirebaseFirestore.getInstance();
        scannedText = findViewById(R.id.idSCAN);
        //clock in date
        TextView viewDate = (TextView) findViewById(R.id.dateText1);
        viewDate.setText(currentDate);


        //class name
        className = (Button) findViewById(R.id.classview);
        //clock in duration
        timerText = (TextView) findViewById(R.id.Timer1);
        //break time duration
        breakTime = (TextView) findViewById(R.id.breakTime1);

        back = findViewById(R.id.back_arrow_ATT);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(attendance.this, MainActivity.class);
                startActivity(intent);
            }
        });

        //pause button
        stopStartButton = (Button) findViewById(R.id.Timein1);
        timer = new Timer();

        clockOut = (Button) findViewById(R.id.clockOut1);
        //start clock in button
        clockOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder alert = new AlertDialog.Builder(attendance.this);
                alert.setTitle(className.getText().toString());
                alert.setMessage("Submit your attendance to this class?");
                alert.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        insertData();
                        recreate();
                    }
                });
                alert.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //
                    }
                });
                alert.show();
            }
        });
        //CLOCK IN BUTTON
        stopStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String a_scan = scannedText.getText().toString().trim();
                String a_class = className.getText().toString().trim();


                if (a_class.equals("CLICK HERE TO CHOOSE \n A CLASS SCHEDULE")) {
                    Toast.makeText(attendance.this, "Choose class to attend", Toast.LENGTH_SHORT).show();
                } else if (a_scan.equals("")) {
                    scanner();
                    clockOut.setVisibility(View.VISIBLE);
                } else if (valid == false) {
                    scanner();
                } else {
                    //sakit sa ulo neto wew, reverse the procedure
                    //did a boolean onpause for breaktime, this time the condition musnt met first
                    if (onpause == true) {
                        onpause = false;
                        breakTimerTask.cancel();
                    }
                    //starting to clock in, need to satisfy the condition first
                    if (timerStarted == false) {
                        timerStarted = true; //make the TS true
                        setButtonUI("GO BREAK TIME", R.color.Red);
                        startTimer();
                    } else {
                        timerStarted = false;
                        onpause = true; //after showing the "resume clock in  day" make the onpause true again
                        setButtonUI("RESUME CLOCK-IN DAY", R.color.Green);
                        timerTask.cancel();
                        breakTimer();
                    }
                }
            }

        });

        //select class to attend
        className.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(attendance.this, Schedule.class);
                startActivity(intent);
            }
        });
        getAndSetIntentdata();
    }


    public void scanner() {
        IntentIntegrator intentIntegrator = new IntentIntegrator(attendance.this);
        intentIntegrator.setPrompt("to open flash, use up volume key");//prompt a text to use flash
        intentIntegrator.setBeepEnabled(true);//set beep
        intentIntegrator.setOrientationLocked(true);//to lock orientation
        intentIntegrator.setCaptureActivity(Camera.class);
        intentIntegrator.initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        fireStore = FirebaseFirestore.getInstance();
        scannedText.setText(intentResult.getContents());
        String a_scan = scannedText.getText().toString().trim();

        SessionManager sessionManager = new SessionManager(this);
        HashMap<String, String> userDetails = sessionManager.getUserDetailsFromSession();
        String s_email = userDetails.get(SessionManager.KEY_EMAIL);//get session email
        String s_encrypt = userDetails.get(SessionManager.KEY_ENCRYPT);//encryption key from session
        try {
            a_decrypt = decrypt(a_scan, s_encrypt);
            DocumentReference ref = fireStore
                    .collection("users")
                    .document(s_email)
                    .collection("password")
                    .document(a_decrypt);
            ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        Toast.makeText(attendance.this, "VALID QR CODE", Toast.LENGTH_SHORT).show();
                        valid = true;
                    }
                }
            });

        } catch (Exception e) {
            Toast.makeText(attendance.this, "INVALID QR CODE", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private SecretKeySpec generateKey(String qr_password) throws Exception {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = qr_password.getBytes("UTF-8");
        digest.update(bytes, 0, bytes.length);
        byte[] key = digest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        return secretKeySpec;
    }

    //decrypt the qr code content
    public String decrypt(String scan_content, String password) throws Exception {
        SecretKeySpec key = generateKey(password);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedValue = Base64.decode(scan_content, Base64.DEFAULT);
        byte[] decValue = c.doFinal(decodedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }

    //insert attendance to database
    public void insertData() {
        Calendar calendar = Calendar.getInstance();
        String reDate = DateFormat.getDateInstance(DateFormat.SHORT).format(calendar.getTime());
        String Date1 = DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendar.getTime());

        String currentTime = new SimpleDateFormat("HH:mm:aa", Locale.getDefault()).format(new Date());

        SessionManager sessionManager = new SessionManager(this);
        HashMap<String, String> userDetails = sessionManager.getUserDetailsFromSession();
        String s_email = userDetails.get(SessionManager.KEY_EMAIL);

        Map<String, String> items = new HashMap<>();
        String a_start = currentTime.trim();
        String a_date = reDate.trim();
        String a_date1 = Date1;
        String a_clockin = timerText.getText().toString().trim();
        String a_break = breakTime.getText().toString().trim();
        String a_class = className.getText().toString().trim();

        items.put("Date", a_date);
        items.put("Class started", a_start);
        items.put("Clock in duration", a_clockin);
        items.put("Break time", a_break);
        items.put("Class attended", a_class);

        fireStore.collection("attendance").document(a_date1)
                .collection(s_email)
                .document()
                .set(items);
        Intent intent = new Intent(attendance.this, attendance_dialog.class);
        startActivity(intent);
        finish();
    }

    //TEXT STRING FROM SCHEDULEVIEW INTO ATTENDANCE
    void getAndSetIntentdata() {
        if (getIntent().hasExtra("SUBJECT")
                && getIntent().hasExtra("SECTION")
                && getIntent().hasExtra("START TIME")
                && getIntent().hasExtra("TIME END")) {

            a_subject = getIntent().getStringExtra("SUBJECT");
            a_section = getIntent().getStringExtra("SECTION");
            a_time_start = getIntent().getStringExtra("START TIME");
            a_time_end = getIntent().getStringExtra("TIME END");
            //Set Intent Data
            className.setText(a_subject + ", " + a_section);

        } else {
            Toast.makeText(this, "Select class to attend", Toast.LENGTH_SHORT).show();
        }
    }


    //reset method
    public void resetTime() {
        //reset the whole timer to zero
        if (timerTask != null) {
            timerTask.cancel();
            time = 0.0;
            timerStarted = false;
            timerText.setText(formatTime(0, 0, 0));

            if (bTime != 0.0) {
                //break time reset
                breakTimerTask.cancel();
                bTime = 0.0;
                onpause = true;
                breakTime.setText(formatTime(0, 0, 0));
            }
        }
    }

    //disable back button
    @Override

    public void onBackPressed() {
        //super.onBackPressed();
    }

    //change color button
    private void setButtonUI(String start, int color) {
        stopStartButton.setText(start);
        stopStartButton.setTextColor(ContextCompat.getColor(attendance.this, color));
    }

    //CLOCK IN method
    private void startTimer() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                attendance.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        time++;
                        timerText.setText(getTimerText());
                    }
                });
            }

        };
        timer.scheduleAtFixedRate(timerTask, 0, 1000);
    }

    //BREAK  TIME
    private void breakTimer() {
        breakTimerTask = new TimerTask() {
            @Override
            public void run() {
                attendance.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bTime++;
                        breakTime.setText(getBreakTimerText());
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(breakTimerTask, 0, 1000);
    }


    //clock timer for clock-in
    private String getTimerText() {
        int rounded = (int) Math.round(time);
        int seconds = ((rounded % 86400) % 3600) % 60;
        int mins = ((rounded % 86400) % 3600) / 60;
        int hrs = ((rounded % 86400) / 3600);

        return formatTime(seconds, mins, hrs);
    }

    //clock timer format for breaktime
    private String getBreakTimerText() {

        int rounded = (int) Math.round(bTime);
        int seconds = ((rounded % 86400) % 3600) % 60;
        int mins = ((rounded % 86400) % 3600) / 60;
        int hrs = ((rounded % 86400) / 3600);

        return formatTime(seconds, mins, hrs);
    }

    private String formatTime(int seconds, int mins, int hrs) {

        return String.format("%02d", hrs) + " : " + String.format("%02d", mins) + " : " + String.format("%02d", seconds);
    }
}