package com.example.smartnfctag.SubFunctionality;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartnfctag.R;

import java.io.IOException;
import java.util.Arrays;

public class SilientOffOn extends AppCompatActivity {


    private NfcAdapter nfcAdapter;
    private static final int NOTIFICATION_POLICY_ACCESS_REQUEST_CODE = 100;
    private Button silient_on;
    private Button silient_off;

    private boolean setSlientFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.silient_off_on);
        silient_on= findViewById(R.id.silient_on);
        silient_off= findViewById(R.id.silient_off);

        // Check if the app has Do Not Disturb access
        if (!hasDoNotDisturbAccess()) {
            // Request permission
            requestDoNotDisturbAccess();
        } else {
            // Do your operation
        }
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC is not supported on this device!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        silient_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSlientFlag = true;

            }
        });
        silient_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSlientFlag = false;

            }
        });
    }
    private boolean hasDoNotDisturbAccess() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        return notificationManager.isNotificationPolicyAccessGranted();
    }

    private void requestDoNotDisturbAccess() {
        Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
        startActivityForResult(intent, NOTIFICATION_POLICY_ACCESS_REQUEST_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NOTIFICATION_POLICY_ACCESS_REQUEST_CODE) {
            if (hasDoNotDisturbAccess()) {
                // Do your operation
            } else {
                // User denied access,
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if (detectedTag != null) {
                readNfcTag(detectedTag);
                // writeTag(detectedTag, "Silent Mode Trigger");
            }
        }
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if (detectedTag != null) {
                writeTag(detectedTag, "Silent Mode Trigger");
            }
        }
    }
    private void writeTag(Tag tag, String message) {
        NdefMessage ndefMessage = createNdefMessage(message);

        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();
                if (ndef.isWritable()) {
                    ndef.writeNdefMessage(ndefMessage);
                 //   Toast.makeText(this, "Write to NFC successful!", Toast.LENGTH_SHORT).show();
                    setPhoneToSilent(setSlientFlag);
                } else {
                    Toast.makeText(this, "NFC tag is read-only", Toast.LENGTH_SHORT).show();
                }
                ndef.close();
            } else {
                Toast.makeText(this, "NFC tag is not supported", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException | FormatException e) {
            e.printStackTrace();
            Toast.makeText(this, "Write failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private NdefMessage createNdefMessage(String text) {
        NdefRecord ndefRecord = NdefRecord.createTextRecord("en", text);
        return new NdefMessage(new NdefRecord[]{ndefRecord});
    }

    private void setPhoneToSilent(boolean setSlientFlag) {
       // if setSlientFlag is true make setRingMode Ring if false make RINGER_MODE_SILENT
        if (setSlientFlag){
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (audioManager != null) {
                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                Toast.makeText(this, "Phone set to RING mode Normal", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to set phone to silent mode", Toast.LENGTH_SHORT).show();
            }
        }else {

            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (audioManager != null) {
                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                Toast.makeText(this, "Phone set to silent mode", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to set phone to silent mode", Toast.LENGTH_SHORT).show();
            }
        }
    }


    // Read NFC tag and display content in Toast
    private void readNfcTag(Tag tag) {
        Ndef ndef = Ndef.get(tag);
        if (ndef != null) {
            try {
                ndef.connect();
                NdefMessage ndefMessage = ndef.getNdefMessage();
                NdefRecord[] records = ndefMessage.getRecords();

                for (NdefRecord record : records) {
                    if (Arrays.equals(record.getType(), NdefRecord.RTD_TEXT)) {
                        String payload = new String(record.getPayload(), "UTF-8");
                        Toast.makeText(this, "NFC Message: " + payload, Toast.LENGTH_LONG).show();
                    }
                }
                ndef.close();
            } catch (Exception e) {
                Toast.makeText(this, "Error reading NFC tag!", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
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
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter[] writeTagFilters = new IntentFilter[]{tagDetected};

        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, writeTagFilters, null);
        }
    }

    // Disable foreground NFC dispatch
    @Override
    protected void onPause() {
        super.onPause();
        nfcAdapter.disableForegroundDispatch(this);
    }
}
