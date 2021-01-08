package com.cordova.mynfccontrollersample.visa.enums;

public enum TransactionTypeEnum {
    PURCHASE(new byte[]{0x00}),
    REFUND(new byte[]{0x20});

    private final byte[] value;
    TransactionTypeEnum(byte[] bytes) {
        this.value = bytes;
    }

    public byte[] getValue() { return value; }
}
