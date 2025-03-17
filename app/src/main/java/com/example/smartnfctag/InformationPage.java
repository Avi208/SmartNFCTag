package com.example.smartnfctag;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class InformationPage extends AppCompatActivity {
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private Handler handler;
    private Runnable runnable;
    private int currentPage = 0;
    private ImageButton closeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_main);

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        closeButton = findViewById(R.id.close);

        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Write");
                            break;
                        case 1:
                            tab.setText("Read");
                            break;
                        case 2:
                            tab.setText("Overwrite");
                            break;
                        default:
                            tab.setText("Page " + (position + 1));
                            break;
                    }
                }
        ).attach();

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (currentPage == adapter.getItemCount()) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++, true);
                handler.postDelayed(this, 3000); // Change page every 3 seconds
            }
        };
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // call Homepage Activity
                Intent intent = new Intent(InformationPage.this, HomeActivity.class);
                startActivity(intent);
            }
        });
        handler.postDelayed(runnable, 3000); // Start the automatic page change
        // create a method play Video repeatedly
        playVideo();
    }

    private void playVideo() {
        VideoView videoView = findViewById(R.id.videoView);
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.card_tap_to_pay);

        videoView.setVideoURI(videoUri);
        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            videoView.start();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}