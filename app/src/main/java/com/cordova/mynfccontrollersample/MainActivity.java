package com.cordova.mynfccontrollersample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.cordova.mynfccontrollersample.nfc.controller.INfcController;
import com.cordova.mynfccontrollersample.nfc.controller.INfcViewer;
import com.cordova.mynfccontrollersample.nfc.controller.MyNfcController;
import com.cordova.mynfccontrollersample.nfc.services.TimeOutNfcController;
import com.cordova.mynfccontrollersample.nfc.utils.TransformUtils;

public class MainActivity extends AppCompatActivity implements INfcViewer {
    private static final String TAG = MainActivity.class.getSimpleName();
    private INfcController myNfcController;
    private TextView textTest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textTest = findViewById(R.id.text_test);
        myNfcController = MyNfcController.getInstance(this, this);

        findViewById(R.id.button_nfc).setOnClickListener(v -> myNfcController.activate());
    }

    @Override
    protected void onResume() {
        super.onResume();
        myNfcController.foregroundDispatch();
        if (myNfcController.isEnabled()) {
            TimeOutNfcController timeOutNfcController = new TimeOutNfcController(2000);
            timeOutNfcController.start();
        }
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
        Log.d(TAG, "onNewIntent: " + intent);
        if (intent == null) return;
//        myNfcController.getData(intent);
    }

    // CALLBACK METHODS to nfc Controller
    @Override
    public void isLoadingNfcParser(boolean value) {
        Log.d(TAG, "isLoading: " + value);
    }

    @Override
    public void onResultNfcData(byte[] data) {
        this.runOnUiThread(() -> textTest.setText("Result: " + TransformUtils.byteArrayToHexString(data)));
    }

    @Override
    public void onErrorNfc(String message, Exception exception) {
        Log.e(TAG, "onErrorNfc: " + message, exception);
        this.runOnUiThread(() -> textTest.setText(message));
    }


}