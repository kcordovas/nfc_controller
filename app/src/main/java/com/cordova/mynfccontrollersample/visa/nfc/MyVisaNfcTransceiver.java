package com.cordova.mynfccontrollersample.visa.nfc;

import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.util.Log;

import com.cordova.mynfccontrollersample.nfc.enums.AidMasterCardEnum;
import com.cordova.mynfccontrollersample.nfc.enums.AidVisaEnum;
import com.cordova.mynfccontrollersample.nfc.enums.CommandEnum;
import com.cordova.mynfccontrollersample.nfc.utils.CommandApdu;
import com.cordova.mynfccontrollersample.nfc.utils.TransformUtils;
import com.visa.app.ttpkernel.NfcTransceiver;

import java.io.IOException;

public class MyVisaNfcTransceiver implements NfcTransceiver {
    private static final String TAG = MyVisaNfcTransceiver.class.getSimpleName();

    private IsoDep mIsoDep;

    public MyVisaNfcTransceiver (Tag tag) throws IOException {
        String[] techList = tag.getTechList();
        String searchTechList = IsoDep.class.getName();
        for (String techItem : techList) {
            if (searchTechList.trim().equals(techItem.trim())) mIsoDep = IsoDep.get(tag);
        }
        mIsoDep.connect();
    }

    @Override
    public byte[] transceive(byte[] txData) {
        Log.d(TAG, "transceive: Command" + TransformUtils.byteArrayToHexString(txData));
        CommandApdu commandApdu;
        byte[] result = new byte[0];
        try {
            // PPSE
            if (txData[0] == (byte)0x00 && txData[1] == (byte)0xA4 && txData[4] == (byte)0x0E) {
                commandApdu = new CommandApdu(CommandEnum.SELECT, CommandApdu.PPSE, 0);
                result = mIsoDep.transceive(commandApdu.getBytes());
            } else if (txData[0] == (byte)0x00 && txData[1] == (byte)0xA4) {
                // SELECT AID
                if ((txData[4] == (byte)0x07) || (txData[4] == (byte)0x08) || (txData[4] == (byte)0x09)) {
                    commandApdu = new CommandApdu(CommandEnum.SELECT,
                            TransformUtils.hexStringToByteArray(AidVisaEnum.VISA_DEBIT_CREDIT_CLASSIC.getAidValue()),
                            0);
//                    result = mIsoDep.transceive(commandApdu.getBytes());
                    result = mIsoDep.transceive(txData);
                }
            } else if (txData[0] == (byte)0x80 && txData[1] == (byte)0xA8 && txData[2] == (byte)0x00) {
                // GPO
                commandApdu = new CommandApdu(CommandEnum.GPO, new byte[]{(byte) 0x083, (byte) 0x00}, 0);
                result = mIsoDep.transceive(commandApdu.getBytes());
            }  else if (txData[0] == (byte)0x00 && txData[1] == (byte)0xB2) {
                //READ RECORD
                commandApdu = new CommandApdu(CommandEnum.READ_RECORD);
                result = mIsoDep.transceive(commandApdu.getBytes());
            }
        } catch (IOException e) { e.printStackTrace(); }

        Log.d(TAG, "transceive: Data ->" + TransformUtils.byteArrayToHexString(result));
        return result;
    }

    @Override
    public void destroy() {
        Log.d(TAG, "destroy: ");
        if (mIsoDep != null) {
            try {
                mIsoDep.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean isCardPresent() { return true; }
}
