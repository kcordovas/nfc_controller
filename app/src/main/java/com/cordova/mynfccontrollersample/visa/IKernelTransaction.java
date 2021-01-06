package com.cordova.mynfccontrollersample.visa;

import android.content.Context;

import com.cordova.mynfccontrollersample.nfc.enums.AidVisaEnum;
import com.visa.app.ttpkernel.NfcTransceiver;

public interface IKernelTransaction<T> {
    byte[] getVersion();
    void doTransaction(NfcTransceiver nfcTransceiver);
    void settingTerminalData(T... value);
    void nextCandidate(int indexCandidate);
    void setAidListCandidate(String... aidCandidates);
}
