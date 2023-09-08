/**
 * The MainActivity class is an implementation of an Android activity that serves as the main screen of
 * an admin app, with clickable card views that lead to different functionalities and a logout button.
 */
package com.example.adminapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Nullable
    CardView cardStudy, cardNotice, cardImg, cardFaculty, cardRequests;
    private SharedPreferences sharedPreferences;
    private final String SHARED_PREF_NAME = "loginStatus";
    private final String KEY_NAME = "email";
    private final String KEY_PASS = "pass";
    private SharedPreferences.Editor editor;
    private ImageView logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        sharedPreferences = this.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        String name = sharedPreferences.getString(KEY_NAME, null);
        String pass = sharedPreferences.getString(KEY_PASS, null);

        if (name == null && pass == null) {
            openLoginScreen();
        }

        cardStudy = findViewById(R.id.cardStudy);
        cardNotice = findViewById(R.id.cardNotice);
        cardImg = findViewById(R.id.cardImg);
        cardFaculty = findViewById(R.id.cardFaculty);
        cardRequests = findViewById(R.id.cardRequets);
        logoutBtn = findViewById(R.id.logoutBtn);

        cardStudy.setOnClickListener(this);
        cardNotice.setOnClickListener(this);
        cardImg.setOnClickListener(this);
        cardFaculty.setOnClickListener(this);
        cardRequests.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);
    }

    private void openLoginScreen() {
        editor.clear();
        editor.commit();
        startActivity(new Intent(MainActivity.this, loginScreen.class));
        finish();
    }

    @Override
    public void onClick(View view) {
        Intent i;
        switch (view.getId()) {

            case R.id.cardStudy:
                i = new Intent(MainActivity.this, books_upload.class);
                startActivity(i);
                break;

            case R.id.cardNotice:
                i = new Intent(MainActivity.this, manage_announcement.class);
                startActivity(i);
                break;

            case R.id.cardImg:
                i = new Intent(MainActivity.this, photo_upload.class);
                startActivity(i);
                break;

            case R.id.cardFaculty:
                i = new Intent(MainActivity.this, update_faculties.class);
                startActivity(i);
                break;

            case R.id.logoutBtn:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Alert");
                builder.setMessage("Are you sure you want to logout ?");
                builder.setCancelable(true);

                builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        openLoginScreen();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                AlertDialog dialog = null;
                try {
                    dialog = builder.create();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (dialog != null)
                    dialog.show();
                break;

            case R.id.cardRequets:
                startActivity(new Intent(getApplicationContext(), requests_inbox.class));
                break;
        }
    }
}