package com.cordova.mynfccontrollersample.nfc.utils;

/**
 * Utils Class, it used to transform Binary, Hexadecimal, etc.
 * @author Kevin Cordova
 */
public class TransformUtils {

    /**
     * Method to transform byte to Hexadecimal
     * @param numByte is a SINGLE byte number
     * @return String transform of a byte to Hexadecimal
     */
    public static String byteSingleToHex(byte numByte) {
        char[] hexDigits = new char[2];
        hexDigits[0] = Character.forDigit((numByte >> 4) & 0xF, 16);
        hexDigits[1] = Character.forDigit((numByte & 0xF), 16);
        return new String(hexDigits);
    }

    /**
     * Utility class to convert a byte array to a hexadecimal string.
     *
     * @param bytes Bytes to convert
     * @return String, containing hexadecimal representation.
     */
    public static String byteArrayToHexString(byte[] bytes) {
        final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for ( int j = 0; j < bytes.length; j++ ) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * Utility class to convert a hexadecimal string to a byte string.
     *
     * <p>Behavior with input strings containing non-hexadecimal characters is undefined.
     *
     * @param s String containing hexadecimal characters to convert
     * @return Byte array generated from input
     */
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
}
