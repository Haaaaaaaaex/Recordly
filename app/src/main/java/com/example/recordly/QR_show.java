package com.example.recordly;

import static com.google.zxing.BarcodeFormat.QR_CODE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Properties;

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

public class QR_show extends AppCompatActivity {
    String AES = "AES";
    ImageView qr_view, back;
    TextView g;
    String s_encrypt_pass, s_email,s_fname,s_lname;
    Button download, emailBtn;

    @Override
    protected void onStart() {
        super.onStart();
        generate_qr();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_show);
        qr_view = findViewById(R.id.QR_acc);
        g = findViewById(R.id.gone);
        back = findViewById(R.id.imageView2);
        download = findViewById(R.id.downloadBtnQR);
        emailBtn = findViewById(R.id.emailBtnQR);

        ActivityCompat.requestPermissions(QR_show.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        ActivityCompat.requestPermissions(QR_show.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QR_show.this, MainActivity.class);
                startActivity(intent);
            }
        });


        SessionManager sessionManager = new SessionManager(this);
        HashMap<String, String> userDetails = sessionManager.getUserDetailsFromSession();

        s_encrypt_pass = userDetails.get(SessionManager.KEY_ENCRYPT);
        s_email = userDetails.get(SessionManager.KEY_EMAIL);
        s_fname = userDetails.get(SessionManager.KEY_FIRSTNAME);
        s_lname = userDetails.get(SessionManager.KEY_LASTNAME);
        g.setText(s_encrypt_pass);

        //DOWNLOAD BUTTON (SAVE TO LOCAL DEVICE STORAGE)
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadImageGallery();
            }
        });

        //SEND QR TO REGISTERED EMAIL

        emailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMail();
            }
        });
        
    }

    public void generate_qr(){
        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            //GENERATE QR CODE
            BitMatrix matrix = writer.encode(s_encrypt_pass, BarcodeFormat.QR_CODE, 350, 350);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();   //barcode encoder
            Bitmap bitmap = barcodeEncoder.createBitmap(matrix);//create qr code to matrix
            qr_view.setImageBitmap(bitmap); //set qr code to imageview
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(g.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //download image
    private void downloadImageGallery(){
        BitmapDrawable bitmapDrawable = (BitmapDrawable) qr_view.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();

        FileOutputStream outputStream = null;
        File file = Environment.getExternalStorageDirectory();
        File dir = new File(file.getAbsolutePath() + "/Recordly");
        dir.mkdir();

        String filename = String.format("%d.png",System.currentTimeMillis());
        File outFile = new File(dir,filename);
        try{
            outputStream = new FileOutputStream(outFile);
        }catch (Exception e){
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.PNG, 100,outputStream);
        try{
            outputStream.flush();
            Toast.makeText(QR_show.this, "QR Code Saved!", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
        }

        try{
            outputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void sendMail() {
        AccountNotification accountNotification = new AccountNotification();

        String e_subject = "QR Code";
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
            String htmlText = "<H1>Recordly</H1> " +
                    "<h3>Greetings!</h3> <b>" + s_fname + " " + s_lname + "</b>,<br> "+
                    "<br> See attachments for your unique QR Code, <b> Thank you! </b>"; //EMAIL MESSAGE HAHA ANG HABA
            messageBodyPart.setContent(htmlText, "text/html");
            multipart.addBodyPart(messageBodyPart);

            Bitmap bitmap = ((BitmapDrawable) qr_view.getDrawable()).getBitmap();//get the drawable file of QR gen

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

            new QR_show.SendMail().execute(message);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //send email class
    private class SendMail extends AsyncTask<Message, String, String> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(QR_show.this,
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
                AlertDialog.Builder builder = new AlertDialog.Builder(QR_show.this);
                builder.setCancelable(false);
                builder.setTitle("Success!");
                builder.setMessage("Your information has been sent to your email");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });
                builder.show();
            } else {
                Toast.makeText(QR_show.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }

}