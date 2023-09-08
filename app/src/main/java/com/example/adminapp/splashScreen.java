package com.example.adminapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class splashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        this.getSupportActionBar().hide();


        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(splashScreen.this,loginScreen.class));
                finish();
            }
        },2000);
    }
}