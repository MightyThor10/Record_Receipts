package com.example.record_receipts;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class AnalysisActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner spinner;
    EditText editText;
    Button saveButton;
    ImageView imageView;
    String[] Categories = {"Gas", "Grocery",
            "Drug Store", "Dining",
            "Entertainment"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.analysis_screen);
        File path = getFilesDir();
        Uri photoURI = Uri.parse(getIntent().getStringExtra("photoURI"));

        // Set up spinner categories
        spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter ad
                = new ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                Categories);
        ad.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item);
        spinner.setAdapter(ad);

        editText = findViewById(R.id.edit_text);
        imageView = findViewById(R.id.imageView);
        imageView.setImageURI(photoURI);

        try {
            detect_text(photoURI);
        } catch (IOException e) {
            e.printStackTrace();
        }

        saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Prepare display string for path string
                String category = spinner.getSelectedItem().toString().toLowerCase().replace(' ', '_');

                // Find new file name
                int photoNumber = 0;
                File newFile;
                do {
                    photoNumber++;
                    newFile = new File(getApplicationContext().getFilesDir() + "/" + category + "/" + category + photoNumber + ".jpg");
                }
                while (newFile.exists());

                // Move pic to appropriate directory
                try (FileOutputStream outputStream = new FileOutputStream(newFile, false)) {
                    InputStream inputStream = getContentResolver().openInputStream(photoURI);
                    int read;
                    byte[] bytes = new byte[8192];
                    while ((read = inputStream.read(bytes)) != -1) {
                        outputStream.write(bytes, 0, read);
                    }

                    // Return to main activity
                    Intent startMain = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(startMain);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void detect_text(Uri photoURI) throws IOException {
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        // Get image from temp file via URI
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoURI);
        InputImage image = InputImage.fromBitmap(bitmap, 0);

        Task<Text> result = recognizer.process(image)
                .addOnSuccessListener(new OnSuccessListener<Text>() {
                    @Override
                    public void onSuccess(Text visionText) {
                        String resultText = visionText.getText();
                        List<Text.TextBlock> blocks = visionText.getTextBlocks();
                        Boolean total_flag = false;
                        String total = "";

                        // Search for total $
                        for (Text.TextBlock block : visionText.getTextBlocks()) {
                            String txt = block.getText();
                            Log.d("this block: ", txt);
                            if (txt.equalsIgnoreCase("total") && resultText.toLowerCase().contains("grand total") == false) {
                                total = txt;
                                total_flag = true;
                                continue;
                            } else if (txt.toLowerCase().contains("$")) {
                                total = txt;
                                total_flag = true;
                                continue;
                            }
                            if (total_flag == true) {
                                // TODO Store runnning total in SharedPref
                                editText.setText(total + " in this receipt is: $" + txt);
                                break;
                            }
                        }
                    }
                })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("Tag failed", "Processing image failed");
                            }
                        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }
}