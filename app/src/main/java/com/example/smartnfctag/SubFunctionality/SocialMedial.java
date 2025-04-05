package com.example.smartnfctag.SubFunctionality;


import android.Manifest;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.smartnfctag.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class SocialMedial extends AppCompatActivity {

    private static final String INSTAGRAM_USERNAME = "avinashr167"; // Change to your username


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.social_media);
        openInstagramProfile(INSTAGRAM_USERNAME);



    }
    private void openInstagramProfile(String username) {
        Uri uriApp = Uri.parse("http://instagram.com/_u/" + username);
        Uri uriWeb = Uri.parse("http://instagram.com/" + username);
        Intent intent = new Intent(Intent.ACTION_VIEW, uriApp);
        intent.setPackage("com.instagram.android");

        try {
            startActivity(intent);  // Open Instagram app
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, uriWeb));  // Open in browser if app not installed
        }
    }




}
