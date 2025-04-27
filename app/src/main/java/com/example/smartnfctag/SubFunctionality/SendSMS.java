package com.example.smartnfctag.SubFunctionality;


import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.smartnfctag.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class SendSMS extends AppCompatActivity {
    private  BottomSheetDialog bottomSheetDialog;

    private  NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] intentFiltersArray;
    private static final int REQUEST_CALL_PHONE = 1;
    private static final int REQUEST_WIFI_PERMISSION = 1;
    private int REQUEST_SMS_PERMISSION= 100;
    private EditText phoneNumber;
    private EditText UserMessage;
    private Button okButton;
    private Button btnSelectContact;
    private static final int REQUEST_CODE_PICK_CONTACT = 1;
    private static final int REQUEST_CODE_PERMISSION_READ_CONTACTS = 100;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_sms);
        phoneNumber= findViewById(R.id.et_input);
        okButton= findViewById(R.id.btnOk);
        btnSelectContact = findViewById(R.id.btnSelectContact);
        UserMessage= findViewById(R.id.mess_input);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    REQUEST_SMS_PERMISSION);
        }
       
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnSelectContact.setOnClickListener(v -> {
            if (checkPermission()) {
                openContactPicker();
            } else {
                requestPermission();
            }
        });

    }
    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_CONTACTS},
                REQUEST_CODE_PERMISSION_READ_CONTACTS);
    }
    private void openContactPicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_PICK_CONTACT);
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
                if (payload.trim().equals("enSEND-SMS_005")|| payload.trim().equals("enMASTER_KEY")){
                    // get phone number from EditText et_input
                    String phoneNumner = phoneNumber.getText().toString();
                    String userMessageText = UserMessage.getText().toString();
                    sendSms(phoneNumner, userMessageText);
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
    private void sendSms(String phoneNumber, String message) {
        if (checkSelfPermission(android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                Toast.makeText(this, "Message sent to " + phoneNumber, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Failed to send SMS: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "SMS permission not granted", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_CONTACT && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri contactUri = data.getData();
                String contactNumber = getContactNumber(contactUri);

                if (contactNumber != null) {
                    phoneNumber.setText(contactNumber);
                } else {
                    Toast.makeText(this, "No phone number found", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    private String getContactNumber(Uri contactUri) {
        String phoneNumber = null;
        Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            int hasPhoneIndex = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
            String contactId = cursor.getString(idIndex);

            if (cursor.getInt(hasPhoneIndex) > 0) {
                Cursor phoneCursor = getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        new String[]{contactId},
                        null);

                if (phoneCursor != null && phoneCursor.moveToFirst()) {
                    int numberIndex = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    phoneNumber = phoneCursor.getString(numberIndex);
                    phoneCursor.close();
                }
            }
            cursor.close();
        }
        return phoneNumber;
    }


}
