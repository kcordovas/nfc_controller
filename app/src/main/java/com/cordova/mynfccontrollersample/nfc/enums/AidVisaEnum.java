package com.cordova.mynfccontrollersample.nfc.enums;

/**
 * Enum class to create the AIDs to Visa International
 * only consider with EMV Standard Type
 * @author Kevin Cordova
 */
public enum AidVisaEnum {
    // AID VISA GENERAL
    VISA_ALL_AID("A000000003"),
    // VISA Debit/Credit (Classic)
    // Standard/Gold VISA credit card
    VISA_DEBIT_CREDIT_CLASSIC("A0000000031010"),
    // VISA Credit
    VISA_CREDIT("A000000003101001"),
    // VISA Debit
    VISA_DEBIT("A000000003101002"),
    // Visa Electron
    // VISA Electron (Debit)
    VISA_ELECTRON("A0000000032010"),
    // Visa Plus
    VISA_PLUS("A0000000038010"),
    // (VISA) CARD MANAGER
    // 	Used by most GP2.1.1 cards / Oberthur OP201 cards.
    // 	Visa Proprietary Card Manager AID for OpenPlatform cards (visa.openplatform).
    VISA_GP_CARD_MANAGER("A000000003000000"),
    ;

    // AID value is the hexadecimal array
    private final String aidValue;

    /**
     * Constructor to send hexadecimal array
     * @param aidValue is Hexadecimal array
     */
    AidVisaEnum(String aidValue) {
        this.aidValue = aidValue;
    }

    /**
     * Get the AID value
     * @return AID value
     */
    public String getAidValue() {
        return aidValue;
    }

}
