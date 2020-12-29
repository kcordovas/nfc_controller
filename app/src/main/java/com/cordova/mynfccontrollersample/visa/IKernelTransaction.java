package com.cordova.mynfccontrollersample.visa;

import android.content.Context;

public interface IKernelTransaction {
    byte[] getVersion();
    void doTransaction();
}
