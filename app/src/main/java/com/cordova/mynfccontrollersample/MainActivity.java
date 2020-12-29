package com.cordova.mynfccontrollersample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.cordova.mynfccontrollersample.nfc.controller.INfcController;
import com.cordova.mynfccontrollersample.nfc.controller.MyNfcController;
import com.cordova.mynfccontrollersample.nfc.listener.INfcListener;
import com.cordova.mynfccontrollersample.visa.IKernelTransaction;
import com.cordova.mynfccontrollersample.visa.TerminalVisaValueMap;
import com.cordova.mynfccontrollersample.visa.VisaKernel;
import com.cordova.mynfccontrollersample.visa.nfc.MyVisaNfcTransceiver;
import com.visa.app.ttpkernel.NfcTransceiver;

public class MainActivity extends AppCompatActivity implements INfcListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private INfcController myNfcController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myNfcController = MyNfcController.getInstance(this, this);

        findViewById(R.id.button_nfc).setOnClickListener(v -> myNfcController.activate());
    }

    @Override
    protected void onResume() {
        super.onResume();
        myNfcController.foregroundDispatch();
    }

    @Override
    protected void onPause() {
        myNfcController.disableForegroundDispatch();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myNfcController.disable();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent == null) return;
        myNfcController.getData(intent);
    }

    // NFC Listener
    @Override
    public void onResult(byte[] resultData) {
        IKernelTransaction<TerminalVisaValueMap> visaKernel = VisaKernel.getInstance(this);
        visaKernel.settingTerminalData(
                // Set the amount
                new TerminalVisaValueMap("9F02", new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06}),
                // Set terminal country code
                new TerminalVisaValueMap("9F1A", new byte[]{0x08, 0x40}),
                // Set currency code
                new TerminalVisaValueMap("5F2A", new byte[]{0x08, 0x40}),
                // Terminal type
                new TerminalVisaValueMap("9F35", new byte[]{0x22}),
                // Transaction type
                new TerminalVisaValueMap("9C", new byte[]{0x20}),
                // TTQ
                new TerminalVisaValueMap("9F66", new byte[]{(byte)0x20, (byte)0x80, (byte)0x40, (byte)0x00}),
                new TerminalVisaValueMap("9F39", new byte[]{0x07})
        );
        NfcTransceiver visaNfcTransceiver = new MyVisaNfcTransceiver(resultData);
        //visaKernel.doTransaction(visaNfcTransceiver);
    }

    @Override
    public void onErrorNfc(Exception exception) {
        Log.e(TAG, "onErrorNfc: " + exception.getMessage(), exception);
    }
}