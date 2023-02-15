package com.emc.edc.emv.entity;

/**
 * Drop down boxes the contents of each option
 */
public class SpinnerItem {
    int value;
    String name;
    String strValue;

    public SpinnerItem (int value, String name) {
        this.value = value;
        this.name = name;
    }

    public SpinnerItem (String value, String name) {
        this.strValue = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getStringValue() {
        return strValue;
    }

    @Override
    public String toString() {
        return name;
    }
}
