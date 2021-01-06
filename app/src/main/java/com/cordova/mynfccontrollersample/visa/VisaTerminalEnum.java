package com.cordova.mynfccontrollersample.visa;

public enum VisaTerminalEnum {
    /**
     * AMOUNT AUTHORISED (NUMERIC)
     * Authorised amount of the transaction (excluding adjustments)
     * LENGTH MIN: 6
     * LENGTH MAX: 6
     * MANDATORY
     * EXAMPLE: 000000010000
     */
    AMOUNT_AUTHORISED("9F02"),
    /**
     * TERMINAL COUNTRY CODE
     * Indicates the country of the terminal, represented according to ISO 3166
     * LENGTH MIN: 2
     * LENGTH MAX: 2
     * MANDATORY
     */
    TERMINAL_COUNTRY_CODE("9F1A"),
    /**
     * TRANSACTION CURRENCY CODE
     * Indicates the currency code of the transaction according to ISO 4217
     * LENGTH MIN: 2
     * LENGTH MAX: 2
     * MANDATORY
     * EXAMPLE: 0978
     */
    TRANSACTION_CURRENCY_CODE("5F2A"),
    /**
     * APPLICATION IDENTIFIER (ADF NAME)
     * The ADF Name identifies the application as described in [ISO 7816-5].
     * The AID is made up of the Registered Application Provider Identifier (RID) and the Proprietary Identifier Extension (PIX).
     * LENGTH MIN: 5
     * LENGTH MAX: 16
     * MANDATORY
     */
    APPLICATION_IDENTIFIER_ADF("4F"),
    /**
     * TRANSACTION TYPE
     * Indicates the type of financial transaction, represented by the first two digits of the ISO 8583:1987 Processing Code.
     * The actual values to be used for the Transaction Type data element are defined by the relevant payment system
     * LENGTH MIN: 1
     * LENGTH MAX: 1
     * MANDATORY
     * TYPES:
     * - Purchase -> 00
     * - Refund -> 20
     */
    TRANSACTION_TYPE("9C"),
    /**
     * TERMINAL TRANSACTION QUALIFIERS (TTQ)
     * Indicates reader capabilities, requirements, and preferences to the card.
     * TTQ byte 2 bits 8-7 are transient values, and reset to zero at the beginning of the transaction.
     * All other TTQ bits are static values, and not modified based on transaction conditions.
     * TTQ byte 3 bit 7 shall be set by the acquirer-merchant to 1b. (Refer to the "Contactless Tap to Phone Kernel Requirements")
     * LENGTH MIN: 4
     * LENGTH MAX: 4
     * MANDATORY
     */
    TERMINAL_TRANSACTION_QUALIFIERS_TTQ("9F66"),
    /**
     * MERCHANT NAME AND LOCATION
     * Indicates the name and location of the merchant.
     * LENGTH MIN: var
     * LENGTH MAX: var
     * MANDATORY
     */
    MERCHANT_NAME_AND_LOCATION("9F4E"),
    /**
     * TERMINAL TYPE
     * Indicates the environment of the terminal, its communications capability, and its operational control
     * Refer link to more info:
     * https://cert.api2.heartlandportico.com/Gateway/PorticoDevGuide/build/PorticoDeveloperGuide/PDL%20Response%20Table%2030%20-%20Terminal%20Data.html
     * LENGTH MIN: 1
     * LENGTH MIN: 1
     * UNMENTIONED BUT IF REQUIRED
     */
    TERMINAL_TYPE("9F35"),
    /**
     * READER CVM REQUIRED
     * Kernel proprietary data only (exists only in the Visa Kernel SDK namespace).
     * This tag uses a different namespace as in some cases the card may return the same tag.
     * The SDK is able to handle them as separate different data objects.
     * Refer to the “Contactless Tap to Phone Kernel Requirements” section in the latest version of the Visa Ready Tap to Phone mPOS Solutions
     * Requirements document for details.
     * Note: If cardholder verification is not supported, then the Reader CVM Required Limit may be set to the maximum value (999999999999).
     * OPTIONAL
     */
    READER_CVM_REQUIRED_LIMIT("DF01");

    private final String tag;
    VisaTerminalEnum(String tag) { this.tag = tag; }

    public String getTag() { return tag; }
}
