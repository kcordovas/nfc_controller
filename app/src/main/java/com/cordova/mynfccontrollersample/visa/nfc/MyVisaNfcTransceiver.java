package com.cordova.mynfccontrollersample.visa.nfc;

import com.visa.app.ttpkernel.NfcTransceiver;

public class MyVisaNfcTransceiver implements NfcTransceiver {
    public MyVisaNfcTransceiver(byte[] resultData) {
        transceive(resultData);
    }

    @Override
    public byte[] transceive(byte[] bytes) {
        return bytes;
    }

    @Override
    public void destroy() {}

    @Override
    public boolean isCardPresent() {
        return true;
    }
}
