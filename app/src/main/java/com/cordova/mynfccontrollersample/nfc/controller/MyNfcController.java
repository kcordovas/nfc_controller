package com.cordova.mynfccontrollersample.nfc.controller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.cordova.mynfccontrollersample.nfc.enums.CommandEnum;
import com.cordova.mynfccontrollersample.nfc.listener.INfcListener;
import com.cordova.mynfccontrollersample.nfc.parser.MyParserTag;
import com.cordova.mynfccontrollersample.nfc.utils.CommandApdu;
import com.cordova.mynfccontrollersample.nfc.utils.TransformUtils;

import java.io.IOException;

/**
 * This Class is to control on NfcAdapter
 * it's use to not repeat coding
 * @author Kevin Córdova
 */
public class MyNfcController implements INfcController {
    private final Context context;
    private static PendingIntent pendingIntent;
    private static NfcAdapter mNfcAdapter;
    @SuppressLint("StaticFieldLeak")
    private static MyNfcController myNfcController;

    private INfcListener nfcListener;

    /**
     * Constructor
     * @param context is who use the NfcController
     * @param listener is who listen on the result of Nfc Controller
     */
    public MyNfcController(Context context, INfcListener listener) {
        this.context = context;
        this.nfcListener = listener;
    }

    /**
     * Singleton Design Pattern to not create the NfcAdapter sometimes
     * @param context is the Activity or Fragment that called it
     * @return INfc controller interface that contains object MyNfcController
     */
    public static INfcController getInstance(Context context, INfcListener listener) {
        if (myNfcController == null) {
            mNfcAdapter = NfcAdapter.getDefaultAdapter(context);

            pendingIntent = PendingIntent.getActivity(context, 0,
                    // Create an Intent that instance the same Activity or Fragment to call yourself
                    new Intent(context, context.getClass())
                            // Added this Flag
                            // If set, the activity will not be launched if it is already running at the top of the history stack.
                    .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
            return new MyNfcController(context, listener);
        }
        return myNfcController;
    }


    /**
     * Method to get the intent when NFC read the card
     * First, detect the Tag type to not read other NFC formats.
     * Second, Transform intent in a Tag NFC (remember that is Tag only to work with Contacless Payment Card)
     * Then, With the IsoDep interface, taken the tag and with ADPU command get the Result in format TLV
     * @param intent is the result of NFC when finish the reading
     */
    @Override
    public void getData(Intent intent) {
        // Get that NFC type is
        final String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
            // Get the tag from intent
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            nfcListener.onResult(tag);
            /*
            // Set Command with PPSE standard, it's use in Contacless card
            CommandApdu commandApdu = new CommandApdu(CommandEnum.SELECT, CommandApdu.PPSE, 0);
            MyParserTag parserTag = new MyParserTag(commandApdu);
            // Send the tag
            parserTag.tag(tag);
            try {
                // Get the result parser
                byte[] result = parserTag.parser();
            } catch (IOException e) {
                nfcListener.onErrorNfc(e);
            }
             */
        }
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
    public void activate(String message) {
        final Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
        context.startActivity(intent);
        // Show a Toast to say for user that he'll activate the nfc
        if (message.isEmpty()) message = "Activate NFC here";
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
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
        } catch (Exception e) {
            nfcListener.onErrorNfc(e);
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
            nfcListener.onErrorNfc(e);
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
