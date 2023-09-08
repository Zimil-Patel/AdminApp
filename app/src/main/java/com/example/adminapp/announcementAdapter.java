package com.example.adminapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class announcementAdapter extends RecyclerView.Adapter<announcementAdapter.announcementViewAdapter> {

    private Context context;
    private ArrayList<AnnouncementInfo> list;
    private String msg = "";

    public announcementAdapter(Context context, ArrayList<AnnouncementInfo> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public announcementAdapter.announcementViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notice_layout,parent,false);
        return new announcementViewAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull announcementAdapter.announcementViewAdapter holder, @SuppressLint("RecyclerView") int position) {
        AnnouncementInfo currentData = list.get(position);

        holder.photoCard.setVisibility(View.GONE);

        holder.announcementTitle.setText(currentData.getTitle());

        try {
            if (currentData.getImage() != null){
                Picasso.get().load(currentData.getImage()).into(holder.announcementPhoto);
                holder.photoCard.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Alert!");
                builder.setMessage("Are you sure you want to delete announcement ?");
                builder.setCancelable(true);
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Notice Data");
                        databaseReference.child(currentData.getKey()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(context, "Successfully Deleted", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        notifyItemRemoved(position);
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

                if(dialog != null)
                    dialog.show();


            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class announcementViewAdapter extends RecyclerView.ViewHolder {
        private ImageView announcementPhoto;
        private Button deleteBtn;
        private TextView announcementTitle;
        private CardView photoCard;
        public announcementViewAdapter(@NonNull View itemView) {
            super(itemView);
            deleteBtn = itemView.findViewById(R.id.announcementDelete);
            announcementPhoto = itemView.findViewById(R.id.announcementPhoto);
            announcementTitle = itemView.findViewById(R.id.announcementTitle);
            photoCard = itemView.findViewById(R.id.photoCard);

        }
    }
}
