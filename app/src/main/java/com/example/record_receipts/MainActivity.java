package com.example.record_receipts;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create photo folders
        makeDirectories();

        // Prepare empty file for new picture
        File photo = null;
        try {
            photo = createImageFile();
            Log.v("", "getPath(): " + photo.getPath());
            Log.v("", "exists:" + photo.exists());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Get uri for file
        Uri photo_uri = FileProvider.getUriForFile(getApplicationContext(), "com.example.record_receipts.provider", photo);
        Log.v("image", "toString" + photo_uri.toString());
        Log.v("image", "getPath" + photo_uri.getPath());

        // Take photo
        ActivityResultLauncher<Uri> takePicture = registerForActivityResult(new ActivityResultContracts.TakePicture(), result -> {
            Log.v("result", result.toString());
            Log.v("uri", photo_uri.toString());

            // Start OCR Activity here
        });


        Button take_photo = findViewById(R.id.activity_main_take_button);

        // Onclick, take picture and store into photo_uri
        take_photo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                takePicture.launch(photo_uri);
            }
        });

        // Does no true functionality right now
        Button upl = findViewById(R.id.activity_main_upload_button);
        upl.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                testDirectory("");
            }
        });
    }

    // Return empty jpg file for picture to be inserted
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "_" + timeStamp;
        File imagePath = new File(getApplicationContext().getFilesDir(), "temp");
        Log.v("", "path exists: " + imagePath.exists());
        File photo = File.createTempFile("JPEG_", imageFileName + ".jpg", imagePath);
        Log.v("", "imagePath: " + imagePath);
        Log.v(" ", "newFilepath: " + photo.getPath());
        return photo;
    }

    // On start, create directories if they do not exist. If they do, this does nothing
    private void makeDirectories() {
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

    // To identify existing directories and files
    private void testDirectory(String dir) {
        File imagePath = new File(getApplicationContext().getFilesDir(), dir);
        Log.d("Files", "Path: " + imagePath);
        File directory = new File(imagePath, "");
        File[] files = directory.listFiles();
        Log.d("Files", "Size: " + files.length);
        for (int i = 0; i < files.length; i++) {
            Log.d("Files", "FileName:" + files[i].getName());
            Log.d("Files", "Size: " + files[i].getTotalSpace());
        }

    }
}

