package com.example.smartnfctag;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
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
        for (int i = 1; i <= 8; i++) {
            cardItemList.add(new CardItem("Card " + i, R.drawable.ic_launcher_foreground));
        }

        adapter = new CardAdapter(cardItemList);
        recyclerView.setAdapter(adapter);
    }
}