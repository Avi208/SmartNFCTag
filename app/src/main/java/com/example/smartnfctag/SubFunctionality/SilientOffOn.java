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
import android.util.Log;
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
    private void readNfcTag(Ndef ndef) {
        try {
            ndef.connect();
            NdefMessage ndefMessage = ndef.getNdefMessage();
            if (ndefMessage != null) {
                String payload = new String(ndefMessage.getRecords()[0].getPayload());
                if (payload.trim().equals("enSEND-SMS_005")|| payload.trim().equals("enMASTER_KEY")){
                  setPhoneToSilent(setSlientFlag);
                }else{
                    Toast.makeText(this, "NFC Tag Invalid, Contact Administrator! ", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(this, "NFC tag is empty.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.d("TestingLogs", e.toString());
            Toast.makeText(this, "Failed to read NFC tag.", Toast.LENGTH_SHORT).show();
        } finally {
            try {
                ndef.close();
            } catch (Exception e) {
                // Ignore
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
}
