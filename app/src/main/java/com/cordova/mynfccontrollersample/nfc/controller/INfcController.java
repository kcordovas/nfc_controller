package com.cordova.mynfccontrollersample.nfc.controller;

import android.content.Intent;

public interface INfcController {
    boolean isEnabled();
    boolean haveNfcSupport();
    void disable();
    void activate();
    void foregroundDispatch();
    void getData(Intent intent);
    void disableForegroundDispatch();
}
