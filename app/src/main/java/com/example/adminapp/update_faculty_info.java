package com.example.adminapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class update_faculty_info extends AppCompatActivity {

    private ImageView updatedPhoto;
    private CardView changePhoto;
    private EditText updateName, updateEmail, updatePost;
    private Button update, delete;

    private String name, email, post, image;
    private Bitmap bitmap = null;
    private final int request = 1;
    private String downloadUrl = "", primaryKey, category;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_faculty_info);

        progress = new ProgressDialog(this);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Faculties Detail");
        storageReference = FirebaseStorage.getInstance().getReference().child("Faculties Photos");

        updatedPhoto = findViewById(R.id.updatedPhoto);
        changePhoto = findViewById(R.id.changePhoto);
        updateName = findViewById(R.id.updateName);
        updateEmail = findViewById(R.id.updateEmail);
        updatePost = findViewById(R.id.updatePost);
        update = findViewById(R.id.updateFaculty);
        delete = findViewById(R.id.deleteFaculty);

        name = getIntent().getStringExtra("name");
        post = getIntent().getStringExtra("post");
        email = getIntent().getStringExtra("email");
        image = getIntent().getStringExtra("image");
        primaryKey = getIntent().getStringExtra("key");
        category = getIntent().getStringExtra("category");

        try {
            Picasso.get().load(image).into(updatedPhoto);
        } catch (Exception e) {
            e.printStackTrace();
        }

        updateName.setText(name);
        updatePost.setText(post);
        updateEmail.setText(email);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert!");
        builder.setMessage("Are sure you want to delete ?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteData();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        
        changePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = updateName.getText().toString();
                post = updatePost.getText().toString();
                email = updateEmail.getText().toString();

                checkUpdation();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog = null;
                try {
                    dialog = builder.create();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(dialog != null){
                    dialog.show();
                }
            }
        });
    }

    private void deleteData() {
        progress.setTitle("Deleting");
        progress.setMessage("Please Wait");
        progress.show();

        databaseReference.child(category).child(primaryKey).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progress.dismiss();
                Toast.makeText(update_faculty_info.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(update_faculty_info.this,update_faculties.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progress.dismiss();
                Toast.makeText(update_faculty_info.this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUpdation() {

        if(name.isEmpty()){
            updateName.setError("Empty");
            updateName.requestFocus();
        }else if(post.isEmpty()){
            updatePost.setError("Empty");
            updatePost.requestFocus();
        }else if(!(!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches())){
            updateEmail.setError("Invalid");
            updateEmail.requestFocus();
        }else if(bitmap == null){
            updateData(image);
        }else{
            progress.setTitle("Updating");
            progress.setMessage("Please Wait...");
            progress.show();
            progress.setCancelable(false);
            updatePhoto();
        }
    }

    private void updatePhoto() {

        progress.setTitle("Updating");
        progress.setMessage("Please Wait...");
        progress.show();
        progress.setCancelable(false);
        ByteArrayOutputStream img = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,img);
        byte[] finalimg = img.toByteArray();

        final StorageReference filePath = storageReference.child(finalimg+"jpg");

        final UploadTask uploadTask = filePath.putBytes(finalimg);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if(uploadTask.isSuccessful()){
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            downloadUrl = String.valueOf(uri);
                            updateData(downloadUrl);
                        }
                    });
                }else{
                    progress.dismiss();
                    Toast.makeText(update_faculty_info.this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateData(String downloadUrl) {

        HashMap<String,Object> hasMap = new HashMap<>();
        hasMap.put("name",name);
        hasMap.put("post",post);
        hasMap.put("email",email);
        hasMap.put("image",downloadUrl);


        databaseReference.child(category).child(primaryKey).updateChildren(hasMap).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                progress.dismiss();
                Toast.makeText(update_faculty_info.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(update_faculty_info.this,update_faculties.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progress.dismiss();
                Toast.makeText(update_faculty_info.this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openGallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == request && resultCode == RESULT_OK){
            Uri uri = data.getData();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            updatedPhoto.setImageBitmap(bitmap);
        }
    }
}