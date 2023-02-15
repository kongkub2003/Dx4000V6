package com.emc.edc.constant;

import com.usdk.apiservice.aidl.pinpad.KeyAlgorithm;

public interface OnGuardConfig {

    String KEY_IPEK = "KEY_IPEK";
    String DEF_VALUE_IPEK = "11111111111111111111111111111111";

    String KEY_KSN = "KEY_KSN";
    String DEF_VALUE_KSN = "00000000000000400000";

    String KEY_REGION_ID = "KEY_REGION_ID";
    int DEF_VALUE_REGION_ID = 0;

    String KEY_KAP_ID = "KEY_KAP_ID";
    int DEF_VALUE_KAP_ID = 0;

    String KEY_KEY_ID = "KEY_KEY_ID";
    int DEF_VALUE_KEY_ID = 0;

    String KEY_NB_BIN = "KEY_NB_BIN";
    int DEF_VALUE_NB_BIN = 6;
    String KEY_EXTERNAL_Flag = "KEY_EXTERNAL_Flag";
    boolean DEF_VALUE_EXTERNAL_FLAG = true;
    String KEY_ENC_PAN_END = "KEY_ENC_PAN_END";
    boolean DEF_VALUE_ENC_PAN_END = true;

    String KEY_KEY_ALGORITHM = "KEY_KEY_ALGORITHM";
    String DEF_VALUE_KEY_ALGORITHM = String.valueOf(KeyAlgorithm.KA_TDEA);

    String KEY_ENCRYPTION_MODEL = "KEY_ENCRYPTION_MODEL";
    /**
     * 1表示2009；2表示2017兼容，3表示2017
     */
    String DEF_VALUE_ENCRYPTION_MODEL = "1";
    String VALUE_ENCRYPTION_MODEL_2009 = "1";
    String VALUE_ENCRYPTION_MODEL_2017_COMP = "2";
    String VALUE_ENCRYPTION_MODEL_2017 = "3";

    String KEY_JOB_TYPE_IS_EMV = "KEY_JOB_TYPE_IS_EMV";
    boolean DEF_VALUE_JOB_TYPE_IS_EMV = false;
}