package com.cordova.mynfccontrollersample.visa;

import android.content.Context;
import android.util.Log;

import com.visa.app.ttpkernel.ContactlessConfiguration;
import com.visa.app.ttpkernel.ContactlessKernel;
import com.visa.app.ttpkernel.ContactlessResult;
import com.visa.app.ttpkernel.NfcTransceiver;
import com.visa.app.ttpkernel.TtpOutcome;
import com.visa.app.ttpkernel.Version;
import com.visa.vac.tc.emvconverter.Utils;

import java.util.HashMap;
import java.util.Map;

public class VisaKernel implements IKernelTransaction<TerminalVisaValueMap> {
    private static final String TAG = VisaKernel.class.getSimpleName();

    private final Context context;
    private static IKernelTransaction<TerminalVisaValueMap> visaKernel;

    // VISA SDK
    private static ContactlessConfiguration contactlessConfiguration;
    private static ContactlessKernel contactlessKernel;


    /**
     * Constructor to init the Context by Activity
     * @param context is the Activity that called to SDK
     */
    public VisaKernel(Context context) {
        this.context = context;
    }

    /**
     * Singleton Design Pattern to instance the init configuration of Visa Kernel
     * @param context is the Activity that called to SDK
     * @return the Visa Kernel Object
     */
    public static IKernelTransaction<TerminalVisaValueMap> getInstance(Context context) {
        if (visaKernel == null) {
            contactlessKernel = ContactlessKernel.getInstance(context);
            contactlessConfiguration = ContactlessConfiguration.getInstance();
            visaKernel = new VisaKernel(context);
        }
        return visaKernel;
    }

    /**
     * Method use to configure the Terminal in base parameters
     * that could to send the user or the app
     * @param terminalVisaValueMaps is a Object in format Key,Value to set TerminalData
     */
    @Override
    public void settingTerminalData(TerminalVisaValueMap... terminalVisaValueMaps) {
        HashMap<String, byte[]> terminalData = contactlessConfiguration.getTerminalData();
        for (TerminalVisaValueMap terminalValue: terminalVisaValueMaps) {
            terminalData.put(terminalValue.getKey(), terminalValue.getValue());
        }
        /*
        terminalData.put("9F02", new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06}); // set the amount
        terminalData.put("9F1A", new byte[]{0x08, 0x40}); // set terminal country code
        terminalData.put("5F2A", new byte[]{0x08, 0x40}); // set currency code
        terminalData.put("9F35", new byte[]{0x22}); //Terminal Type
        terminalData.put("9C", new byte[]{0x20}); //Transaction Type
        terminalData.put("9F66", new byte[]{(byte)0x20, (byte)0x80, (byte)0x40, (byte)0x00}); //TTQ
        terminalData.put("9F39", new byte[]{0x07});
        // SELECT PPSE ADPU command
        // Process PPSE Response
        terminalData.put("4F", new byte[]{(byte)0xA0, 0x00, 0x00, 0x00, 0x03, 0x10, 0x10}); // set the selected aid
         */
        contactlessConfiguration.setTerminalData(terminalData);
    }

    @Override
    public void doTransaction(NfcTransceiver nfcTransceiver) {
        ContactlessResult contactlessResult = contactlessKernel
                .performTransaction(nfcTransceiver, contactlessConfiguration);

        TtpOutcome outcome = contactlessResult.getFinalOutcome();
        switch (outcome) {
            case COMPLETED:
                Log.d(TAG, "doTransaction: completed");
                break;
            case DECLINED:
                Log.d(TAG, "doTransaction: declined");
                break;
            case ABORTED:
                Log.d(TAG, "doTransaction: aborted");
                break;
            case TRYNEXT:
                Log.d(TAG, "doTransaction: trynext");
                break;
            case SELECTAGAIN:
                Log.d(TAG, "doTransaction: select again");
                break;
        }
        HashMap<String, byte[]> cardData = contactlessResult.getData();
        for (Map.Entry<String, byte[]> entry: cardData.entrySet()) {
            String key = entry.getKey();
            if (entry.getValue() != null) {
                String value = Utils.getHexString(entry.getValue());
                Log.d(TAG, "doTransaction: key ->" + key);
                Log.d(TAG, "doTransaction: value->" + value);
            }
        }
        HashMap<String, byte[]> internalData = contactlessResult.getInternalData();
        for (Map.Entry<String, byte[]> data : internalData.entrySet()) {
            if (data.getValue() != null) {
                String key = data.getKey();
                String value = Utils.getHexString(data.getValue());
                Log.d(TAG, "doTransaction: key->" + key);
                Log.d(TAG, "doTransaction: value->" + value);
            }
        }
        if (contactlessResult.getLastApdu() != null &&
                contactlessResult.getLastSW() != null) {
            String lastAdpu = Utils.getHexString(contactlessResult.getLastApdu());
            String lastSW = Utils.getHexString(contactlessResult.getLastSW());
            Log.d(TAG, "doTransaction: Last Adpu -> " + lastAdpu);
            Log.d(TAG, "doTransaction: Last SW -> " + lastSW);
        }

    }

    /**
     * Get Visa SDK Version
     * @return Version in byte format
     */
    @Override
    public byte[] getVersion() {
        return Version.getVersion();
    }
}
