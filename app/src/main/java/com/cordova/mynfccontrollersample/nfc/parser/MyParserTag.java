package com.cordova.mynfccontrollersample.nfc.parser;

import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.util.Log;

import com.cordova.mynfccontrollersample.nfc.enums.AidMasterCardEnum;
import com.cordova.mynfccontrollersample.nfc.enums.AidVisaEnum;
import com.cordova.mynfccontrollersample.nfc.enums.CommandEnum;
import com.cordova.mynfccontrollersample.nfc.utils.CommandApdu;
import com.cordova.mynfccontrollersample.nfc.utils.TransformUtils;

import java.io.IOException;

/**
 * Class that receive the Tag of NFC controller and process data
 * @author Kevin Cordova
 */
public class MyParserTag implements IParserTag {
    private static final String TAG = MyParserTag.class.getSimpleName();
    // Iso Dep is the interface to interact with Tag
    private IsoDep isoDep;
    // This byte array is result of parser
    private byte[] parseData;
    // This byte array is the Adpu command
    private final byte[] arrayCommandAdpu;

    /**
     * Constructor to send the Adpu Command
     * @param commandApdu is Utils class ADPU command.
     */
    public MyParserTag(final CommandApdu commandApdu) {
        this.arrayCommandAdpu = commandApdu.getBytes();
    }

    /**
     * Set the Tag with the Iso Dep
     * Filter by only Iso Dep Tags
     * @param tag is the tag send by NFC controller
     */
    @Override
    public void tag(Tag tag) {
        String[] techList = tag.getTechList();
        String searchTechList = IsoDep.class.getName();
        for (String techItem : techList) {
            if (searchTechList.trim().equals(techItem.trim())) isoDep = IsoDep.get(tag);
        }
    }

    /**
     * Parsed data to interact with the Tag
     * @return byte array result when finish with interactive
     * @throws IOException is a exception when Iso Dep interface not connect
     */
    @Override
    public byte[] parser() throws IOException {
        isoDep.connect();
        parseData = isoDep.transceive(arrayCommandAdpu);
        Log.d(TAG, "parser: PPSE" + TransformUtils.byteArrayToHexString(parseData));
        CommandApdu commandApdu2 = new CommandApdu(CommandEnum.SELECT, TransformUtils.hexStringToByteArray(AidMasterCardEnum.MASTER_CARD_CREDIT_DEBIT_GLOBAL.getAidValue()), 0);
        byte[] selectId = isoDep.transceive(commandApdu2.getBytes());
        Log.d(TAG, "parser: SELECT AID" + TransformUtils.byteArrayToHexString(selectId));
        CommandApdu commandApdu = new CommandApdu(CommandEnum.GPO, new byte[]{(byte) 0x083, (byte) 0x00}, 0);
        byte[] gpoByte = isoDep.transceive(commandApdu.getBytes());
        Log.d(TAG, "parser: GPO" + TransformUtils.byteArrayToHexString(gpoByte));
        CommandApdu commandApdu3 = new CommandApdu(CommandEnum.READ_RECORD);
        byte[] readRecord = isoDep.transceive(commandApdu3.getBytes());
        Log.d(TAG, "parser: READ RECORD" + TransformUtils.byteArrayToHexString(readRecord));
        isoDep.close();
        return parseData;
    }

    /**
     * Verify if the result is a Visa Card
     * @return true if is Visa and false if not
     */
    @Override
    public boolean isVisaCard() {
        if (parseData == null) throw new NullPointerException();
        String res = TransformUtils.byteArrayToHexString(parseData);
        return res.contains(AidVisaEnum.VISA_ALL_AID.getAidValue()) &&
                !res.contains(AidVisaEnum.VISA_GP_CARD_MANAGER.getAidValue());
    }
}
