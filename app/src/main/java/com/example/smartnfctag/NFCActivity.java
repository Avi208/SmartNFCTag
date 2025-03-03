package com.example.smartnfctag;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

public class NFCActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    private Dialog nfcDialog;
    private Button btnScan, btnOk;
    private ImageView nfcImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nfc_dialog);  // Set your main layout

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC is not supported on this device!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Show the NFC dialog when the activity starts
        showNfcDialog();
    }

    // Display a custom NFC scan dialog
    private void showNfcDialog() {
        nfcDialog = new Dialog(this);
        nfcDialog.setContentView(R.layout.nfc_dialog);
        nfcDialog.setCancelable(false); // Prevent dismissal outside clicks

        btnScan = nfcDialog.findViewById(R.id.btn_scan);
        btnOk = nfcDialog.findViewById(R.id.btn_ok);
        nfcImage = nfcDialog.findViewById(R.id.nfc_image);

        btnScan.setOnClickListener(v -> {
            Toast.makeText(this, "Bring NFC tag closer to scan", Toast.LENGTH_SHORT).show();
        });

        btnOk.setOnClickListener(v -> nfcDialog.dismiss());

        nfcDialog.show();
    }

    // Handle new NFC intent when the tag is detected
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if (tag != null) {
                readNfcTag(tag);
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

    // Enable foreground NFC dispatch
    @Override
    protected void onResume() {
        super.onResume();

       Intent intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
       // prepare pending intent
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
       // enable foreground dispatch
       if (nfcAdapter != null) {
           nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
       }
    }

    // Disable foreground NFC dispatch
    @Override
    protected void onPause() {
        super.onPause();
        nfcAdapter.disableForegroundDispatch(this);
    }
}