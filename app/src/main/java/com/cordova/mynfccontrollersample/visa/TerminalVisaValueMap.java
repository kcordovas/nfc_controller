package com.cordova.mynfccontrollersample.visa;

/**
 * Object class create to use in Visa Configuration Kernel
 * @author Kevin Cordova
 */
public class TerminalVisaValueMap {
    // Key is the TAG based on EMV standard
    private String key;
    // Is the byte array value that set user or the app
    private byte[] value;

    /**
     * Constructor to create a TLV, only use in Configuration Kernel Visa
     * @param key is the Tag that send to configuration
     * @param value is the byte array value
     */
    public TerminalVisaValueMap(String key, byte[] value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Getter to get Key of Object
     * @return Key
     */
    public String getKey() {
        return key;
    }

    /**
     * Setter the Key on Object
     * @param key is the Tag of the Configuration Kernel
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Getter the Value of Object
     * @return the value in byte arrays on Object
     */
    public byte[] getValue() {
        return value;
    }

    /**
     * Setter Value of Object
     * @param value is the Value to send to Configuration kernel
     */
    public void setValue(byte[] value) {
        this.value = value;
    }
}
