package com.example.record_receipts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class CreatePassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_password);
        EditText editPassword, editConfirmPassword;
        Button createPasswordButton;
        editPassword  = (EditText)findViewById(R.id.create_password);
        editConfirmPassword = (EditText)findViewById(R.id.confirm_password);
        createPasswordButton = (Button)findViewById(R.id.create_password_button);


        createPasswordButton.setOnClickListener(view -> {
            if(editPassword.getText().toString().equals(editConfirmPassword.getText().toString()))
            {
                try {

                    // Create Key in Keystore
                    final KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
                    final KeyGenParameterSpec keyGenParameterSpec = new KeyGenParameterSpec.Builder("password",
                            KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                            .build();
                    keyGenerator.init(keyGenParameterSpec);
                    final SecretKey secretKey = keyGenerator.generateKey();

                    // Create Cipher and encrypt with IV
                    final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
                    cipher.init(Cipher.ENCRYPT_MODE, secretKey);
                    byte[] iv = cipher.getIV();
                    byte[] encryption = cipher.doFinal(editPassword.getText().toString().getBytes(StandardCharsets.UTF_8));

                    // Storing data into SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
                    SharedPreferences.Editor myEditor = sharedPreferences.edit();

                    // Encode into Base64 String - cant store arrays in SharedPreferences
                    String encryptionString = Base64.encodeToString(encryption,Base64.DEFAULT);
                    String encryptionIvString = Base64.encodeToString(iv,Base64.DEFAULT);
                    myEditor.putString("encryptionPassword", encryptionString);
                    myEditor.putString("encryptionIv", encryptionIvString);
                    myEditor.apply();

                    // Start Login Activity
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);

                } catch (NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchPaddingException | InvalidAlgorithmParameterException | NoSuchProviderException e) {
                    e.printStackTrace();
                }
            }
        });




    }
}