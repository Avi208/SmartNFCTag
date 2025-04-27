package com.example.smartnfctag.SubFunctionality;


import android.Manifest;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.smartnfctag.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class SocialMedial extends AppCompatActivity {

    private static final String INSTAGRAM_USERNAME = "avinashr167"; // Change to your username
    private  NfcAdapter nfcAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.social_media);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);




    }
    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        // prepare pending intent
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                PendingIntent.FLAG_MUTABLE // or FLAG_IMMUTABLE depending on your use case
        );        // enable foreground dispatch
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if (tag != null) {
                Ndef ndef = Ndef.get(tag);
                if (ndef != null) {
                    readNfcTag(ndef);
                } else {
                    Toast.makeText(this, "NFC tag is not NDEF formatted.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    private void readNfcTag(Ndef ndef) {
        try {
            ndef.connect();
            NdefMessage ndefMessage = ndef.getNdefMessage();
            if (ndefMessage != null) {
                String payload = new String(ndefMessage.getRecords()[0].getPayload());
                if (payload.trim().equals("enSOCIAL-MEDIA_011")){
                    openInstagramProfile(INSTAGRAM_USERNAME);
                }else{
                    showCustomToast();
                    // Toast.makeText(this, "NFC Tag Invalid, Contact Administrator! ", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "NFC tag is empty.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Failed to read NFC tag.", Toast.LENGTH_SHORT).show();
        } finally {
            try {
                ndef.close();
            } catch (Exception e) {
                // Ignore
            }
        }
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

    public void showCustomToast() {
        // Inflate the custom layout
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast, null);

        // Find views
        TextView toastText = layout.findViewById(R.id.toast_text);

        // Set text if you want dynamic
        toastText.setText("NFC Tag Invalid, Contact Administrator! ");



        // Create the Toast
        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 100); // Bottom with some margin
        toast.show();
    }





}
