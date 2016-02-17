package com.android.jhansi.designchallenge;

import android.content.Context;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NavigationDrawerActivity extends AppCompatActivity implements AdapterView.OnItemClickListener
{


    private DrawerLayout drawerLayout;
    private ListView navList;


    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;
    private int FragmentToBeLoaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);

        CommonUtil.initialiseSharedPreference(this);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawerlayout);
        navList = (ListView)findViewById(R.id.navlist);

        String[] myResArray;

        if(CommonUtil.isPreferencesSet(this)) {
            FragmentToBeLoaded = CommonUtil.MapsFragment;
            myResArray = getResources().getStringArray(R.array.nav_menu_logout);
        }else{
            FragmentToBeLoaded = CommonUtil.GreetingFragment;
            myResArray = getResources().getStringArray(R.array.nav_menu);
        }

        List<String> myResArrayList = Arrays.asList(myResArray);
        ArrayList<String> navArray = new ArrayList<String>(myResArrayList);


        navList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.layout_listitem,navArray);
        navList.setAdapter(adapter);
        navList.setOnItemClickListener(this);

        fragmentManager = getSupportFragmentManager();

       // FragmentToBeLoaded = 1; // TODO remove it ony for testing


        loadSelection(FragmentToBeLoaded);
    }

    private void loadSelection(int i) {
        navList.setItemChecked(i,true);
        switch (i) {
            case CommonUtil.GreetingFragment:
                GreetingFragment greetingFragment = new GreetingFragment();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentholder, greetingFragment);
                fragmentTransaction.commit();
                break;
            case CommonUtil.MapsFragment:
                MapFragment mapFragment = new MapFragment();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentholder,mapFragment);
                fragmentTransaction.commit();
            default:
                break;
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        switch(position){

            case 0:
                drawerLayout.closeDrawers();
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            default:
                drawerLayout.closeDrawers();
                break;
        }

        //loadSelection(position);

    }

    public void onClickMenu(View view) {

        String[] myResArray;
        if(CommonUtil.isPreferencesSet(this)) {
            FragmentToBeLoaded = CommonUtil.MapsFragment;
            myResArray = getResources().getStringArray(R.array.nav_menu_logout);
        }else{
            FragmentToBeLoaded = CommonUtil.GreetingFragment;
            myResArray = getResources().getStringArray(R.array.nav_menu);
        }

        List<String> myResArrayList = Arrays.asList(myResArray);
        ArrayList<String> navArray = new ArrayList<String>(myResArrayList);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.layout_listitem,navArray);
        navList.setAdapter(adapter);

        drawerLayout.openDrawer(GravityCompat.START);
        View viewKeyBoard = this.getCurrentFocus();
        if (viewKeyBoard != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void onClickGoButton(View view) {

        EditText emailEditText = (EditText) findViewById(R.id.emailEditText);
        EditText fnameEditText = (EditText) findViewById(R.id.fNameEditText);
        EditText lnameEditText = (EditText) findViewById(R.id.lNameEditText);

        String email = emailEditText.getText().toString();
        String fname = fnameEditText.getText().toString();
        String lname = lnameEditText.getText().toString();

        if( ! isValidEmail(email)){

            showCustomToast(R.drawable.invalid_email_banner_r);
//        }else if (!isNetworkConnectionAvailable()){
//            showCustomToast(R.drawable.no_internet_connection_banner_r);
        }else{
            CommonUtil.saveToPreferences(email,fname,lname);

            loadSelection(CommonUtil.MapsFragment);
        }

    }

    private boolean isNetworkConnectionAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return (activeNetworkInfo != null && activeNetworkInfo.isConnected());
    }

    private void showCustomToast(int resId){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.custom_toast_layout));

        ImageView image = (ImageView) layout.findViewById(R.id.image);
        image.setImageResource(resId);


        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.TOP, 0, -1);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }
    public final static boolean isValidEmail(CharSequence email) {
        if (email == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }

}
