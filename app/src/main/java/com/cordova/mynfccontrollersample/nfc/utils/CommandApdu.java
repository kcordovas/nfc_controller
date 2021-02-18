package com.cordova.mynfccontrollersample.nfc.utils;

import com.cordova.mynfccontrollersample.nfc.enums.CommandEnum;

/**
 * Util Class to transform Enum and get byte array of a ADPU specify command
 * @author Kevin Cordova
 */
public class CommandApdu {

    // PSE directory "1PAY.SYS.DDF01"
    public static final byte[] PSE = "1PAY.SYS.DDF01".getBytes();
    // PPSE directory "2PAY.SYS.DDF01"
    public static final byte[] PPSE = "2PAY.SYS.DDF01".getBytes();

    // Class Byte
    protected int mCla = 0x00;
    // Instruction Byte
    protected int mIns = 0x00;
    // Parameter one
    protected int mP1 = 0x00;
    // Parameter two
    protected int mP2 = 0x00;
    // Data
    protected byte[] mData = new byte[0];
    // Block data Length
    protected int mLengthC = 0x00;
    // Response Length
    protected int mLengthE = 0x00;
    // Identify if is used the field Response Length
    protected boolean isLeUsed;

    /**
     * Constructor to add Data in Command ADPU
     * @param commandEnum is the Enum for send SELECT, GPO, etc.
     * @param data is the data, optional, to send
     * @param le is the response length
     */
    public CommandApdu(final CommandEnum commandEnum, final byte[] data, final int le) {
        this.mCla = commandEnum.getFieldCLA();
        this.mIns = commandEnum.getFieldINS();
        this.mP1 = commandEnum.getFieldParameterOne();
        this.mP2 = commandEnum.getFieldParameterTwo();
        this.mLengthC = data == null ? 0 : data.length;
        this.mData = data;
        this.mLengthE = le;
        this.isLeUsed = true;
    }

    /**
     * Constructor to set Command ADPU
     * @param commandEnum is Enum for send SELECT, GPO, etc.
     */
    public CommandApdu(final CommandEnum commandEnum) {
        this.mCla = commandEnum.getFieldCLA();
        this.mIns = commandEnum.getFieldINS();
        this.mP1 = commandEnum.getFieldParameterOne();
        this.mP2 = commandEnum.getFieldParameterTwo();
        this.isLeUsed = false;
    }

    /**
     * Constructor to set Command APDU, use when command requires P1 and P2 changeable
     * @param commandEnum is Enum that only use CLA and INS fields
     * @param p1 is Parameter one
     * @param p2 is Parameter two
     * @param le is the response length
     */
    public CommandApdu(final CommandEnum commandEnum, final int p1, final int p2, final int le) {
        this.mCla = commandEnum.getFieldCLA();
        this.mIns = commandEnum.getFieldINS();
        this.mP1 = p1;
        this.mP2 = p2;
        this.mLengthE = le;
        this.isLeUsed = true;
    }

    /**
     * Method uses to get byte array ADPU command
     * @return byte array
     */
    public byte[] getBytes() {
        int length = 4; // CLA, INS, P1, P2
        if (mData != null && mData.length != 0) {
            length += 1; // LC
            length += mData.length;
        }
        if (isLeUsed) length += 1;

        byte[] commandAdpu = new byte[length];
        int index = 0;
        commandAdpu[index] = (byte) mCla;
        commandAdpu[1] = (byte) mIns;
        commandAdpu[2] = (byte) mP1;
        commandAdpu[3] = (byte) mP2;
        index = 4;
        // Verify if data is send,
        if (mData != null && mData.length != 0) {
            commandAdpu[index] = (byte) mLengthC;
            index = 5;
            // Copy the data to the array
            System.arraycopy(mData, 0, commandAdpu, index, mData.length);
            index += mData.length;
        }
        if (isLeUsed) commandAdpu[index] += mLengthE; // Added Length E

        return commandAdpu;
    }
}
