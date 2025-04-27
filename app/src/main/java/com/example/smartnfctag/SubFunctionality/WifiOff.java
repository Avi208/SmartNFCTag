package com.example.smartnfctag.SubFunctionality;


import android.Manifest;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.smartnfctag.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class WifiOff extends AppCompatActivity {
    private  BottomSheetDialog bottomSheetDialog;

    private  NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] intentFiltersArray;
    private static final int REQUEST_CALL_PHONE = 1;
    private static final int REQUEST_WIFI_PERMISSION = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_off);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE)
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_NETWORK_STATE)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.CHANGE_WIFI_STATE,
                            Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.CHANGE_NETWORK_STATE
                    },
                    REQUEST_WIFI_PERMISSION);
        }

// Request WRITE_SETTINGS permission separately
        if (!Settings.System.canWrite(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, REQUEST_WIFI_PERMISSION);
        }


    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WIFI_PERMISSION) {
            if (grantResults.length > 2 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Wi-Fi permissions granted", Toast.LENGTH_SHORT).show();
                disableWifi();
            } else {
                Toast.makeText(this, "Wi-Fi permissions denied", Toast.LENGTH_SHORT).show();
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
                if (payload.trim().equals("enWIFI_OFF_006") || payload.trim().equals("enMASTER_KEY") ){
                   disableWifi();
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

    private void disableWifi() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifiManager != null && wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(false);
                Toast.makeText(this, "Wi-Fi Disabled (Android 9 or lower)", Toast.LENGTH_SHORT).show();
            }
        } else {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                connectivityManager.requestNetwork(new NetworkRequest.Builder().build(), new ConnectivityManager.NetworkCallback() {
                    @Override
                    public void onAvailable(Network network) {
                        // make wifi off
                        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        wifiManager.setWifiEnabled(false);

                        connectivityManager.bindProcessToNetwork(null); // Disconnect network
                        Toast.makeText(getApplicationContext(), R.string.wifi_instruction, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
                        startActivity(intent);
                    }

                    @Override
                    public void onLost(@NonNull Network network) {
                        super.onLost(network);
                        Toast.makeText(getApplicationContext(), "Wi-Fi network lost", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        }
    }





}
