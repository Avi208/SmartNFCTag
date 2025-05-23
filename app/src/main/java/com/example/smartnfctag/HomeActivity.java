package com.example.smartnfctag;

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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CardAdapter adapter;
    private List<CardItem> cardItemList;



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
        for (int i = 1; i <= 12; i++) {
            switch (i) {
                case 1:
                    cardItemList.add(new CardItem("Set Alaram", R.drawable.set_alaram));
                    break;
                case 2:
                    cardItemList.add(new CardItem("Silient Phone", R.drawable.silient_write));
                    break;
                case 3:
                    cardItemList.add(new CardItem("Make Call", R.drawable.make_call));
                    break;
                case 4:
                    cardItemList.add(new CardItem("Navigate APP", R.drawable.navigate_app));
                    break;
                case 5:
                    cardItemList.add(new CardItem("Send SMS", R.drawable.send_sms_page));
                    break;
                case 6:
                    cardItemList.add(new CardItem("WIFI off", R.drawable.wifi_off));
                    break;
                case 7:
                    cardItemList.add(new CardItem("Find Address", R.drawable.location_me));
                    break;
                case 8:
                    cardItemList.add(new CardItem("Screen Brightness", R.drawable.screen_brightness));
                    break;
                case 9:
                    cardItemList.add(new CardItem("Read Doc", R.drawable.pdf_reader));
                    break;
                case 10:
                    cardItemList.add(new CardItem("Read Message", R.drawable.message));
                    break;
                case 11:
                    cardItemList.add(new CardItem("Social Media", R.drawable.inst_face));
                    break;
                case 12:
                    cardItemList.add(new CardItem("Share Contact", R.drawable.share_contact));
                    break;
                default:
                    cardItemList.add(new CardItem(" Default " + i, R.drawable.ic_launcher_foreground));
            }
        }

        adapter = new CardAdapter(cardItemList);
        recyclerView.setAdapter(adapter);

    }



}