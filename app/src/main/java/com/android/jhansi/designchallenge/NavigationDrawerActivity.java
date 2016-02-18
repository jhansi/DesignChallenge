/*
 * Copyright (c) 2016 Jhansi Tavva. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.jhansi.designchallenge;

import android.content.Context;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.model.CameraPosition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NavigationDrawerActivity extends AppCompatActivity implements AdapterView.OnItemClickListener
{


    private DrawerLayout drawerLayout;
    private ListView navList;
    private ArrayAdapter<String> adapter;

    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;
    private int FragmentToBeLoaded;


    private Tracker mTracker;

    public static final String TAG = NavigationDrawerActivity.class.getSimpleName();

    private CameraPosition cp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);

        CommonUtil.initialiseSharedPreference(this);

        // Obtain the shared Tracker instance.
        AnalyticsApplication application =  ((AnalyticsApplication) getApplication());
        mTracker = application.getDefaultTracker();


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
        adapter = new ArrayAdapter<String>(this,R.layout.layout_listitem,navArray);
        navList.setAdapter(adapter);
        navList.setOnItemClickListener(this);

        fragmentManager = getSupportFragmentManager();

        if (!isNetworkConnectionAvailable()) {
            showCustomToast(R.drawable.no_internet_connection_banner_r);
        }
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
                break;
            case CommonUtil.AboutFragment:
                AboutFragment aboutFragment = new AboutFragment();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentholder, aboutFragment);
                fragmentTransaction.commit();
                break;

            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        drawerLayout.closeDrawers();
        switch(position){

            case 0:
                drawerLayout.closeDrawers();
                break;
            case 1:
                if(CommonUtil.isPreferencesSet(this)){
                    loadSelection(CommonUtil.MapsFragment);
                }else{
                    loadSelection(CommonUtil.GreetingFragment);
                }
                break;
            case 2:
                loadSelection(CommonUtil.AboutFragment);
                break;
            case 3:
                CommonUtil.deleteUserData();
                Intent intent = new Intent(this,SplashScreen.class);
                startActivity(intent);
                finish();
                break;
            default:
                drawerLayout.closeDrawers();
                break;
        }

    }

    public void onClickMenu(View view) {

        if(CommonUtil.isPreferencesSet(this)) {
            Log.i(TAG, "preference is set");
            Log.i(TAG, "(adapter.getCount(): "+ adapter.getCount());
           if(adapter.getCount() == 3){
               Log.i(TAG, "(adapter.getCount(): "+ adapter.getCount());
               adapter.insert(getResources().getString(R.string.logout), 3);
               adapter.notifyDataSetChanged();
           }

        }

        drawerLayout.openDrawer(GravityCompat.START);
        hideKeypad(view);
    }

    public void onClickGoButton(View view) {
        hideKeypad(view);

        EditText emailEditText = (EditText) findViewById(R.id.emailEditText);
        EditText fnameEditText = (EditText) findViewById(R.id.fNameEditText);
        EditText lnameEditText = (EditText) findViewById(R.id.lNameEditText);

        String email = emailEditText.getText().toString();
        String fname = fnameEditText.getText().toString();
        String lname = lnameEditText.getText().toString();

        if( ! isValidEmail(email)){
            showCustomToast(R.drawable.invalid_email_banner_r);
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

    @Override
    protected void onResume() {
        super.onResume();

       if(Intent.ACTION_VIEW.equals(getIntent().getAction())) {
           Log.i(TAG, "App Launch from Link: " + TAG);
           mTracker.setScreenName(TAG);
           mTracker.send(new HitBuilders.ScreenViewBuilder().build());
           mTracker.send(new HitBuilders.EventBuilder()
                   .setCategory(getString(R.string.category_email))
                   .setAction(getString(R.string.action_email))
                   .setLabel(getString(R.string.label_email))
                   .build());
       }
    }


    protected void hideKeypad(View view){
        View viewKeyBoard = this.getCurrentFocus();
        if (viewKeyBoard != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
