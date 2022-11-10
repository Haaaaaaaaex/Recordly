package com.example.recordly;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {

    SharedPreferences userSession;
    SharedPreferences.Editor editor;
    Context context;
    String SESSION_KEY = "session_user";
    public static final String IS_LOGIN = "IsLoggedIn";

    public static final String KEY_FIRSTNAME = "firstname";
    public static final String KEY_LASTNAME = "lastname";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_PHONE_NUMBER = "phone";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_CITY = "city";
    public static final String KEY_ENCRYPT = "encrypt";



    public SessionManager (Context _context){
        context = _context;
        userSession = context.getSharedPreferences("userLoginSession",Context.MODE_PRIVATE);
        editor = userSession.edit();
    }

    public void createLoginSession(String firstname, String lastname, String email, String phone, String address,String city, String encrypt, String password){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_FIRSTNAME,firstname);
        editor.putString(KEY_LASTNAME, lastname);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PHONE_NUMBER, phone);
        editor.putString(KEY_ADDRESS, address);
        editor.putString(KEY_CITY, city);
        editor.putString(KEY_ENCRYPT, encrypt);
        editor.putString(KEY_PASSWORD, password);

        editor.commit();
    }




    public HashMap<String, String> getUserDetailsFromSession(){
        HashMap<String, String> userData = new HashMap<String,String>();

        userData.put(KEY_FIRSTNAME, userSession.getString(KEY_FIRSTNAME, null));
        userData.put(KEY_LASTNAME, userSession.getString(KEY_LASTNAME, null));
        userData.put(KEY_EMAIL, userSession.getString(KEY_EMAIL, null));
        userData.put(KEY_PHONE_NUMBER, userSession.getString(KEY_PHONE_NUMBER, null));
        userData.put(KEY_ADDRESS, userSession.getString(KEY_ADDRESS, null));
        userData.put(KEY_CITY, userSession.getString(KEY_CITY, null));
        userData.put(KEY_ENCRYPT, userSession.getString(KEY_ENCRYPT, null));
        userData.put(KEY_PASSWORD, userSession.getString(KEY_PASSWORD, null));


        return userData;
    }

    public boolean checkLogin(){
        if(userSession.getBoolean(IS_LOGIN, true)){
            return true;
        }else{
            return false;
        }
    }

    public void saveSession(){
        int id = 1;
        editor.putInt(SESSION_KEY, id).commit();
    }

    public int getSession(){
        return userSession.getInt(SESSION_KEY, -1);
    }



    public void LogoutUserFromSession(){
        editor.putInt(SESSION_KEY, -1);
        editor.clear();
        editor.commit();
    }
}

