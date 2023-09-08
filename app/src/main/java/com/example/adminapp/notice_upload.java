package com.example.adminapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ktx.Firebase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class notice_upload extends AppCompatActivity {

    private CardView cardSelImg;
    private ImageView previewImg;
    private final int request = 1;
    private Bitmap bitmap;
    private EditText noticeTitle;
    private Button uploadAnnounce;
    private DatabaseReference reference;
    private StorageReference storageReference;
    String downloadUrl = "";
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_upload);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading Announcement...");

        reference = FirebaseDatabase.getInstance().getReference().child("Notice Data");
        storageReference = FirebaseStorage.getInstance().getReference().child("Notice Data");
        noticeTitle = findViewById(R.id.announceTitle);
        uploadAnnounce = findViewById(R.id.uploadAnnounce);
        cardSelImg = findViewById(R.id.cardSelectImg);
        previewImg = findViewById(R.id.previewImg);

        cardSelImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        uploadAnnounce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(noticeTitle.getText().toString().isEmpty())
                {
                    noticeTitle.setError("Empty");
                    noticeTitle.requestFocus();
                }else if(bitmap == null){
                    progressDialog.show();
                    dataUpload();
                }else {
                    imageUpload();
                }
            }
        });
    }

    private void imageUpload() {
        progressDialog.show();
        progressDialog.setCancelable(false);
        ByteArrayOutputStream img = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,img);
        byte[] finalImage = img.toByteArray();
        final StorageReference filePath;
        filePath = storageReference.child(finalImage+"jpg");
        final UploadTask uploadTask = filePath.putBytes(finalImage);

        uploadTask.addOnCompleteListener(notice_upload.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadUrl = String.valueOf(uri);
                                    dataUpload();
                                }
                            });
                        }
                    });
                } else{
                    progressDialog.dismiss();
                    Toast.makeText(notice_upload.this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void dataUpload() {

        final String primaryKey = reference.push().getKey();

        String title = noticeTitle.getText().toString();

        Calendar calDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yy");
        String date = currentDate.format(calDate.getTime());

        Calendar calTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        String time = currentTime.format(calTime.getTime());

        AnnouncementInfo announcementInfo = new AnnouncementInfo(title, downloadUrl, date, time, primaryKey);

        reference.child(primaryKey).setValue(announcementInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressDialog.dismiss();
                noticeTitle.setText("");
                bitmap = null;
                previewImg.setImageBitmap(bitmap);
                Intent intent = new Intent(notice_upload.this,manage_announcement.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                Toast.makeText(notice_upload.this, "Notice Uploaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(notice_upload.this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openGallery() {
        Intent pickImg = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickImg,request);
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
            previewImg.setImageBitmap(bitmap);
        }
    }
}