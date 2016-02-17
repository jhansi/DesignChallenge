package com.android.jhansi.designchallenge;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by Jhansi
 */
public class CommonUtil {

    public static final int GreetingFragment = 0;
    public static final int MapsFragment = 1;

    public static final int AboutFragment = 3;


    public static final String  preference_file = "com.android.jhansi.PREFERENCE_FILE_KEY";

    public static final String FIRST_NAME_KEY = "firstNameKey";
    public static final String LAST_NAME_KEY = "lastNameKey";
    public static final String EMAIL_KEY = "emailKey";
    public static final int exactNumberOfPreferences = 3;

    public static final String TAG = CommonUtil.class.getSimpleName();

    private static SharedPreferences sharedpreferences;
    private static SharedPreferences.Editor editor;

    public  static Context context;

    public static void initialiseSharedPreference(Context con) {
        context = con;
        sharedpreferences = context.getApplicationContext().getSharedPreferences(CommonUtil.preference_file, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
    }


    public static void saveToPreferences(String email, String fname, String lname){
        editor.putString(EMAIL_KEY, email);
        editor.putString(FIRST_NAME_KEY, fname);
        editor.putString(LAST_NAME_KEY, lname);
        editor.commit();
        editor.apply();
    }

    public static String getFNamefromPreferences() {
        String fName = sharedpreferences.getString(FIRST_NAME_KEY,"");
        return fName;
    }

    public static String getLNamefromPreferences() {
        String lName = sharedpreferences.getString(LAST_NAME_KEY,"");
        return lName;
    }

    public static String getEmailfromPreferences() {
        String email = sharedpreferences.getString(EMAIL_KEY,"");
        return email;
    }

    public static String getUserName(){
        //StringBuilder userName = new StringBuilder("");
        String userName = getFNamefromPreferences() +" "+ getLNamefromPreferences();
        return userName;
    }
    public static boolean isPreferencesSet(Context context){
        int size = sharedpreferences.getAll().size();
        Log.i(TAG, "Size" + size);
        return (size > 0 );
    }

    public static void deleteUserData(){
        editor.clear().commit();
    }
}
