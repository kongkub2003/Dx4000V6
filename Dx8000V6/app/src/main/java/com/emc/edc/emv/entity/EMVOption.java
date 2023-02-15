package com.emc.edc.emv.entity;

import android.os.Bundle;

import com.usdk.apiservice.aidl.emv.EMVData;

/**
 * EMV, the parameters of the method startEMV
 */
public class EMVOption {

    private Bundle option = new Bundle();

    private EMVOption(){}

    public static EMVOption create() {
        return new EMVOption();
    }

    /**
     * Application selection way
     * <br> 0 - PSE selection first and then AID selection;
     * <br> 1 - Only PSE selection;
     * <br> 2 - Only AID selection;
     * <br> 3 - Only PPSE selection;
     * <br> 4 - PPSE First, AID selection Second (Discover ZIP Mode)
     */
    public EMVOption flagPSE(byte flagPSE) {
        option.putByte(EMVData.FLAG_PSE, flagPSE);
        return this;
    }

    /** The flag of flash card recovery process, 0: Normal trade; 1:Single flash card recovery; 2:Global flash card recovery.*/
    public EMVOption flagRecovery(byte flagRecovery) {
        option.putByte(EMVData.FLAG_RECOVERY, flagRecovery);
        return this;
    }

    /** Whether to query IC card transaction logs */
    public EMVOption setFlagICCLog(boolean flagICCLog) {
        option.putBoolean(EMVData.FLAG_ICC_LOG, flagICCLog);
        return this;
    }

    /** Whether the callback of onAppSelect is executed when RF transaction, 0: the kernel calls back onAppSelect on condition; 1: the kernel mandatory callback onAppSelect.*/
    public EMVOption flagCtlAsCb(byte flagCtlAsCb) {
        option.putByte(EMVData.FLAG_CTL_AS_CB, flagCtlAsCb);
        return this;
    }

    /** Whether to execute the issuer script(Abandoned, please do not use) */
    public EMVOption flagExecuteIssuerScript(byte flagExecuteIssuerScript) {
        option.putByte(EMVData.FLAG_EXECUTE_ISSUER_SCRIPT, flagExecuteIssuerScript);
        return this;
    }

    public Bundle toBundle() {
        return option;
    }
}
