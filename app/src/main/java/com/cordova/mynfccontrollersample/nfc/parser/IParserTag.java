package com.cordova.mynfccontrollersample.nfc.parser;

import android.nfc.Tag;

import java.io.IOException;

public interface IParserTag {
    void tag(Tag tag) throws IOException;
    byte[] parser() throws IOException;
    void disconnect() throws IOException;
    byte[] transceive(byte[] commandAdpu) throws IOException;
}
