package com.example.smartnfctag.SubFunctionality;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartnfctag.R;

public class PdfViewerActivity extends AppCompatActivity {

    private WebView webView;
    private ProgressBar progressBar;
    private  NfcAdapter nfcAdapter;


  //  private static final String PDF_URL = "https://drive.google.com/file/d/1z201KNOge2HsLxU7sUX6Q9TZYdRbgDjI/view?usp=sharing"; // Replace with your actual link
    private static final String PDF_URL = "https://drive.google.com/file/d/1UM984JHK9SV1Iuk2SRS6lPgadwk41f5o/view?usp=sharing"; // Replace with your actual link
    private LinearLayout beforeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);


        webView = findViewById(R.id.webView);
        beforeContainer = (LinearLayout) findViewById(R.id.before_container);
        progressBar = findViewById(R.id.progressBar);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        beforeContainer.setVisibility(View.VISIBLE);
        webView.setVisibility(View.GONE);

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
                if (isInternetPermissionGranted()) {
                    loadPdf();
                } else {
                    requestInternetPermission();
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

    // Load the PDF using Google Drive's built-in PDF viewer
    private void loadPdf() {
        beforeContainer.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.setWebViewClient(new WebViewClient());

        // Update progress bar using WebChromeClient
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(newProgress);

                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        // Convert Google Drive file link to direct PDF link
        String directPdfUrl = PDF_URL.replace("/view", "/preview");
        webView.loadUrl(directPdfUrl);
    }

    // Check Internet permission
    private boolean isInternetPermissionGranted() {
        return checkSelfPermission(android.Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED;
    }

    // Request Internet permission
    private void requestInternetPermission() {
        requestPermissions(new String[]{android.Manifest.permission.INTERNET}, 100);
    }

    // Handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadPdf();
            } else {
                Toast.makeText(this, "Internet permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}