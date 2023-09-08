package com.example.adminapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class loginScreen extends AppCompatActivity {

    private EditText adminId, adminPass;
    private ImageView showPass;
    private RelativeLayout loginBtn;
    private String email, pass;
    private SharedPreferences sharedPreferences;
    private final String SHARED_PREF_NAME = "loginStatus";
    private final String KEY_NAME = "email";
    private final String KEY_PASS = "pass";
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


        this.getSupportActionBar().hide();

        sharedPreferences = this.getSharedPreferences(SHARED_PREF_NAME,MODE_PRIVATE);
        editor = sharedPreferences.edit();

        String name = sharedPreferences.getString(KEY_NAME,null);
        String pass = sharedPreferences.getString(KEY_PASS,null);

        if(name != null  && pass != null){
            openAdminPanel();
        }

        adminId = findViewById(R.id.adminId);
        adminPass = findViewById(R.id.adminPass);
        showPass = findViewById(R.id.showPass);
        loginBtn = findViewById(R.id.loginBtn);

        showPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(adminPass.getInputType() == 144){
                    adminPass.setInputType(129);
                } else {
                    adminPass.setInputType(144);
                }
                adminPass.setSelection(adminPass.getText().length());
            }
        });


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckLoginInfo();
            }
        });
    }

    private void CheckLoginInfo() {
        email = adminId.getText().toString();
        pass = adminPass.getText().toString();

        if(email.isEmpty()){
            adminId.setError("Enter Id");
            adminId.requestFocus();
        } else if(pass.isEmpty()){
            adminPass.setError("Enter Pass");
            adminPass.requestFocus();
        } else if(email.equals("admin123@gmail.com") && pass.equals("kalana0k")){
            editor.putString(KEY_NAME,email);
            editor.putString(KEY_PASS,pass);
            editor.apply();
            openAdminPanel();
        } else {
            Toast.makeText(this, "Incorrect ID or PASSWORD", Toast.LENGTH_SHORT).show();
        }
    }

    private void openAdminPanel() {
        startActivity(new Intent(loginScreen.this,MainActivity.class));
        finish();
    }
}