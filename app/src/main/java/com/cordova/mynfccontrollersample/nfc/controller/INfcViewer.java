package com.cordova.mynfccontrollersample.nfc.controller;

public interface INfcViewer {
    // Is loading is a callback method that
    // if is true is that is loading
    // if is in false is that finish with loading
    void isLoadingNfcParser(boolean value);
    // This method is to send result data to View
    void onResultNfcData(byte[] data);
    // This method is when a error is happened in NfcController
    void onErrorNfc(String message, Exception exception);
}
