package com.example.record_receipts;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class LoginActivity extends AppCompatActivity {
    Dialog dialog;
    Button reset;
    Button go_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_login);
        super.onCreate(savedInstanceState);

        // Get components
        Button loginButton = findViewById(R.id.first_screen_submit_password);
        EditText password = (EditText) findViewById(R.id.first_screen_ask_password);
        Button forgotPasswordButton = (Button) findViewById(R.id.first_screen_forgot_password);
        loginButton.setOnClickListener(view -> {

            // Grab IV and encrypted password from SharedPreferences. Decode from Base64
            SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
            byte[] encryptedPassword = Base64.decode(sharedPreferences.getString("encryptionPassword", "foo"), Base64.DEFAULT);
            byte[] encryptionIv = Base64.decode(sharedPreferences.getString("encryptionIv", "foo"), Base64.DEFAULT);


            try {
                // Get Keystore and retrieve key
                KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
                keyStore.load(null);
                KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) keyStore
                        .getEntry("password", null);
                final SecretKey secretKey = secretKeyEntry.getSecretKey();

                // Create new decrypting cypher using IV
                final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
                final GCMParameterSpec spec = new GCMParameterSpec(128, encryptionIv);
                cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);

                // Decrypt password
                final byte[] decodedData = cipher.doFinal(encryptedPassword);
                final String unencryptedString = new String(decodedData, StandardCharsets.UTF_8);

                if (password.getText().toString().equals(unencryptedString)) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Wrong Password!", Toast.LENGTH_SHORT).show();
                }

            } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | IOException | InvalidKeyException | UnrecoverableEntryException | NoSuchPaddingException e) {
                e.printStackTrace();
            }


        });
        // Equivalent to clearing app storage and cache from Android Settings
        // TODO Create confirmation dialog

        //Create Dialog
        dialog = new Dialog(LoginActivity.this);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.background));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
        reset = dialog.findViewById(R.id.yes);
        go_back = dialog.findViewById(R.id.no);

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
                    ((ActivityManager) getApplicationContext().getSystemService(ACTIVITY_SERVICE))
                            .clearApplicationUserData();
                }
                dialog.dismiss();
            }
        });

        go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        forgotPasswordButton.setOnClickListener(view -> {
            /*if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
                ((ActivityManager) getApplicationContext().getSystemService(ACTIVITY_SERVICE))
                        .clearApplicationUserData();
            }*/
            dialog.show();
        });
    }
}