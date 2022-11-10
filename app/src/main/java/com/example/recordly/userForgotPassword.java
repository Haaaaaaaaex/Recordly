package com.example.recordly;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.Random;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class userForgotPassword extends AppCompatActivity {

    Button sendCode, valid;
    EditText digit1, digit2, digit3, digit4, FP_email;
    String encrypt_key, s_email;
    FirebaseFirestore fireStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_forgot_password);

        digit1 = findViewById(R.id.FPdigit1);
        digit2 = findViewById(R.id.FPdigit2);
        digit3 = findViewById(R.id.FPdigit3);
        digit4 = findViewById(R.id.FPdigit4);
        FP_email = findViewById(R.id.email_fp1);
        sendCode = findViewById(R.id.send_code);
        valid = findViewById(R.id.validate_FP);
        encrypt_key = key_gen(4);

        digit1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>0){
                    digit2.requestFocus();
                }
            }
        });
        digit2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>0){
                    digit3.requestFocus();
                }
            }
        });
        digit3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>0){
                    digit4.requestFocus();
                }
            }
        });



        sendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s_email = FP_email.getText().toString().trim();
                if (s_email.equals("")) {
                    Toast.makeText(userForgotPassword.this, "Email cannot be blank", Toast.LENGTH_SHORT).show();
                } else {
                    digit1.requestFocus();
                    fireStore = FirebaseFirestore.getInstance();
                    DocumentReference ref = fireStore.collection("users").document(s_email);
                    ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                sendCode.setEnabled(false);
                                btnCount();
                                sendMail();
                            } else {
                                Toast.makeText(userForgotPassword.this, "Invalid email, please check again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        valid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String checkCode = digit1.getText().toString()+digit2.getText().toString()+digit3.getText().toString()+digit4.getText().toString();
                if(encrypt_key.equals(checkCode)){
                    Intent intent = new Intent(userForgotPassword.this, userChangePassword.class);
                    String fp_email = FP_email.getText().toString().trim();
                    intent.putExtra("FP EMAIL", fp_email);
                    startActivity(intent);
                }else{
                    Toast.makeText(userForgotPassword.this, "Invalid 4 Digit Code", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    public void btnCount() {
        new CountDownTimer(11000, 1000) {
            public void onTick(long millisUntilFinished) {
                sendCode.setText("SEND CODE" + "(" + millisUntilFinished / 1000 + ")");
                Drawable d = getResources().getDrawable(R.drawable.disable_btn);
                sendCode.setBackgroundDrawable(d);
                valid.setVisibility(View.INVISIBLE);
            }

            public void onFinish() {
                sendCode.setEnabled(true);
                sendCode.setText("SEND CODE");
                Drawable f = getResources().getDrawable(R.drawable.ripple_effect);
                sendCode.setBackgroundDrawable(f);
                valid.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    //generate 4 random nums 0-9
    private String key_gen(int length) {
        char[] chars = "1234567890".toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            char c = chars[random.nextInt(chars.length)];
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }

    public void sendMail() {
        AccountNotification accountNotification = new AccountNotification();

        String e_subject = "Verification Code";
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(accountNotification.notif_username, accountNotification.notif_password);
            }
        });

        try {
            Message message = new MimeMessage(session);  //Initialize MimeMessage
            Multipart multipart = new MimeMultipart();
            message.setFrom(new InternetAddress(accountNotification.notif_username));//sender address
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(s_email)); //recipients

            message.setSubject(e_subject); //Email Contents

            BodyPart messageBodyPart = new MimeBodyPart(); //set body part for message
            String htmlText = "<h3>4 DIGIT CODE <br>" + encrypt_key + "</h3> <br>" +
                    "If you didn't authorized this action you may contact the developers at: <br>" +
                    "renzchristian.palma@cvsu.edu.ph<br>aronshun.samosino@cvsu.edu.ph<br><b>Thank you!</b>";
            messageBodyPart.setContent(htmlText, "text/html");
            multipart.addBodyPart(messageBodyPart);

            message.setContent(multipart);

            new userForgotPassword.SendMail().execute(message);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class SendMail extends AsyncTask<Message, String, String> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(userForgotPassword.this, "Sending 4 Digit Code..", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(Message... messages) {
            try {
                Transport.send(messages[0]);
                return "Success";
            } catch (MessagingException e) {
                e.printStackTrace();
                return "Error";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("Success")) {
                Toast.makeText(userForgotPassword.this, "4 Digit Code Sent!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(userForgotPassword.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
