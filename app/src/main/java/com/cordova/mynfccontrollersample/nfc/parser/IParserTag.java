package com.cordova.mynfccontrollersample.nfc.parser;

import android.nfc.Tag;

import java.io.IOException;

public interface IParserTag {
    void tag(Tag tag);
    byte[] parser() throws IOException;
    boolean isVisaCard();
}
