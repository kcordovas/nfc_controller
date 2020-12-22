package com.cordova.mynfccontrollersample.nfc.controller;

import android.content.Context;

public interface INfcController {
    boolean isEnabled();
    boolean haveNfcSupport();
    void disable();
    void activate();
    void foregroundDispatch();
}
