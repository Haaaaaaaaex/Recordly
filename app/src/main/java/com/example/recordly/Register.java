package com.example.recordly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Base64;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.scottyab.aescrypt.AESCrypt;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import javax.activation.DataHandler;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
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

public class Register extends AppCompatActivity {

    FirebaseFirestore fireStore;
    FirebaseAuth fAuth;


    Button register;
    EditText first_n, last_n, emailadd, schoolID;
    String id;

    ImageView QR_CODE, download;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar1;
    String f_name, l_name, address, city, number;

    String AES = "AES";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        QR_CODE = findViewById(R.id.QR_GEN);

        getAndSetIntentdata();
        emailadd = findViewById(R.id.email_reg1);
        register = findViewById(R.id.register_btn);
        schoolID = findViewById(R.id.schoolID_reg1);//password

        //REGISTER BUTTON
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = emailadd.getText().toString();


                String u_ID = schoolID.getText().toString();
                String encrypt_key = key_gen(6);

                String e_subject = "Recordly registration";

                //REGISTER BEGINS HERE!!!
                MultiFormatWriter writer = new MultiFormatWriter();
                if (f_name.equals("") || l_name.equals("") || email.equals("") || u_ID.equals("")) {
                    Toast.makeText(Register.this, "All Fields Are Required", Toast.LENGTH_SHORT).show();
                }else if(u_ID.length()<8){
                    Toast.makeText(Register.this, "Password must be 8 characters long", Toast.LENGTH_SHORT).show();
                }else{
                    //FIRESTORE FIREBASE
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("First name ", f_name);
                    hashMap.put("Last name ", l_name);
                    hashMap.put("Address ", address);
                    hashMap.put("City ", city);
                    hashMap.put("Phone number ", number);
                    hashMap.put("Email ", email);
                    hashMap.put("Encryption Password ", encrypt_key);
                    hashMap.put("Password ", u_ID);

                    fireStore = FirebaseFirestore.getInstance();  //initialize firebase root data
                    DocumentReference ref = fireStore.collection("users").document(email);   //reference (if user is already registered)
                    ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                Toast.makeText(Register.this, "Email already exists", Toast.LENGTH_SHORT).show();
                            } else  if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                Toast.makeText(Register.this, "Invalid email address", Toast.LENGTH_SHORT).show();
                            } else {
                                try {
                                    //GENERATE QR CODE
                                    BitMatrix matrix = writer.encode(encrypt_qr(u_ID,encrypt_key), BarcodeFormat.QR_CODE, 350, 350);
                                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();   //barcode encoder
                                    Bitmap bitmap = barcodeEncoder.createBitmap(matrix);//create qr code to matrix
                                    QR_CODE.setImageBitmap(bitmap); //set qr code to imageview
                                    InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    manager.hideSoftInputFromWindow(schoolID.getWindowToken(), 0);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                //INSERT DATA USERINFO TO DATABASE PATH
                                    fireStore.collection("users").document(email).set(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(Register.this, "Registered Successfully", Toast.LENGTH_SHORT).show();

                                            //SEND EMAIL BEGINS HEREE!! (JAVAMAILER API)
                                            Properties properties = new Properties();
                                            properties.put("mail.smtp.auth", "true");
                                            properties.put("mail.smtp.starttls.enable", "true");
                                            properties.put("mail.smtp.host", "smtp.gmail.com");
                                            properties.put("mail.smtp.port", "587");
                                            AccountNotification accountNotification = new AccountNotification();
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
                                                        InternetAddress.parse(emailadd.getText().toString().trim())); //recipients

                                                message.setSubject(e_subject); //Email Contents

                                                BodyPart messageBodyPart = new MimeBodyPart(); //set body part for message
                                                String htmlText = "<H1>Welcome to Recordly!</H1> " +
                                                        "<h3>Greetings!</h3> <b>" + f_name + " " + l_name + "</b>,<br> your email: " + email + " is now registered " +
                                                        "to <b>Recordly: Mobile Attendance System!</b> <br> Password: " + u_ID +
                                                        "<br> See attachments for your unique QR Code, <b> Thank you! </b>"; //EMAIL MESSAGE HAHA ANG HABA
                                                messageBodyPart.setContent(htmlText, "text/html");
                                                multipart.addBodyPart(messageBodyPart);

                                                Bitmap bitmap = ((BitmapDrawable) QR_CODE.getDrawable()).getBitmap();//get the drawable file of QR gen

                                                ByteArrayOutputStream baos = new ByteArrayOutputStream(); //initialize BAOS
                                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                                byte[] imageInByte = baos.toByteArray();

                                                MimeBodyPart imageBodyPart = new MimeBodyPart(); //body part for attachments
                                                ByteArrayDataSource bds = new ByteArrayDataSource(imageInByte, "image/jpeg");
                                                imageBodyPart.setDataHandler(new DataHandler(bds));
                                                imageBodyPart.setHeader("Content-ID", "<image>");
                                                imageBodyPart.setFileName("QR_CODE.jpg");
                                                multipart.addBodyPart(imageBodyPart);
                                                message.setContent(multipart);

                                                new SendMail().execute(message);

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(Register.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            }
                        }
                    });
                }
            }
        });
    }
    //KEY GEN FOR ENCRYPT PASSWORD
    private String key_gen(int length){
        char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        for(int i = 0; i< length; i++){
            char c = chars[random.nextInt(chars.length)];
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }

    private String encrypt_qr(String data, String qr_password) throws Exception{
        SecretKeySpec key = generateKey(qr_password);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(data.getBytes());
        String encryptedValue = Base64.encodeToString(encVal, Base64.DEFAULT);
        return encryptedValue;
    }

    private SecretKeySpec generateKey(String qr_password) throws Exception {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = qr_password.getBytes("UTF-8");
        digest.update(bytes, 0,bytes.length);
        byte[] key = digest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        return secretKeySpec;
    }

    //PASS INPUT INFORMATION TO NEXT REGISTRATION PAGE
    void getAndSetIntentdata() {
        Bundle extras = getIntent().getExtras();
        if (extras!=null){
            f_name = extras.getString("FIRST NAME");
            l_name = extras.getString("LAST NAME");
            address = extras.getString("ADDRESS");
            city = extras.getString("CITY");
            number = extras.getString("PHONE");
        }

    }



    private class SendMail extends AsyncTask<Message, String, String> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(Register.this,
                    "Please wait", "Sending email...", true);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                builder.setCancelable(false);
                builder.setTitle("Registered successfully");
                builder.setMessage("Welcome! your information has been sent to your email. \nPlease proceed to login page ");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Register.this, Login.class);
                        startActivity(intent);
                    }
                });
                builder.show();
            } else {
                Toast.makeText(Register.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }


}