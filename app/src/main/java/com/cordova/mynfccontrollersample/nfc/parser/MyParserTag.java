package com.cordova.mynfccontrollersample.nfc.parser;

import android.nfc.Tag;
import android.nfc.tech.IsoDep;

import com.cordova.mynfccontrollersample.nfc.enums.CommandEnum;
import com.cordova.mynfccontrollersample.nfc.utils.CommandApdu;

import java.io.IOException;

/**
 * Class that receive the Tag of NFC controller and process data
 * @author Kevin Cordova
 */
public class MyParserTag implements IParserTag {
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
     * @param tag is the tag send by NFC controller
     */
    @Override
    public void tag(Tag tag) {
        isoDep = IsoDep.get(tag);
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
        return parseData;
    }
}
