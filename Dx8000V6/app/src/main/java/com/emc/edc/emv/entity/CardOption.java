package com.emc.edc.emv.entity;

import android.os.Bundle;

import com.usdk.apiservice.aidl.emv.EMVData;

/**
 * EMV Card-seeking related parameters
 * - the method searchCard supports all parameters.
 * - the method startPrcoess does not support Magnetic stripe cards, so the magnetic stripe card related parameters are not valid. Such as: supportMagCard, trackCheckEnabled, lrcCheckEnabled, ctrlFlagEnabled, trkIdWithWholeData
 */
public class CardOption {

    private Bundle option = new Bundle();

    private CardOption(){}

    public static CardOption create() {
        return new CardOption();
    }

    public CardOption supportMagCard(boolean supportMagCard) {
        option.putBoolean(EMVData.SUPPORT_MAG_CARD, supportMagCard);
        return this;
    }

    public CardOption supportICCard(boolean supportICCard) {
        option.putBoolean(EMVData.SUPPORT_IC_CARD, supportICCard);
        return this;
    }

    public CardOption supportRFCard(boolean supportRFCard) {
        option.putBoolean(EMVData.SUPPORT_RF_CARD, supportRFCard);
        return this;
    }

    public CardOption rfDeviceName(String rfDeviceName) {
        option.putString(EMVData.RF_DEVICE_NAME, rfDeviceName);
        return this;
    }

    public CardOption trackCheckEnabled(boolean trkDataCheck) {
        option.putBoolean(EMVData.TRACK_CHECK_ENABLED, trkDataCheck);
        return this;
    }

    public CardOption trkIdWithWholeData(int trkIdWithWholeData) {
        option.putInt(EMVData.TRKID_WITH_WHOLE_DATA, trkIdWithWholeData);
        return this;
    }

    public CardOption supportAllRFCardTypes(boolean supportAllRFCardTypes) {
        option.putBoolean(EMVData.SUPPORT_ALL_RF_CARD_TYPES, supportAllRFCardTypes);
        return this;
    }

    public CardOption loopSearchRFCard(boolean loopSearchRFCard) {
        option.putBoolean(EMVData.LOOP_SEARCH_RF_CARD, loopSearchRFCard);
        return this;
    }

    public Bundle toBundle() {
        return option;
    }
}
