package com.cordova.mynfccontrollersample.visa.nfc;

import android.util.Log;

import com.cordova.mynfccontrollersample.nfc.parser.MyParserTag;
import com.cordova.mynfccontrollersample.nfc.enums.CommandEnum;
import com.cordova.mynfccontrollersample.nfc.utils.CommandApdu;
import com.cordova.mynfccontrollersample.nfc.utils.TransformUtils;
import com.visa.app.ttpkernel.NfcTransceiver;

import java.io.IOException;

public class MyVisaNfcTransceiver implements NfcTransceiver {
    private static final String TAG = MyVisaNfcTransceiver.class.getSimpleName();

    private final MyParserTag myParserTag;

    public MyVisaNfcTransceiver(MyParserTag myParserTag) {
        this.myParserTag = myParserTag;
    }

    @Override
    public byte[] transceive(byte[] txData) {
        Log.d(TAG, "transceive: Command " + TransformUtils.byteArrayToHexString(txData));
        CommandApdu commandApdu;
        byte[] result = new byte[0];
        try {
            // PPSE
            if (txData[0] == (byte)0x00 && txData[1] == (byte)0xA4 && txData[4] == (byte)0x0E) {
                commandApdu = new CommandApdu(CommandEnum.SELECT, CommandApdu.PPSE, 0);
                result = myParserTag.transceive(commandApdu.getBytes());
            } else if (txData[0] == (byte)0x00 && txData[1] == (byte)0xA4) {
                // SELECT AID
                if ((txData[4] == (byte)0x07) || (txData[4] == (byte)0x08) || (txData[4] == (byte)0x09)) {
                    result = myParserTag.transceive(txData);
                }
            } else if (txData[0] == (byte)0x80 && txData[1] == (byte)0xA8 && txData[2] == (byte)0x00) {
                // GPO
                result = myParserTag.transceive(txData);
            }  else if (txData[0] == (byte)0x00 && txData[1] == (byte)0xB2) {
                //READ RECORD
                result = myParserTag.transceive(txData);
            } else {
                // OTHER COMMANDS, e.g. GET DATA
                result = myParserTag.transceive(txData);
            }
        } catch (IOException e) { e.printStackTrace(); }

        Log.d(TAG, "transceive: Data -> " + TransformUtils.byteArrayToHexString(result));
        return result;
    }

    @Override
    public void destroy() {
        Log.d(TAG, "destroy: ");
        if (myParserTag == null) return;
        try {
            myParserTag.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isCardPresent() { return true; }
}
