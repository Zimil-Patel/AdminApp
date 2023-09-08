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
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.regex.Pattern;

public class add_faculty extends AppCompatActivity {

    private CardView selectImage;
    private ImageView facultyPhoto;
    private EditText facultyName,facultyEmail,facultyPost;
    private Spinner facultyCategory;
    private Button addFaculty;
    private Bitmap bitmap = null;
    private DatabaseReference databaseReference, dbRef;
    private StorageReference storageReference;
    private String name,email,post;
    String downloadUrl = "";
    private String category;
    private ProgressDialog progress;

    public static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    //"(?=.*[0-9])" +         //at least 1 digit
                    //"(?=.*[a-z])" +         //at least 1 lower case letter
                    //"(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{4,}" +               //at least 4 characters
                    "$");

    private final int request = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_faculty);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Faculties Detail");
        storageReference = FirebaseStorage.getInstance().getReference().child("Faculties Photos");
        facultyPhoto = findViewById(R.id.facultyPhoto);
        selectImage = findViewById(R.id.selectImage);
        facultyName = findViewById(R.id.facultyName);
        facultyEmail = findViewById(R.id.facultyEmail);
        facultyPost = findViewById(R.id.facultyPost);
        facultyCategory = findViewById(R.id.facultyCategory);
        addFaculty = findViewById(R.id.addFaculty);
        progress = new ProgressDialog(this );
        
        addFaculty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkInfo();
            }
        });

        String[] items = new String[]{"Select Category","BCA","MCA","IMCA"};
        facultyCategory.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,items));

        facultyCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                category = facultyCategory.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
    }

    private void checkInfo() {
        name = facultyName.getText().toString();
        email = facultyEmail.getText().toString();
        post = facultyPost.getText().toString();
        
        if(name.isEmpty()){
            facultyName.setError("Empty");
            facultyName.requestFocus();
        } else if(!(!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
            facultyEmail.setError("Invalid!");
            facultyEmail.requestFocus();
        } else if (post.isEmpty()) {
            facultyPost.setError("Empty");
            facultyPost.requestFocus();
        } else if (category.equals("Select Category")) {
            Toast.makeText(this, "Please Select Category!", Toast.LENGTH_SHORT).show();
        } else if (bitmap == null) {
            progress.setTitle("Please Wait");
            progress.setMessage("Uploading...");
            progress.show();
            progress.setCancelable(false);
            uploadData();
        } else {
            progress.setTitle("Please Wait");
            progress.setMessage("Uploading...");
            progress.show();
            progress.setCancelable(false);
            uploadImage();
        }
    }

    private void uploadImage() {
        progress.show();
        ByteArrayOutputStream img = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,img);
        byte[] finalImage = img.toByteArray();

        final StorageReference path;
        path = storageReference.child(finalImage+"jpg");
        final  UploadTask uploadTask = path.putBytes(finalImage);

        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadUrl = String.valueOf(uri);
                                    uploadData();
                                }
                            });
                        }
                    });
                }else{
                    progress.dismiss();
                    Toast.makeText(add_faculty.this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadData() {
        dbRef = databaseReference.child(category);
        final String primaryKey = dbRef.push().getKey();
        FacultyData facultyData = new FacultyData(name, email, post, downloadUrl, primaryKey);
        dbRef.child(primaryKey).setValue(facultyData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progress.dismiss();
                Toast.makeText(add_faculty.this, "Successfully uploaded", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(add_faculty.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progress.dismiss();
                Toast.makeText(add_faculty.this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
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
            facultyPhoto.setImageBitmap(bitmap);

        }
    }
}