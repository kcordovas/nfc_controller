package com.cordova.mynfccontrollersample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cordova.mynfccontrollersample.nfc.controller.INfcController;
import com.cordova.mynfccontrollersample.nfc.controller.INfcViewer;
import com.cordova.mynfccontrollersample.nfc.controller.MyNfcController;
import com.cordova.mynfccontrollersample.nfc.utils.TransformUtils;
import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity implements INfcViewer, View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private INfcController myNfcController;

    private MaterialButton mButtonActivateNfc, mButtonReadCard;
    private RelativeLayout mLayoutProgressRead;
    private TextView mTextResult;
    private boolean isReadyToRead;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myNfcController = MyNfcController.getInstance(this, this);

        mButtonActivateNfc = findViewById(R.id.button_nfc);
        mButtonReadCard = findViewById(R.id.button_read_card);
        mLayoutProgressRead = findViewById(R.id.r_layout_load_card);
        mTextResult = findViewById(R.id.text_result);

        this.isReadyToRead = false;
        mLayoutProgressRead.setVisibility(View.GONE);
        mButtonReadCard.setVisibility(View.GONE);
        mButtonActivateNfc.setVisibility(View.VISIBLE);
        mTextResult.setText("");

        mButtonActivateNfc.setOnClickListener(this);
        mButtonReadCard.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int idView = v.getId();
        if (idView == mButtonActivateNfc.getId()) {
            myNfcController.activate();
            mButtonActivateNfc.setVisibility(View.GONE);
            mButtonReadCard.setVisibility(View.VISIBLE);
        } else if (idView == mButtonReadCard.getId()) {
            isReadyToRead = true;
            mButtonReadCard.setVisibility(View.GONE);
            mLayoutProgressRead.setVisibility(View.VISIBLE);
        }
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
        mLayoutProgressRead.setVisibility(View.GONE);
        mButtonReadCard.setVisibility(View.GONE);
        mButtonActivateNfc.setVisibility(View.VISIBLE);
        mTextResult.setText("");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent == null) return;
        if (isReadyToRead)
            myNfcController.getData(intent);
    }
    // CALLBACK METHODS to nfc Controller

    @Override
    public void isLoadingNfcParser(boolean value) {
        Log.d(TAG, "isLoading: " + value);
    }

    @Override
    public void onResultNfcData(byte[] data) {
        mLayoutProgressRead.setVisibility(View.GONE);
        mButtonReadCard.setVisibility(View.GONE);
        mButtonActivateNfc.setVisibility(View.GONE);
        String result = TransformUtils.byteArrayToHexString(data);
        mTextResult.setText(result);
    }

    @Override
    public void onErrorNfc(String message, Exception exception) {
        Log.e(TAG, "onErrorNfc: " + message, exception);
    }

}