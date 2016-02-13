package com.android.jhansi.designchallenge;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

    startLoading();
}


    //Start signup activity and finish splash activity
    public void openMainActivity() {
        Intent intent = new Intent(SplashScreen.this,SignUpActivity.class);
        startActivity(intent);
        finish();
    }
    //Start thread which will wait for 3 secs and call openMainActivity
    private void startLoading(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                }
                openMainActivity();
            }
        }).start();
    }


}
