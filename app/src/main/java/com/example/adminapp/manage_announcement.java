package com.example.adminapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class manage_announcement extends AppCompatActivity {

    private RecyclerView announcementRecycler;
    private FloatingActionButton floatingBtn;
    private LinearLayout noData;
    private ProgressBar progressBar;
    private ArrayList<AnnouncementInfo> l1;
    private announcementAdapter adapter;

    private DatabaseReference databaseReference, dbRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_announcement);

        floatingBtn = findViewById(R.id.floatingBtn);
        announcementRecycler = findViewById(R.id.announcementRecycler);
        noData = findViewById(R.id.noData);
        progressBar = findViewById(R.id.progressBar);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Notice Data");

        announcementRecycler();

        floatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(manage_announcement.this, notice_upload.class);
                startActivity(intent);
            }
        });
    }

    private void announcementRecycler() {

        dbRef = databaseReference;

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                l1 = new ArrayList<>();
                if(!dataSnapshot.exists()){
                    progressBar.setVisibility(View.GONE);
                    noData.setVisibility(View.VISIBLE);
                    announcementRecycler.setVisibility(View.GONE);
                }else{
                    progressBar.setVisibility(View.GONE);
                    noData.setVisibility(View.GONE);
                    announcementRecycler.setVisibility(View.VISIBLE);
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                        AnnouncementInfo data = snapshot.getValue(AnnouncementInfo.class);
                        l1.add(data);
                    }
                    announcementRecycler.setHasFixedSize(true);
                    announcementRecycler.setLayoutManager(new LinearLayoutManager(manage_announcement.this,LinearLayoutManager.VERTICAL, false));
                    adapter = new announcementAdapter(manage_announcement.this, l1);
                    announcementRecycler.setAdapter(adapter);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(manage_announcement.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}