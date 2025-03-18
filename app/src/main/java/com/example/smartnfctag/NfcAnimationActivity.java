package com.example.smartnfctag;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.PendingIntent;
import android.content.Context;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;

public class NfcAnimationActivity {

    private static BottomSheetDialog bottomSheetDialog;
    private static ImageView phoneImage;
    private static ImageView nfcTagImage;
    private static NfcAdapter nfcAdapter;


    private PendingIntent pendingIntent;
    private IntentFilter[] writeTagFilters;
    private Tag detectedTag;

    public static void showBottomSheet(Context context) {
        View view = View.inflate(context, R.layout.dialog_nfc_bottom_sheet, null);
        bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setCancelable(true);

        phoneImage = view.findViewById(R.id.phoneImage);
        nfcTagImage = view.findViewById(R.id.nfcTagImage);
        Button btnScan = view.findViewById(R.id.btnScan);
        Button btnOk = view.findViewById(R.id.btnOk);

        // Handle Scan Button Click
        btnScan.setOnClickListener(v -> startNfcAnimation());

        // Handle OK Button Click
        btnOk.setOnClickListener(v -> bottomSheetDialog.dismiss());

        bottomSheetDialog.show();
    }

    // Start NFC Tap Animation
    private static void startNfcAnimation() {
        // Move phone toward NFC tag
        ObjectAnimator moveY = ObjectAnimator.ofFloat(phoneImage, "translationY", 0f, 80f);
        moveY.setDuration(800);

        // Scale phone when close to tag
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(phoneImage, "scaleX", 1f, 1.2f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(phoneImage, "scaleY", 1f, 1.2f);
        scaleX.setDuration(500);
        scaleY.setDuration(500);

        // Fade in NFC tag
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(nfcTagImage, "alpha", 0f, 1f);
        fadeIn.setDuration(500);

        // Combine animations
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(moveY, scaleX, scaleY, fadeIn);
        animatorSet.start();

    }

    // Handle NFC Read
    public static void handleNfcRead(Tag tag, Context context) {
        if (tag != null) {
            String tagData = bytesToHex(tag.getId());
            android.widget.Toast.makeText(context, "NFC Tag Detected: " + tagData, android.widget.Toast.LENGTH_SHORT).show();
        }
    }

    // Convert NFC Data to String
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}