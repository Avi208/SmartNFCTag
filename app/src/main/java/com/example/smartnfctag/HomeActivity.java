package com.example.smartnfctag;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CardAdapter adapter;
    private List<CardItem> cardItemList;
    private NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Optionally, you can set the title programmatically:
        // getSupportActionBar().setTitle("SmartNFC tag connect");

        // Set up RecyclerView with a grid of 2 columns
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        cardItemList = new ArrayList<>();
        // Populate 8 sample card items (for a 2 x 4 grid)
        for (int i = 1; i <= 8; i++) {
            switch (i) {
                case 1:
                    cardItemList.add(new CardItem("Set Alaram", R.drawable.set_alaram));
                    break;
                case 2:
                    cardItemList.add(new CardItem("Silient Phone", R.drawable.silient_write));
                    break;
                case 3:
                    cardItemList.add(new CardItem("Card 3", R.drawable.ic_launcher_foreground));
                    break;
                case 4:
                    cardItemList.add(new CardItem("Card 4", R.drawable.ic_launcher_foreground));
                    break;
                case 5:
                    cardItemList.add(new CardItem("Card 5", R.drawable.ic_launcher_foreground));
                    break;
                case 6:
                    cardItemList.add(new CardItem("Card 6", R.drawable.ic_launcher_foreground));
                    break;
                default:
                    cardItemList.add(new CardItem("Card " + i, R.drawable.ic_launcher_foreground));
            }
        }

        adapter = new CardAdapter(cardItemList);
        recyclerView.setAdapter(adapter);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC is not supported on this device!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }
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

    // Disable foreground NFC dispatch
    @Override
    protected void onPause() {
        super.onPause();
        nfcAdapter.disableForegroundDispatch(this);
    }
}