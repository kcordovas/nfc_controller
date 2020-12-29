package com.cordova.mynfccontrollersample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.cordova.mynfccontrollersample.nfc.controller.INfcController;
import com.cordova.mynfccontrollersample.nfc.controller.INfcViewer;
import com.cordova.mynfccontrollersample.nfc.controller.MyNfcController;
import com.cordova.mynfccontrollersample.nfc.utils.TransformUtils;

public class MainActivity extends AppCompatActivity implements INfcViewer {
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

    // CALLBACK METHODS to nfc Controller
    @Override
    public void isLoadingNfcParser(boolean value) {
        Log.d(TAG, "isLoading: " + value);
    }

    @Override
    public void onResultNfcData(byte[] data) {
        Log.d(TAG, "onResultNfc: Result Data" + TransformUtils.byteArrayToHexString(data));
    }

    @Override
    public void onErrorNfc(String message, Exception exception) {
        Log.e(TAG, "onErrorNfc: " + message, exception);
    }


}