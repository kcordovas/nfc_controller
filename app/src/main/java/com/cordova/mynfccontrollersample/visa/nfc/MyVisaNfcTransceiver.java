package com.cordova.mynfccontrollersample.visa.nfc;

import android.util.Log;

import com.cordova.mynfccontrollersample.nfc.utils.TransformUtils;
import com.visa.app.ttpkernel.NfcTransceiver;

public class MyVisaNfcTransceiver implements NfcTransceiver {
    private static final String TAG = MyVisaNfcTransceiver.class.getSimpleName();
    public MyVisaNfcTransceiver(byte[] resultData) {
        //transceive(resultData);
    }

    @Override
    public byte[] transceive(byte[] txData) {
        Log.d(TAG, "transceive: " + TransformUtils.byteArrayToHexString(txData));

        // TODO CHANGE with Real Data
        String res = "";
        // PPSE
        if (txData[0] == (byte)0x00 && txData[1] == (byte)0xA4 && txData[4] == (byte)0x0E) {
            res = "6F39840E325041592E5359532E4444463031A527BF0C2461224F07A000000003101050105649534120434F4E544143544C4553538701019F2A01039000";
        } else if (txData[0] == (byte)0x00 && txData[1] == (byte)0xA4 && (txData[4] == (byte)0x07) || (txData[4] == (byte)0x08) || (txData[4] == (byte)0x09)) {
            // SELECT AID
            res = "6F438407A0000000031010A53850105649534120434F4E544143544C4553539F38189F66049F02069F03069F1A0295055F2A029A039C019F3704BF0C089F5A0510084008409000";
        } else if (txData[0] == (byte)0x80 && txData[1] == (byte)0xA8 && txData[2] == (byte)0x00) {
            // GPO
            res = "7781918202002094040805050057104761731000000027D2412201190582545F20135649534120434445542033302F4341524430325F3401019F10201F220100A00000000000000000000000000000000000000000000000000000009F260896991101193D90D29F2701809F360200019F6C0200009F6E04207000009F7C0C010A434152440244474991159F5D060000000000009000";
        }

        return TransformUtils.hexStringToByteArray(res);
    }

    @Override
    public void destroy() {}

    @Override
    public boolean isCardPresent() {
        return true;
    }
}
