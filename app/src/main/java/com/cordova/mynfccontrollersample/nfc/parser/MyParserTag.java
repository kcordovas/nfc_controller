package com.cordova.mynfccontrollersample.nfc.parser;

import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.util.Log;

import com.cordova.mynfccontrollersample.mastercard.enums.AidMasterCardEnum;
import com.cordova.mynfccontrollersample.visa.enums.AidVisaEnum;
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
    // This byte array is the Adpu command
    private byte[] arrayCommandAdpu;

    /**
     * Constructor to send the Adpu Command
     * @param commandApdu is Utils class ADPU command.
     */
    public MyParserTag(final CommandApdu commandApdu) {
        this.arrayCommandAdpu = commandApdu.getBytes();
    }

    public MyParserTag(Tag tag) throws IOException {
        tag(tag);
    }

    /**
     * Set the Tag with the Iso Dep
     * Filter by only Iso Dep Tags
     * @param tag is the tag send by NFC controller
     */
    @Override
    public void tag(Tag tag) throws IOException {
        String[] techList = tag.getTechList();
        String searchTechList = IsoDep.class.getName();
        for (String techItem : techList) {
            if (searchTechList.trim().equals(techItem.trim())) isoDep = IsoDep.get(tag);
        }
        if (isoDep == null) return;
        isoDep.connect();
    }

    /**
     * TODO not use this method in production, only TEST
     * Parsed data to interact with the Tag
     * @return byte array result when finish with interactive
     * @throws IOException is a exception when Iso Dep interface not connect
     */
    @Override
    public byte[] parser() throws IOException {
        byte[] parseData = isoDep.transceive(arrayCommandAdpu);
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
        return parseData;
    }

    /**
     * Method use to interact the NFC with Card and response a byte array
     * Could to be a command to interact with response before
     * @param commandAdpu is a byte array command ADPU, e.g.: SELECT AID, GPO, READ RECORD, etc.
     * @return a byte array with response of card on command sent
     * @throws IOException is when the IsoDep interface is not enable to communication
     */
    @Override
    public byte[] transceive(byte[] commandAdpu) throws IOException {
        return isoDep.transceive(commandAdpu);
    }

    /**
     * Verify if a AID type is with the card
     * @param aidValue is a byte array that contain only AID, not ADPU command Complete, e.g.: "A0000000031010" (is in byte array type)
     * @return true if the AID is who is in the card, false if not
     * @throws IOException is when the IsoDep interface is not enable to communication
     */
    public boolean isAidSelected(byte[] aidValue) throws IOException {
        final CommandApdu commandApdu = new CommandApdu(CommandEnum.SELECT, aidValue, 0);
        byte[] res = isoDep.transceive(commandApdu.getBytes());
        return res[0] != (byte) 0x6A || res[1] != (byte) 0X82;
    }

    /**
     * Disconnect the IsoDep interface with Card
     * Apply this method in where require it
     * @throws IOException is when IsoDep interface is not enable to communication
     */
    @Override
    public void disconnect() throws IOException {
        if (isoDep == null) return;
        isoDep.close();
    }

    /*
    /**
     * Verify if the result is a Visa Card
     * @return true if is Visa and false if not
     */
    /*
    @Override
    public boolean isVisaCard() {
        if (parseData == null) throw new NullPointerException();
        String res = TransformUtils.byteArrayToHexString(parseData);
        return res.contains(AidVisaEnum.VISA_ALL_AID.getAidValue()) &&
                !res.contains(AidVisaEnum.VISA_GP_CARD_MANAGER.getAidValue());
    }

     */
}
