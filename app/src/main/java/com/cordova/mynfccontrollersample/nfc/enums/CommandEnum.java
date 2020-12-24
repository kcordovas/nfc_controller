package com.cordova.mynfccontrollersample.nfc.enums;

public enum CommandEnum {
    /**
     * SELECT Command
     */
    SELECT(0x00, 0xA4, 0x04, 0x00),
    /**
     * READ RECORD Command
     */
    READ_RECORD(0x00, 0xB2, 0x01, 0x00),
    /**
     * GPO (GET PROCESSING OPTIONS) Command
     */
    GPO(0x80, 0xA8, 0x00, 0x00),
    /**
     * GET DATA Command
     */
    GET_DATA(0x80, 0xCA, 0x04, 0x00),
    /**
     * GET RESPONSE Command
     */
    GET_RESPONSE(0x00, 0x0C, 0x00, 0x00),
    ;

    // Class Byte
    private final int fieldCLA;
    // Instruction Byte
    private final int fieldINS;
    // Parameter one
    private final int fieldParameterOne;
    // Parameter two
    private final int fieldParameterTwo;

    /**
     * Enum Constructor
     * @param fieldCLA is class byte
     * @param fieldINS is instruction byte
     * @param fieldParameterOne is the parameter one
     * @param fieldParameterTwo is the parameter two
     */
    CommandEnum(int fieldCLA, int fieldINS, int fieldParameterOne, int fieldParameterTwo) {
        this.fieldCLA = fieldCLA;
        this.fieldINS = fieldINS;
        this.fieldParameterOne = fieldParameterOne;
        this.fieldParameterTwo = fieldParameterTwo;
    }

    /**
     * Method uses to get field CLA (Class byte)
     * @return CLA
     */
    public int getFieldCLA() {
        return fieldCLA;
    }

    /**
     * Method uses to get field Instruction byte
     * @return the fieldInstructionByte
     */
    public int getFieldINS() {
        return fieldINS;
    }

    /**
     * Method uses to get field Parameter ONE
     * @return the field Parameter One
     */
    public int getFieldParameterOne() {
        return fieldParameterOne;
    }

    /**
     * Method uses to get field Parameter TWO
     * @return the field Parameter Two
     */
    public int getFieldParameterTwo() {
        return fieldParameterTwo;
    }
}

