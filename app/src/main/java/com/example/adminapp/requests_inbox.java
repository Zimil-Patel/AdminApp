package com.example.adminapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class requests_inbox extends AppCompatActivity {

    private ProgressBar progressBar;
    private LinearLayout nullPage;
    private RecyclerView requestRecycler;
    private DatabaseReference databaseReference;
    private requestsAdapter adapter;
    private ArrayList<requestsData> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests_inbox);

        progressBar = findViewById(R.id.progressBarRequest);
        nullPage = findViewById(R.id.nullPage);
        requestRecycler = findViewById(R.id.requestRecycler);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("requests");

        fetchRequests();
    }

    private void fetchRequests() {

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list = new ArrayList<>();
                if (!snapshot.exists()){
                    progressBar.setVisibility(View.GONE);
                    requestRecycler.setVisibility(View.GONE);
                    nullPage.setVisibility(View.VISIBLE);
                }else {
                    progressBar.setVisibility(View.GONE);
                    requestRecycler.setVisibility(View.VISIBLE);
                    nullPage.setVisibility(View.GONE);
                    for (DataSnapshot snapshot1 : snapshot.getChildren()){
                        requestsData data = snapshot1.getValue(requestsData.class);
                        list.add(data);
                    }
                    requestRecycler.setHasFixedSize(true);
                    requestRecycler.setLayoutManager(new LinearLayoutManager(requests_inbox.this,LinearLayoutManager.VERTICAL,false));
                    adapter = new requestsAdapter(list,requests_inbox.this);
                    requestRecycler.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requests_inbox.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}