package com.cordova.mynfccontrollersample.visa;

import android.content.Context;
import android.util.Log;

import com.cordova.mynfccontrollersample.nfc.enums.CommandEnum;
import com.cordova.mynfccontrollersample.nfc.utils.CommandApdu;
import com.cordova.mynfccontrollersample.nfc.utils.TransformUtils;
import com.cordova.mynfccontrollersample.visa.enums.VisaTerminalEnum;
import com.visa.app.ttpkernel.ContactlessConfiguration;
import com.visa.app.ttpkernel.ContactlessKernel;
import com.visa.app.ttpkernel.ContactlessResult;
import com.visa.app.ttpkernel.NfcTransceiver;
import com.visa.app.ttpkernel.TtpOutcome;
import com.visa.app.ttpkernel.Version;
import com.visa.vac.tc.emvconverter.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VisaKernel implements IKernelTransaction<TerminalVisaValueMap> {
    private static final String TAG = VisaKernel.class.getSimpleName();

    private final Context context;
    private static IKernelTransaction<TerminalVisaValueMap> visaKernel;

    // VISA SDK
    private static ContactlessConfiguration contactlessConfiguration;
    private static ContactlessKernel contactlessKernel;

    // Array Candidates
    private static ArrayList<byte[]> listCandidateAid;


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
            listCandidateAid = new ArrayList<>();
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
        contactlessConfiguration.setTerminalData(terminalData);
    }

    @Override
    public void nextCandidate(int nextCandidate) {
        contactlessConfiguration = ContactlessConfiguration.getInstance();
        HashMap<String, byte[]> terminalMap = contactlessConfiguration.getTerminalData();
        terminalMap.put(VisaTerminalEnum.APPLICATION_IDENTIFIER_ADF.getTag(), listCandidateAid.get(nextCandidate));
    }

    /**
     * Set List Aid Candidates that they're unique to use in this app
     * @param aidCandidates is a dynamic String array that the app set when configure the terminal
     */
    @Override
    public void setAidListCandidate(String... aidCandidates) {
        for (String candidate: aidCandidates) {
            listCandidateAid.add(TransformUtils.hexStringToByteArray(candidate));
        }
    }

    @Override
    public void doTransaction(NfcTransceiver nfcTransceiver) {
        boolean continueSelection = true;
//        int indexCandidate = 0;
        int indexCandidate = 0;
        int limitCandidate = listCandidateAid.size();
        ContactlessResult contactlessResult = null;
//        nfcTransceiver.transceive(new CommandApdu(CommandEnum.SELECT, CommandApdu.PPSE, 0).getBytes());
        while (continueSelection) {
            /*
            byte[] aidConsultResponse = nfcTransceiver.transceive(new CommandApdu(CommandEnum.SELECT,
                    listCandidateAid.get(indexCandidate),
                    0).getBytes());
            Log.d(TAG, "doTransaction: Aid Cuestion"  + TransformUtils.byteArrayToHexString(aidConsultResponse));
            Log.d(TAG, "doTransaction: index" + indexCandidate );
            Log.d(TAG, "doTransaction: total" + limitCandidate);
            if (aidConsultResponse[0] == (byte) 0x6A && aidConsultResponse[1] == (byte) 0x82) {
                if (indexCandidate < limitCandidate) {
                    nextCandidate(indexCandidate);
                    continueSelection = true;
                } else {
                    continueSelection = false;
                }
                indexCandidate++;
            } else {
                continueSelection = false;
            }
             */

            // TEST
            contactlessConfiguration = ContactlessConfiguration.getInstance();
            HashMap<String, byte[]> terminalMap = contactlessConfiguration.getTerminalData();
            terminalMap.put(VisaTerminalEnum.APPLICATION_IDENTIFIER_ADF.getTag(), listCandidateAid.get(indexCandidate));
            contactlessConfiguration.setTerminalData(terminalMap);

            contactlessResult = contactlessKernel
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
                    if (indexCandidate < limitCandidate) {
                        nextCandidate(indexCandidate);
                        continueSelection = true;
                        Log.d(TAG, "doTransaction: Continue Try, index" + indexCandidate);
                        Log.d(TAG, "doTransaction: Limit" + limitCandidate);
                        indexCandidate++;
                    } else {
                        continueSelection = false;
                        Log.d(TAG, "doTransaction: Over Limit" );
                    }
                    break;
                case SELECTAGAIN:
                    Log.d(TAG, "doTransaction: select again");
                    break;
            }

            if (TtpOutcome.TRYNEXT != outcome) {
                Log.d(TAG, "doTransaction: Find AID");
                continueSelection = false;
            }
            if (indexCandidate == limitCandidate) {
                Log.d(TAG, "doTransaction: Not found AID");
                continueSelection = false;
            }
        }
        Log.d(TAG, "doTransaction: Finish index" + indexCandidate);

        /*
        contactlessConfiguration = ContactlessConfiguration.getInstance();
        HashMap<String, byte[]> terminalMap = contactlessConfiguration.getTerminalData();
        terminalMap.put(VisaTerminalEnum.APPLICATION_IDENTIFIER_ADF.getTag(), listCandidateAid.get(indexCandidate));
        contactlessConfiguration.setTerminalData(terminalMap);

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

         */
        HashMap<String, byte[]> cardData = contactlessResult.getData();
        for (Map.Entry<String, byte[]> entry: cardData.entrySet()) {
            String key = entry.getKey();
            if (entry.getValue() != null) {
                String value = Utils.getHexString(entry.getValue());
                Log.d(TAG, "doTransaction: Card Data key ->" + key);
                Log.d(TAG, "doTransaction: Card Data value->" + value);
            }
        }
        HashMap<String, byte[]> internalData = contactlessResult.getInternalData();
        for (Map.Entry<String, byte[]> data : internalData.entrySet()) {
            if (data.getValue() != null) {
                String key = data.getKey();
                String value = Utils.getHexString(data.getValue());
                Log.d(TAG, "doTransaction: internal Data key->" + key);
                Log.d(TAG, "doTransaction: internal Data value->" + value);
            }
        }
        if (contactlessResult.getLastApdu() != null &&
                contactlessResult.getLastSW() != null) {
            String lastAdpu = Utils.getHexString(contactlessResult.getLastApdu());
            String lastSW = Utils.getHexString(contactlessResult.getLastSW());
            Log.d(TAG, "doTransaction: Last Adpu -> " + lastAdpu);
            Log.d(TAG, "doTransaction: Last SW -> " + lastSW);
        }



        nfcTransceiver.destroy();
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
