package com.example.adminapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.Reference;

public class photo_upload extends AppCompatActivity {

    private ImageView previewPhoto;
    private Button btnUpload;
    private Spinner category;
    private CardView selPhoto;
    private final int request = 1;
    private Bitmap bitmap = null;
    private String selectedCategory;
    private StorageReference storageReference;
    private DatabaseReference databaseReference, dbRef;
    private ProgressDialog progress;
    String downloadUrl = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_upload);
        progress = new ProgressDialog(this);
        storageReference = FirebaseStorage.getInstance().getReference().child("College Gallery");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("College Gallery");
        previewPhoto = findViewById(R.id.previewPhoto);
        btnUpload = findViewById(R.id.uploadPhoto);
        selPhoto = findViewById(R.id.cardSelectImg);


        selPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bitmap == null){
                    Toast.makeText(photo_upload.this, "Please Select Photo!", Toast.LENGTH_SHORT).show();
                }else{
                    upload();
                }
            }
        });
    }

    private void upload() {
        progress.setMessage("Uploading...");
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
                            dataUplaod(downloadUrl);
                        }
                    });
                }else{
                    progress.dismiss();
                    Toast.makeText(photo_upload.this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void dataUplaod(String url) {

        final String primaryKey = databaseReference.push().getKey();
        GalleryData galleryData = new GalleryData(url);
        databaseReference.child(primaryKey).setValue(galleryData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progress.dismiss();
                Toast.makeText(photo_upload.this, "Photo Uploaded", Toast.LENGTH_SHORT).show();
                bitmap = null;
                previewPhoto.setImageBitmap(bitmap);
                Intent intent = new Intent(photo_upload.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progress.dismiss();
                bitmap = null;
                previewPhoto.setImageBitmap(bitmap);
                Toast.makeText(photo_upload.this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
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

            previewPhoto.setImageBitmap(bitmap);
        }
    }
}