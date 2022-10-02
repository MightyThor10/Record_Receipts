package com.example.record_receipts;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private String currentPhotoPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File photo = createImageFile();

        Log.v("space", "space is " + querySpace());
        Log.v("yaaa", "directory");

        ActivityResultLauncher<Uri> mTakePicture = registerForActivityResult(new ActivityResultContracts.TakePicture(), result -> {
            Log.v("image", "size: " + photo.length());
            //testDirectory();
            return;
        });
        ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        return;
                    }
                });


        Uri photo_uri = FileProvider.getUriForFile(getApplicationContext(), "com.example.record_receipts", photo);
        Log.v("image", "toString" + photo_uri.toString());
        Log.v("image", "getPath" + photo_uri.getPath());
        Button take_photo = findViewById(R.id.activity_main_take_button);
        take_photo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mTakePicture.launch(photo_uri);
            }
        });

        Button upload_photo = findViewById(R.id.activity_main_upload_button);
        upload_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGetContent.launch("image/*");
            }
        });
    }

    private File createImageFile(){
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File imagePath = new File(getApplicationContext().getFilesDir(), "temp");
        File image = new File(imagePath, imageFileName+".jpg");
        //File image = null;
        //image = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), imageFileName);
        currentPhotoPath = image.getAbsolutePath();
        Log.v("image", currentPhotoPath);
        return image;
    }

    private long querySpace() {
        long availableBytes =0;
        StorageManager storageManager =
                getApplicationContext().getSystemService(StorageManager.class);
        UUID appSpecificInternalDirUuid = null;
        try {
            appSpecificInternalDirUuid = storageManager.getUuidForPath(getFilesDir());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            availableBytes =
                    storageManager.getAllocatableBytes(appSpecificInternalDirUuid);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return availableBytes;
    }

    private void testDirectory(){
        String imagePath = new File(getApplicationContext().getFilesDir(), "temp").toString();
        Log.d("Files", "Path: " + imagePath);
        File directory = new File(imagePath);
        File[] files = directory.listFiles();
        Log.d("Files", "Size: "+ files.length);
        for (int i = 0; i < files.length; i++)
        {
            Log.d("Files", "FileName:" + files[i].getName());
        }
    }

        }