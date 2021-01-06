package com.cordova.mynfccontrollersample.visa.enums;

/**
 * List City Code based on ISO 3166
 * Reference link: https://en.wikipedia.org/wiki/List_of_ISO_3166_country_codes
 * @author Kevin Cordova
 */
public enum CityCodeEnum {
    ECUADOR(new byte[]{(byte) 0x02, (byte) 0x18}),
    COLOMBIA(new byte[]{(byte) 0x01, (byte) 0x70});

    private final byte[] value;
    CityCodeEnum(byte[] bytes) {
        this.value = bytes;
    }

    public byte[] getValue() {
        return value;
    }
}
