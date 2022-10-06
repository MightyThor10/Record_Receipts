package com.example.record_receipts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class Launch extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        // Retrieve encrypted password from sharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String storedPassword = sharedPreferences.getString("encryptionPassword", "foo");
        Intent intent;

        // Decide on activity if password exists
        if(storedPassword.equals("foo"))
        {
            intent = new Intent(getApplicationContext(), CreatePassword.class);
        }
        else
        {
           intent  = new Intent(getApplicationContext(), LoginActivity.class);
        }
        startActivity(intent);
    }
}