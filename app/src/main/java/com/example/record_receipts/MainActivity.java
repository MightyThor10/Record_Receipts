package com.example.record_receipts;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
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
import java.security.Permission;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private String currentPhotoPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        makeDirectories();
        File photo = null;
        try {
            photo = createImageFile();
        }
         catch (IOException e) {
            e.printStackTrace();
        }
        Log.v("", "getPath(): " + photo.getPath());
        Log.v("", "exists:" + photo.exists());
        Uri photo_uri = FileProvider.getUriForFile(getApplicationContext(), "com.example.record_receipts.provider", photo);

        ActivityResultLauncher<Uri> takePicture = registerForActivityResult(new ActivityResultContracts.TakePicture(), result -> {
            Log.v(" ", result.toString());
            Log.v("uri", photo_uri.toString());
        });

        //ActivityResultLauncher<String> needPermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        //});
        //needPermission.launch(Manifest.permission.CAMERA);


        Log.v("image", "toString" + photo_uri.toString());
        Log.v("image", "getPath" + photo_uri.getPath());
        Button take_photo = findViewById(R.id.activity_main_take_button);
        take_photo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                takePicture.launch(photo_uri);
            }
        });

        Button upl = findViewById(R.id.activity_main_upload_button);
        upl.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                testDirectory("");
            }
        });

    }

        private File createImageFile () throws IOException {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "_" + timeStamp;
            File imagePath = new File(getApplicationContext().getFilesDir(), "temp");
            //imagePath.mkdir();
            Log.v("", "path exists: " + imagePath.exists());
            File photo = File.createTempFile("JPEG_", imageFileName + ".jpg", imagePath);
            Log.v("", "imagePath: " + imagePath);
            Log.v(" ", "newFilepath: " + photo.getPath());
            return photo;
        }

        private long querySpace () {
            long availableBytes = 0;
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

        private void makeDirectories(){
            File temp = new File(getApplicationContext().getFilesDir(), "temp");
            temp.mkdir();
            File gas = new File(getApplicationContext().getFilesDir(), "gas");
            gas.mkdir();
            File grocery = new File(getApplicationContext().getFilesDir(), "grocery");
            grocery.mkdir();
            File dining = new File(getApplicationContext().getFilesDir(), "dining");
            dining.mkdir();
            File drug_store = new File(getApplicationContext().getFilesDir(), "drug_store");
            drug_store.mkdir();
            File entertainment = new File(getApplicationContext().getFilesDir(), "entertainment");
            entertainment.mkdir();
        }

        private void testDirectory (String dir) {
            File imagePath = new File(getApplicationContext().getFilesDir(),dir);
            Log.d("Files", "Path: " + imagePath);
            File directory = new File(imagePath,"");
            File[] files = directory.listFiles();
            Log.d("Files", "Size: " + files.length);
            for (int i = 0; i < files.length; i++) {
                Log.d("Files", "FileName:" + files[i].getName());
               // Log.d("Files", "Size: " + files[i].getTotalSpace());
            }

        }
    }

