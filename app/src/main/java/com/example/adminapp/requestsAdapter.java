package com.example.adminapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class requestsAdapter extends RecyclerView.Adapter<requestsAdapter.requestViewAdapter> {

    private ArrayList<requestsData> list;
    private Context context;
    private String name, email, subject, message, status;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("requests");

    public requestsAdapter(ArrayList<requestsData> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public requestsAdapter.requestViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.requests_ui,parent,false);
        return new requestViewAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull requestsAdapter.requestViewAdapter holder, int position) {
        requestsData item = list.get(position);

        final String primaryKey = item.getKey();

        status = item.getStatus();

        holder.form_name.setText(item.getName());
        holder.form_email.setText(item.getEmail());
        holder.form_subject.setText(item.getSubject());
        holder.form_message.setText(item.getMessage());


        if (status.equals("not approved")){
            holder.approvedBtn.setVisibility(View.GONE);
            holder.approveBtn.setVisibility(View.VISIBLE);
        } else {
            holder.approveBtn.setVisibility(View.GONE);
            holder.approvedBtn.setVisibility(View.VISIBLE);
        }


        holder.approveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    status = "approved";

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("name",item.getName());
                    hashMap.put("email",item.getEmail());
                    hashMap.put("subject",item.getSubject());
                    hashMap.put("message",item.getMessage());
                    hashMap.put("status",status);
                    hashMap.put("key",item.getKey());

                    databaseReference.child(primaryKey).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            String sender = "bmu.cms@gmail.com";
                            String senderPass = "newedfrrdkaerjgh";
                            String recipient = item.getEmail();
                            String title = "Acknowledgement for your received Request";
                            String body = "Dear "+item.getName()+ ",\n\n" +
                                    "We have received your request from BMU application.\n" +
                                    "Your request is in process\nWe will contact you as soon as possible OR\n" +
                                    "We will respond to your request/application as soon as possible.\n\n" +
                                    "Sincerely,\n" +
                                    "Management Committee";
                            sendEmail(sender,senderPass,recipient,title,body);
                            Toast.makeText(context, "Mail sent", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class requestViewAdapter extends RecyclerView.ViewHolder {
        private TextView form_name, form_email, form_subject, form_message;
        private Button approveBtn ,approvedBtn;
        public requestViewAdapter(@NonNull View itemView) {
            super(itemView);
            form_name = itemView.findViewById(R.id.form_name);
            form_email = itemView.findViewById(R.id.form_email);
            form_subject = itemView.findViewById(R.id.form_subject);
            form_message = itemView.findViewById(R.id.form_message);

            approveBtn = itemView.findViewById(R.id.approveBtn);
            approvedBtn = itemView.findViewById(R.id.approvedBtn);
        }
    }

    private void sendEmail(final String Sender,final String Password,final String Receiver,final String Title,final String Message)
    {

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    GMailSender sender = new GMailSender(Sender,Password);
                    sender.sendMail(Title, "<b>"+Message+"</b>", Sender, Receiver);

                } catch (Exception e) {
                    Log.e("SendMail", e.getMessage(), e);
                }
            }

        }).start();
    }
}
