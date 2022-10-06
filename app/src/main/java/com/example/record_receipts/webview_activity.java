package com.example.record_receipts;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.util.List;

public class webview_activity extends Activity {
    WebView myWebView;
    TextView title;
    String shopName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        title = findViewById(R.id.activity_webview_title);
        myWebView = findViewById(R.id.activity_webview_webview);

        detect_text();


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
                        shopName = String.valueOf(blocks.get(0).getText());
                        title.setText("Searching for " + shopName + " near you");
                        myWebView.loadUrl("https://www.google.com/search?q="+shopName+" near+me");
                        myWebView.getSettings().setJavaScriptEnabled(false);
                        myWebView.setWebViewClient(new WebViewClient());
                        Log.d("something", shopName);
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
}
