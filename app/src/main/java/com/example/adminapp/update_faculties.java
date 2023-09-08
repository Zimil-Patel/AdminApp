package com.example.adminapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class update_faculties extends AppCompatActivity {

    FloatingActionButton floatingBtn;
    private RecyclerView bcaDepartment, mcaDepartment, imcaDepartment;
    private CardView bcaNoData, mcaNoData, imcaNoData;
    private ArrayList<FacultyData> l1,l2,l3;
    private DatabaseReference databaseReference, dbRef;
    private faculty_adapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_faculties);

        floatingBtn = findViewById(R.id.floatingBtn);

        bcaDepartment = findViewById(R.id.bcaDepartment);
        mcaDepartment = findViewById(R.id.mcaDepartment);
        imcaDepartment = findViewById(R.id.imcaDepartment);

        bcaNoData = findViewById(R.id.bcaNoData);
        mcaNoData = findViewById(R.id.mcaNoData);
        imcaNoData = findViewById(R.id.imcaNoData);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Faculties Detail");

        bcaDepartmentList();
        mcaDepartmentList();
        imcaDepartmentList();

    floatingBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(update_faculties.this,add_faculty.class));
        }
    });
    }

    private void bcaDepartmentList() {
        dbRef = databaseReference.child("BCA");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                l1 = new ArrayList<>();

                if(!dataSnapshot.exists()){
                    bcaNoData.setVisibility(View.VISIBLE);
                    bcaDepartment.setVisibility(View.GONE);
                }else{
                    bcaNoData.setVisibility(View.GONE);
                    bcaDepartment.setVisibility(View.VISIBLE);
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                        FacultyData data = snapshot.getValue(FacultyData.class);
                        l1.add(data);
                    }
                    bcaDepartment.setHasFixedSize(true);
                    bcaDepartment.setLayoutManager(new LinearLayoutManager(update_faculties.this,LinearLayoutManager.VERTICAL, false));
                    adapter = new faculty_adapter(l1, update_faculties.this, "BCA");
                    bcaDepartment.setAdapter(adapter);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(update_faculties.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mcaDepartmentList() {
        dbRef = databaseReference.child("MCA");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                l2 = new ArrayList<>();
                if(!dataSnapshot.exists()){
                    mcaNoData.setVisibility(View.VISIBLE);
                    mcaDepartment.setVisibility(View.GONE);
                }else{
                    mcaNoData.setVisibility(View.GONE);
                    mcaDepartment.setVisibility(View.VISIBLE);
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                        FacultyData data = snapshot.getValue(FacultyData.class);
                        l2.add(data);
                    }
                    mcaDepartment.setHasFixedSize(true);
                    mcaDepartment.setLayoutManager(new LinearLayoutManager(update_faculties.this,LinearLayoutManager.VERTICAL, false));
                    adapter = new faculty_adapter(l2, update_faculties.this, "MCA");
                    mcaDepartment.setAdapter(adapter);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(update_faculties.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void imcaDepartmentList() {
        dbRef = databaseReference.child("IMCA");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                l3 = new ArrayList<>();
                if(!dataSnapshot.exists()){
                    imcaNoData.setVisibility(View.VISIBLE);
                    imcaDepartment.setVisibility(View.GONE);
                }else{
                    imcaNoData.setVisibility(View.GONE);
                    imcaDepartment.setVisibility(View.VISIBLE);
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                        FacultyData data = snapshot.getValue(FacultyData.class);
                        l3.add(data);
                    }
                    imcaDepartment.setHasFixedSize(true);
                    imcaDepartment.setLayoutManager(new LinearLayoutManager(update_faculties.this,LinearLayoutManager.VERTICAL, false));
                    adapter = new faculty_adapter(l3, update_faculties.this, "IMCA");
                    imcaDepartment.setAdapter(adapter);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(update_faculties.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}