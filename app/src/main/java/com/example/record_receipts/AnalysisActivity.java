package com.example.record_receipts;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
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
import java.io.FileOutputStream;
import java.util.List;

public class AnalysisActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner spinner;
    EditText editText;
    Button saveButton;
    ImageView imageView;
    String[] Categories = { "Gas", "Grocery",
            "Drug Stores", "Dining",
            "Other" };
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.analysis_screen);
        File path = getFilesDir();

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
        imageView.setImageResource(R.drawable.img_4064);

        detect_text();

        saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // here is when the save button is clicked. we should send the value of the total to shared pref for total.
                // also save/move this the photo from temp to the the category that is being selected in the spinner.
                // right now I am just saving the category and the total in a txt file but we can change it.
                String fileName = "image_data.txt";
                String content = String.valueOf(editText.getText()) + "\n" + String.valueOf(spinner.getSelectedItem().toString());
                try {
                    FileOutputStream writer = new FileOutputStream(new File(path, fileName));
                    writer.write(content.getBytes());
                    writer.close();
                    Toast.makeText(getApplicationContext(), "Content written at " + path, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void detect_text() {
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.img_4064);
        InputImage image = InputImage.fromBitmap(bitmap, 0);

        Task<Text> result = recognizer.process(image)
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text visionText) {
                                String resultText = visionText.getText();
                                List<Text.TextBlock> blocks = visionText.getTextBlocks();
                                Boolean total_flag = false;
                                String total = "";
                                for (Text.TextBlock block : visionText.getTextBlocks()) {
                                    String txt = block.getText();
                                    Log.d("this block: ", txt);
                                    if (txt.equalsIgnoreCase("total") && resultText.toLowerCase().contains("grand total") == false) {
                                        total = txt;
                                        total_flag = true;
                                        continue;
                                    } else if (txt.toLowerCase().contains("grand total")){
                                        total = txt;
                                        total_flag = true;
                                        continue;
                                    }
                                    if (total_flag == true) {
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