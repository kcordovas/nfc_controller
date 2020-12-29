package com.cordova.mynfccontrollersample.nfc.listener;

public interface INfcListener {
    void onResult(byte[] resultData);
    void onErrorNfc(Exception exception);
}
