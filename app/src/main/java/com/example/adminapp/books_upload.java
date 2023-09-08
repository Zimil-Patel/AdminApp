package com.example.adminapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

import java.io.File;
import java.util.HashMap;

public class books_upload extends AppCompatActivity {

    private CardView pdfCard;
    private EditText pdfTitle;
    private Button btnUpload;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private ProgressDialog progress;

    private TextView pdfName;

    private Uri pdfData;
    private String nameOfPdf,title;

    private final int request = 1;
    String downloadUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books_upload);
        progress = new ProgressDialog(this);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Study Material");
        storageReference = FirebaseStorage.getInstance().getReference();
        pdfCard = findViewById(R.id.cardSelectPdf);
        pdfTitle = findViewById(R.id.pdfTitle);
        btnUpload = findViewById(R.id.uploadPdf);
        progress = new ProgressDialog(this);
        pdfName = findViewById(R.id.pdfName);
        pdfCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFiles();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title = pdfTitle.getText().toString();
                if(title.isEmpty()){
                    pdfTitle.setError("Empty");
                    pdfTitle.requestFocus();
                }else if(pdfData == null){
                    Toast.makeText(books_upload.this, "Please Select PDF!", Toast.LENGTH_SHORT).show();
                }else{
                    uploadPdf();
                }
            }
        });
    }

    private void uploadPdf() {
        progress.setTitle("Please Wait...");
        progress.setMessage("Uploading PDF...");
        progress.show();
        progress.setCancelable(false);
        StorageReference reference = storageReference.child("pdf/" +nameOfPdf+"-"+System.currentTimeMillis()+".pdf");
        reference.putFile(pdfData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while(!uriTask.isComplete());
                Uri uri = uriTask.getResult();
                uploadData(String.valueOf(uri));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progress.dismiss();
                Toast.makeText(books_upload.this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadData(String downloadUrl) {
        String primaryKey = databaseReference.child("pdf").push().getKey();

        HashMap<String, Object> data = new HashMap<>();
        data.put("pdfTitle",title);
        data.put("pdfUrl",downloadUrl);

        databaseReference.child(primaryKey).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progress.dismiss();
                Toast.makeText(books_upload.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                pdfTitle.setText("");
                pdfName.setText("No File Selected");
                pdfData = null;
                Intent intent = new Intent(books_upload.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pdfTitle.setText("");
                pdfName.setText("No File Selected");
                pdfData = null;
                progress.dismiss();
                Toast.makeText(books_upload.this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openFiles() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select PDF file"),request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == request && resultCode == RESULT_OK){
            pdfData = data.getData();

            if(pdfData.toString().startsWith("content://")){
                Cursor cursor = null;
                try {
                    cursor = books_upload.this.getContentResolver().query(pdfData,null,null,null,null);
                    if(cursor != null && cursor .moveToFirst()){
                        nameOfPdf = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }else if(pdfData.toString().startsWith("file://")){
                nameOfPdf = new File(pdfData.toString()).getName();
            }

            pdfName.setText(nameOfPdf);
        }
    }
}
  