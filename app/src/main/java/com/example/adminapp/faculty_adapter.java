package com.example.adminapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class faculty_adapter extends RecyclerView.Adapter<faculty_adapter.facultyViewAdapter> {

    private List<FacultyData> list;
    private Context context;
    private  String category;

    public faculty_adapter(List<FacultyData> list, Context context, String category) {
        this.list = list;
        this.context = context;
        this.category = category;
    }

    @NonNull
    @Override
    public faculty_adapter.facultyViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.faculty_list, parent, false);
        return new facultyViewAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull faculty_adapter.facultyViewAdapter holder, int position) {
        FacultyData item = list.get(position);
        holder.name.setText(item.getName());
        holder.post.setText(item.getPost());
        holder.email.setText(item.getEmail());

        try {
            Picasso.get().load(item.getImage()).into(holder.imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }


        holder.imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, update_faculty_info.class);
                intent.putExtra("name",item.getName());
                intent.putExtra("post",item.getPost());
                intent.putExtra("email",item.getEmail());
                intent.putExtra("image",item.getImage());
                intent.putExtra("key",item.getKey());
                intent.putExtra("category",category);
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class facultyViewAdapter extends RecyclerView.ViewHolder {

        private TextView name, post, email;
        private CircleImageView imageView,imageBtn;

        public facultyViewAdapter(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.personName);
            post = itemView.findViewById(R.id.personPost);
            email = itemView.findViewById(R.id.personEmail);
            imageView = itemView.findViewById(R.id.personPhoto);
            imageBtn = itemView.findViewById(R.id.updateBtn);
        }
    }
}
