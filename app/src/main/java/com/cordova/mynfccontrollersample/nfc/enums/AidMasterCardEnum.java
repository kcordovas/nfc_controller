package com.cordova.mynfccontrollersample.nfc.enums;

public enum AidMasterCardEnum {
    MASTER_CARD_AID_ALL("A000000004"),
    MASTER_CARD_CREDIT_DEBIT_GLOBAL("A0000000041010"),
    MASTER_CARD_CREDIT("A00000000410101213"),
    MASTER_CARD_CREDIT_2("A00000000410101215"),
    MASTER_CARD_SPECIFIC("A0000000042010"),
    MASTER_CARD_MAESTRO_DEBIT("A0000000043060"),
    MASTER_CARD_MAESTRO_DEBIT_2("A000000004306001"),
    ;

    private final String aidValue;

    /**
     * Constructor to init the aidValue
     * @param aidValue is the hexadecimal array AID for mastercard
     */
    AidMasterCardEnum(String aidValue) {
        this.aidValue = aidValue;
    }

    /**
     * Get the AID Value in base to it
     * @return AID Value
     */
    public String getAidValue() {
        return aidValue;
    }
}
