package com.example.recordly;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Calendar;
import java.util.Date;


public class LoginTabFragment extends Fragment {
    FirebaseFirestore fireStore;
    FirebaseAuth fAuth;

    EditText email;
    EditText password1;
    TextInputLayout password;
    TextView qr_text, Banner,forgot;
    ImageView QR;

    String timeMoment;
    String s_firstName, s_lastName, s_email, s_add, s_city, s_phone, s_encrypt, s_password;

    TextView lst;

    float v = 0;

    Dialog dialog;
    ImageView done1, bg;
    AnimatedVectorDrawableCompat avd;
    AnimatedVectorDrawable avd2;
    boolean keep;

    @Override
    public void onStart() {
        super.onStart();
        SessionManager sessionManager = new SessionManager(getActivity());
        int checkLog = sessionManager.getSession();

        if (checkLog!=-1) {
            startActivity(new Intent(getActivity(), MainActivity.class));
        } else {

        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.login_tab_fragment, container, false);

        email = (EditText) view.findViewById(R.id.email);
        password1 = (EditText) view.findViewById(R.id.pass1);
        password = (TextInputLayout) view.findViewById(R.id.password);
        qr_text = (TextView) view.findViewById(R.id.loginForgot);
        QR = (ImageView) view.findViewById(R.id.QR_LOG);

        forgot = (TextView) view.findViewById(R.id.loginForgot);
        SpannableString underline = new SpannableString("Forgot Password?");
        underline.setSpan(new UnderlineSpan(), 0, underline.length(), 0);
        forgot.setText(underline);


        Button login_button = (Button) view.findViewById(R.id.default_login_button);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uemail = email.getText().toString();
                String pass = password1.getText().toString();


                if (uemail.equals("") || pass.equals(""))
                    Toast.makeText(getActivity(), "All fields are required", Toast.LENGTH_SHORT).show();
                else {
                    fireStore = FirebaseFirestore.getInstance();

                    DocumentReference profile = fireStore.collection("users").document(uemail);
                    profile.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                profile.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                        SessionManager sessionManager = new SessionManager(getActivity());
                                        s_firstName = value.getString("First name ");
                                        s_lastName = value.getString("Last name ");
                                        s_email = value.getString("Email ");
                                        s_add = value.getString("Address ");
                                        s_phone = value.getString("Phone number ");
                                        s_city = value.getString("City ");
                                        s_encrypt = value.getString("Encryption Password ");
                                        s_password = value.getString("Password ");
                                        if(s_password.equals(pass)) {
                                            sessionManager.createLoginSession(s_firstName, s_lastName, s_email, s_phone, s_add, s_city, s_encrypt, s_password);
                                            sessionManager.saveSession();
                                            time();
                                            Intent i = new Intent(getActivity(), success_animation.class);
                                            startActivity(i);
                                            ((Activity) getActivity()).overridePendingTransition(0, 0);
                                        }else{
                                            Toast.makeText(getActivity(), "Invalid email or password", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }else{
                                Toast.makeText(getActivity(), "Invalid email or password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }
            }
        });

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), userForgotPassword.class);
                startActivity(intent);
            }
        });

        email.setTranslationX(300);
        password.setTranslationX(300);
        qr_text.setTranslationX(300);
        QR.setTranslationX(300);
        login_button.setTranslationX(300);

        email.setAlpha(v);
        password.setAlpha(v);
        qr_text.setAlpha(v);
        QR.setAlpha(v);
        login_button.setAlpha(v);

        email.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();
        password.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        qr_text.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(700).start();
        QR.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(700).start();
        login_button.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(900).start();


        return view;


    }

    public void time() {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        if (timeOfDay >= 0 && timeOfDay < 12) {
            Toast.makeText(getActivity(), "Good Morning, " + s_firstName + "!", Toast.LENGTH_SHORT).show();
        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            Toast.makeText(getActivity(), "Good Afternoon, " + s_firstName + "!", Toast.LENGTH_SHORT).show();
        } else if (timeOfDay >= 16 && timeOfDay < 24) {
            Toast.makeText(getActivity(), "Good Evening, " + s_firstName + "!", Toast.LENGTH_SHORT).show();
        }

    }

}
