package com.cordova.mynfccontrollersample.nfc.listener;

import android.nfc.Tag;

public interface INfcListener {
    void onResult(Tag tag);
    void onErrorNfc(Exception exception);
}
