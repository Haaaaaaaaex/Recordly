package com.example.recordly;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
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
import javax.mail.util.ByteArrayDataSource;

public class changePassword extends AppCompatActivity {

    EditText pass_old, pass_new1, pass_new2;
    Button changePass;
    FirebaseFirestore fireStore;
    String s_email, s_fname, s_lname, se_password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        pass_old = findViewById(R.id.oldpass1);
        pass_new1 = findViewById(R.id.newpass1);
        pass_new2 = findViewById(R.id.renewpass1);
        changePass = findViewById(R.id.changepassBtn);

        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SessionManager sessionManager = new SessionManager(changePassword.this);
                HashMap<String, String> getDetails = sessionManager.getUserDetailsFromSession();
                s_email = getDetails.get(SessionManager.KEY_EMAIL);
                s_fname = getDetails.get(SessionManager.KEY_FIRSTNAME);
                s_lname = getDetails.get(SessionManager.KEY_LASTNAME);
                se_password = getDetails.get(SessionManager.KEY_PASSWORD);//session password

                fireStore = FirebaseFirestore.getInstance();
                DocumentReference ref = fireStore.collection("users").document(s_email);

                //user inputs
                String verify_pass = pass_old.getText().toString();
                String new_password = pass_new1.getText().toString().trim();
                String verify_new_password = pass_new2.getText().toString().trim();

                Map<String, Object> updatePass = new HashMap<>();
                updatePass.put("Password ", new_password);

                if(!verify_pass.equals(se_password)){
                    Toast.makeText(changePassword.this, "Wrong old password", Toast.LENGTH_SHORT).show();
                }else if(new_password.equals(verify_pass)){
                    Toast.makeText(changePassword.this, "new password can't be same with old password", Toast.LENGTH_SHORT).show();
                }else if(new_password.length()<8||verify_new_password.length()<8){
                    Toast.makeText(changePassword.this, "Password must be eight characters long", Toast.LENGTH_SHORT).show();
                }else if(!new_password.equals(verify_new_password)){
                    Toast.makeText(changePassword.this, "New password didn't match", Toast.LENGTH_SHORT).show();
                }else {
                    ref.update(updatePass).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            sendMail();
                        }
                    });
                }
            }
        });

    }

    //CALENDAR VIEW


    public void sendMail() {

        AccountNotification accountNotification = new AccountNotification();

        String e_subject = "Recordly change password";

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm:aa", Locale.getDefault()).format(new Date());

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
            String htmlText = "<h3>Greetings!</h3> <br> <b>" + s_fname + " " + s_lname + ",</b><br>" + "The password on your account: " + s_email + " is recently changed on "
                    + currentDate + ", " + currentTime +
                    " if you didn't authorized this action you may contact the developers at: <br>" +
                    "renzchristian.palma@cvsu.edu.ph<br>aronshun.samosino@cvsu.edu.ph<br><b>Thank you!</b>";
            messageBodyPart.setContent(htmlText, "text/html");
            multipart.addBodyPart(messageBodyPart);

            message.setContent(multipart);

            new changePassword.SendMail().execute(message);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class SendMail extends AsyncTask<Message, String, String> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(changePassword.this,
                    "Please wait", "Changing password...", true);
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
            progressDialog.dismiss();
            if (s.equals("Success")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(changePassword.this);
                builder.setCancelable(false);
                builder.setTitle("Success!");
                builder.setMessage("Password successfully updated,\nplease login again");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SessionManager sessionManager = new SessionManager(changePassword.this);
                        sessionManager.LogoutUserFromSession();
                        Intent intent = new Intent(changePassword.this, Login.class);
                        startActivity(intent);
                    }
                });
                builder.show();
            } else {
                Toast.makeText(changePassword.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
