package com.cordova.mynfccontrollersample.nfc.controller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.cordova.mynfccontrollersample.nfc.enums.CommandEnum;
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
    private static final String TAG = MyNfcController.class.getSimpleName();
    private final Context context;
    private static PendingIntent pendingIntent;
    private static NfcAdapter mNfcAdapter;
    private MyParserTag parserTag;
    @SuppressLint("StaticFieldLeak")
    private static MyNfcController myNfcController;

    private static INfcViewer nfcListener;

    /**
     * Constructor
     * @param context is who use the NfcController
     */
    public MyNfcController(Context context) {
        this.context = context;
        if (isEnabled()) {
            final Bundle bundle = new Bundle();
            bundle.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 5000);
            mNfcAdapter.enableReaderMode((Activity) context, tag  -> {
                String[] techList = tag.getTechList();
                for (String tech: techList) Log.d(TAG, "MyNfcController: " + tech);

                CommandApdu commandApdu = new CommandApdu(CommandEnum.SELECT, CommandApdu.PPSE, 0);
                MyParserTag parserTag = new MyParserTag(commandApdu);
                parserTag.tag(tag);
                try {
                    byte[] result = parserTag.parser();
                    nfcListener.onResultNfcData(result);
//                    Log.d(TAG, "MyNfcController: Result " + TransformUtils.byteArrayToHexString(result));
                } catch (IOException e) {
                    e.printStackTrace();
                }
//            },NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK | NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS, bundle);
            },NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK, bundle);
        }
    }

    /**
     * Singleton Design Pattern to not create the NfcAdapter sometimes
     * @param context is the Activity or Fragment that called it
     * @return INfc controller interface that contains object MyNfcController
     */
    public static INfcController getInstance(Context context, INfcViewer iNfcViewer) {
        if (myNfcController == null) {
            mNfcAdapter = NfcAdapter.getDefaultAdapter(context);

            pendingIntent = PendingIntent.getActivity(context, 0,
                    // Create an Intent that instance the same Activity or Fragment to call yourself
                    new Intent(context, context.getClass())
                            // Added this Flag
                            // If set, the activity will not be launched if it is already running at the top of the history stack.
                    .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
            nfcListener = iNfcViewer;
            return new MyNfcController(context);
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
        nfcListener.isLoadingNfcParser(true);
        // Get that NFC type is
        final String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
            // Get the tag from intent
            // TEST Mastercard -> Contains IsoDep and NfcA
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            // Set Command with PPSE standard, it's use in Contacless card
            CommandApdu commandApdu = new CommandApdu(CommandEnum.SELECT, CommandApdu.PPSE, 0);
//            CommandApdu commandApdu = new CommandApdu(CommandEnum.SELECT);
            // Test this to read a mastercard Card
//            CommandApdu commandApdu = new CommandApdu(CommandEnum.SELECT,
//                    TransformUtils.hexStringToByteArray(AidMasterCardEnum.MASTER_CARD_CREDIT_DEBIT_GLOBAL.getAidValue()), 0);
            // Create a Parser and send the comand
            parserTag = new MyParserTag(commandApdu);
            // Send the tag
            parserTag.tag(tag);
            try {
                // Get the result parser
                byte[] result = parserTag.parser();
                nfcListener.onResultNfcData(result);
                nfcListener.isLoadingNfcParser(false);
            } catch (IOException e) {
                nfcListener.onErrorNfc(e.getMessage(), e);
            }
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
        nfcListener.isLoadingNfcParser(true);
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
            nfcListener.onErrorNfc(e.getMessage(), e);
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
            nfcListener.onErrorNfc(e.getMessage(), e);
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
        parserTag.cancel();
    }
}
