package com.example.record_receipts;

import static java.lang.Character.DECIMAL_DIGIT_NUMBER;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MySharedPref", MODE_PRIVATE);
        SharedPreferences.Editor myEditor = sharedPreferences.edit();

        // if statement required so Intent handling only comes from Analysis Activity
        if (getIntent().hasExtra("total")) {

            String totalValue = "";
            for (int i =0; i < getIntent().getStringExtra("total").length(); i++){

                if (getIntent().getStringExtra("total").charAt(i) == '$'){

                    totalValue += getIntent().getStringExtra("total").substring(i+1);
                }
            }

            String category = getIntent().getStringExtra("category");
            String directory = category + "Total";

            Float cur = sharedPreferences.getFloat(directory, 0.00F);
            Float totalFloat = 0.0F;
            if (totalValue != "")
                totalFloat = Float.parseFloat(totalValue) + cur;
            myEditor.putFloat(directory, totalFloat);

            myEditor.commit();
        }
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
            Intent startOCR = new Intent(getApplicationContext(), AnalysisActivity.class);
            startOCR.putExtra("photoURI", photo_uri.toString());
            startActivity(startOCR);
        });


        Button take_photo = findViewById(R.id.activity_main_take_button);

        // Onclick, take picture and store into photo_uri
        take_photo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                takePicture.launch(photo_uri);
            }
        });

        Button dining = findViewById(R.id.activity_main_dining_button);
        Button drugStore = findViewById(R.id.activity_main_drug_store_button);
        Button entertain = findViewById(R.id.activity_main_entertainment_button);
        Button gas = findViewById(R.id.activity_main_gas_button);
        Button grocery = findViewById(R.id.activity_main_grocery_button);
        TextView gasText = findViewById(R.id.gas_total);
        TextView groceryText = findViewById(R.id.grocery_total);
        TextView diningText = findViewById(R.id.dining_total);
        TextView entertainmentText = findViewById(R.id.entertainment_total);
        TextView drugStoreText = findViewById(R.id.drug_store_total);

        dining.setOnClickListener(view -> {
            moveToSub("dining");
        });

        drugStore.setOnClickListener(view -> {
            moveToSub("drug_store");
        });

        entertain.setOnClickListener(view -> {
            moveToSub("entertainment");
        });

        gas.setOnClickListener(view -> {
            moveToSub("gas");
        });

        grocery.setOnClickListener(view -> {
            moveToSub("grocery");
        });

        // set text boxes based on running totals stored in SharedPreferences
        gasText.setText(String.valueOf(sharedPreferences.getFloat("gasTotal", 0.00F)));
        Log.v("",String.valueOf(sharedPreferences.getFloat("gasTotal", 0.00F)));
        diningText.setText(String.valueOf(sharedPreferences.getFloat("diningTotal", 0.00F)));
        groceryText.setText(String.valueOf(sharedPreferences.getFloat("groceryTotal", 0.00F)));
        entertainmentText.setText(String.valueOf(sharedPreferences.getFloat("entertainmentTotal", 0.00F)));
        drugStoreText.setText(String.valueOf(sharedPreferences.getFloat("drug_storeTotal", 0.00F)));

    }
    // Move to SubActivity, showing list of items in category
    private void moveToSub(String subDir)
    {
        Intent intent = new Intent(getApplicationContext(), SubActivity.class);
        intent.putExtra("category", subDir);
        startActivity(intent);
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

