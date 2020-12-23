package com.cordova.mynfccontrollersample.nfc.controller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * This Class is to control on NfcAdapter
 * it's use to not repeat coding
 * @author Kevin Córdova
 */
public class MyNfcController implements INfcController {
    private static final String TAG = MyNfcController.class.getSimpleName();
    private final Context context;
    private static PendingIntent pendingIntent;
    private static NfcAdapter mNfcAdapter;
    @SuppressLint("StaticFieldLeak")
    private static MyNfcController myNfcController;

    /**
     * Constructor
     * @param context is who use the NfcController
     */
    public MyNfcController(Context context) {
        this.context = context;
    }

    /**
     * Singleton Design Pattern to not create the NfcAdapter sometimes
     * @param context is the Activity or Fragment that called it
     * @return INfc controller interface that contains object MyNfcController
     */
    public static INfcController getInstance(Context context) {
        if (myNfcController == null) {
            mNfcAdapter = NfcAdapter.getDefaultAdapter(context);

            pendingIntent = PendingIntent.getActivity(context, 0,
                    // Create an Intent that instance the same Activity or Fragment to call yourself
                    new Intent(context, context.getClass())
                            // Added this Flag
                            // If set, the activity will not be launched if it is already running at the top of the history stack.
                    .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
            return new MyNfcController(context);
        }
        return myNfcController;
    }


    @Override
    public void getData(Intent intent) {
        // Get that NFC type is
        final String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
            // Get the tag from intent
            // TEST Mastercard -> Contains IsoDep and NfcA
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            // TEST --> SELECT COMMAND - ADPU
            byte[] SELECT_COMMAND = {
                    (byte) 0x00, // CLA
                    (byte) 0xA4, // INS
                    (byte) 0x04, // P1
                    (byte) 0x00, // P2
            };
            IsoDep tagDep = IsoDep.get(tag);

            try {
                tagDep.connect();
                byte[] result = tagDep.transceive(SELECT_COMMAND);
                String strResult = transformByteArrayToHexString(result);
                Log.d(TAG, "getData: HEX " + strResult);
                Log.d(TAG, "getData: " + result[0]);
                Log.d(TAG, "getData: " + (byte) 0x90);
                Log.d(TAG, "getData: " + result[1]);
                Log.d(TAG, "getData: " + (byte) 0x00);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Utility class to convert a byte array to a hexadecimal string.
     *
     * @param bytes Bytes to convert
     * @return String, containing hexadecimal representation.
     */
    public static String transformByteArrayToHexString(byte[] bytes) {
        final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for ( int j = 0; j < bytes.length; j++ ) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public String byteToHex(byte num) {
        char[] hexDigits = new char[2];
        hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
        hexDigits[1] = Character.forDigit((num & 0xF), 16);
        return new String(hexDigits);
    }



    /**
     * Send to user for NFC configurations and activate the nfc
     * Create a intent to Settings in base NFC Settings
     * TODO Test Intent to NFC settings
     * Here is necessary to test the intent with this Option
     * because in some phones to verify how is the better option
     * final Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
     */
    @Override
    public void activate() {
        final Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
        context.startActivity(intent);
        // Show a Toast to say for user that he'll activate the nfc
        Toast.makeText(context, "Activate Nfc Here", Toast.LENGTH_SHORT).show();
    }

    /**
     * Verify if NfcAdapter is Enable
     * @return boolean value, if mobile not support nfc it always return false
     */
    @Override
    public boolean isEnabled() {
        try {
            return mNfcAdapter.isEnabled();
        } catch (NullPointerException e) {
            return false;
        }
    }

    /**
     * Activate the method enableForeground to when user activated nfc in settings
     * and return for the app, this allow that app could to watching until read card
     * It'll to be to implement in onResume() Activity lifecycle
     * and Also before in onPause()
     * Otherwise an IllegalStateException is thrown.
     */
    @Override
    public void foregroundDispatch() {
        // Here add TechList to read the card
        // This functions how a filter to not read wrong data
        // if a new Technology is added in market
        // Added here and the file ../res/xml/tech_list_nfc.xml
        String[][] techList = new String[][]{};
        // Enable Foreground to read when user activated the nfc
        // and return to Activity, so the app is observing until reading
        try {
            // Depend how called this, is controller. For example: if called an Fragment
            // not support the Cast. Test in different context
            mNfcAdapter.enableForegroundDispatch((Activity) context, pendingIntent, null, techList);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    /**
     * Disable Foreground nfc adapter to don't see more other nfc files
     */
    @Override
    public void disableForegroundDispatch() {
        try {
            // Disable ForegroundDispatch to not read in foreground
            // It's attach with a Activity, with any other, nfc not execute
            mNfcAdapter.disableForegroundDispatch((Activity) context);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    /**
     * Verify if the phone support Nfc
     * @return a true if support and a false if not
     */
    @Override
    public boolean haveNfcSupport() {
        return !(mNfcAdapter == null);
    }

    /**
     * Disable the nfc Controller
     */
    @Override
    public void disable() {
        myNfcController = null;
        mNfcAdapter = null;
    }
}
