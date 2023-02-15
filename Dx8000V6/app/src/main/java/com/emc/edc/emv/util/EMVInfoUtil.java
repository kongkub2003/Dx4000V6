package com.emc.edc.emv.util;

import com.usdk.apiservice.aidl.emv.ACType;
import com.usdk.apiservice.aidl.emv.CVMFlag;
import com.usdk.apiservice.aidl.emv.CVMMethod;
import com.usdk.apiservice.aidl.emv.CardRecord;
import com.usdk.apiservice.aidl.emv.CertType;
import com.usdk.apiservice.aidl.emv.EMVError;
import com.usdk.apiservice.aidl.emv.FinalData;
import com.usdk.apiservice.aidl.emv.FlowType;
import com.usdk.apiservice.aidl.emv.KernelID;
import com.usdk.apiservice.aidl.emv.TransData;

public class EMVInfoUtil {

    public static String getFinalSelectDesc(FinalData finalData) {
        return String.format("KernalID = %s, AID = %s", getKernelIDDesc(finalData.getKernelID()), BytesUtil.bytes2HexString(finalData.getAID()));
    }

    public static String getKernelIDDesc(byte kernelID) {
        String desc;
        switch (kernelID) {
            case KernelID.EMV:
                desc = "EMV";
                break;
            case KernelID.PBOC:
                desc = "PBOC";
                break;
            case KernelID.VISA:
                desc = "VISA";
                break;
            case KernelID.MASTER:
                desc = "MASTER";
                break;
            case KernelID.JCB:
                desc = "JCB";
                break;
            case KernelID.DISCOVER:
                desc = "DISCOVER";
                break;
            case KernelID.AMEX:
                desc = "AMEX";
                break;

            default:
                desc = "UNKNOWN TYPE";
                break;
        }
        return desc + String.format("[0x%02X]", kernelID);
    }

    public static String getRecordDataDesc(CardRecord recordData) {
        return String.format("Pan = %s, PanSn = %s", BytesUtil.bytes2HexString(recordData.getPan()), recordData.getPanSN());
    }

    public static String getCVMDataDesc(CVMMethod cvm) {
        String desc = getCVMDesc(cvm.getCVM());
        if (cvm.getCVM() == CVMFlag.EMV_CVMFLAG_CERTIFICATE) {
            desc += String.format("(Credential：%s, ID number：%s)", getCertTypeDesc(cvm.getCertType()), BytesUtil.bytes2HexString(cvm.getCertNo()));
        }
        return desc;
    }

    public static String getCVMDesc(byte cvm) {
        String desc;
        switch (cvm) {
            case CVMFlag.EMV_CVMFLAG_NOCVM:
                desc = "No CVM validation is required";
                break;
            case CVMFlag.EMV_CVMFLAG_OFFLINEPIN:
                desc = "Offline PIN";
                break;
            case CVMFlag.EMV_CVMFLAG_ONLINEPIN:
                desc = "Online PIN";
                break;
            case CVMFlag.EMV_CVMFLAG_SIGNATURE:
                desc = "Signature";
                break;
            case CVMFlag.EMV_CVMFLAG_OLPIN_SIGN:
                desc = "Online PIN plus signature";
                break;
            case CVMFlag.EMV_CVMFLAG_CDV:
                desc = "Consumer Device Verification(qVSDC/qPBOC)";
                break;
            case CVMFlag.EMV_CVMFLAG_CCV:
                desc = "Confirmation Code Verified(PayPass)";
                break;
            case CVMFlag.EMV_CVMFLAG_CERTIFICATE:
                desc = "Certificate verification";
                break;
            case CVMFlag.EMV_CVMFLAG_ECASHPIN:
                desc = "Electronic cash deposit PIN";
                break;
            default:
                desc = "Unknown type";
        }
        return desc + String.format("[0x%02X]", cvm);
    }

    public static String getCertTypeDesc(byte certType) {
        String desc;
        switch (certType) {
            case CertType.PERSON_ID:
                desc = "identity card";
                break;
            case CertType.MILITARY_ID:
                desc = "Officer's ID";
                break;
            case CertType.PASSPORT:
                desc = "Passport";
                break;
            case CertType.ENTRY_PERMIT:
                desc = "Entry permit";
                break;
            case CertType.TEMP_ID:
                desc = "Temporary IDENTITY CARD";
                break;
            case CertType.OTHER:
                desc = "Other";
                break;
            default:
                desc = "Unknown type";
                break;
        }
        return desc + String.format("[0x%02X]", certType);
    }

    public static String getACTypeDesc(byte acType) {
        String desc;
        switch (acType) {
            case ACType.EMV_ACTION_AAC:
                desc = "AAC";
                break;
            case ACType.EMV_ACTION_ARQC:
                desc = "ARQC";
                break;
            case ACType.EMV_ACTION_TC:
                desc = "TC";
                break;
            default:
                desc = "Unknown type";
                break;
        }
        return desc + String.format("[0x%02X]", acType);
    }

    public static String getTransDataDesc(TransData transData) {
        return String.format("AcType = %s, CVM = %s, flowType = %s", getACTypeDesc(transData.getACType()), getCVMDesc(transData.getCVM()), getFlowTypeDesc(transData.getFlowType()));
    }

    public static String getFlowTypeDesc(byte flowType) {
        String desc;
        switch (flowType) {
            case FlowType.EMV_FLOWTYPE_EMV:
                desc = "EMV";
                break;
            case FlowType.EMV_FLOWTYPE_ECASH:
                desc = "ECASH";
                break;
            case FlowType.EMV_FLOWTYPE_QPBOC:
                desc = "QPBOC";
                break;
            case FlowType.EMV_FLOWTYPE_QVSDC:
                desc = "QVSDC";
                break;
            case FlowType.EMV_FLOWTYPE_PBOC_CTLESS:
                desc = "PBOC_CTLESS";
                break;
            case FlowType.EMV_FLOWTYPE_M_CHIP:
                desc = "M_CHIP";
                break;
            case FlowType.EMV_FLOWTYPE_M_STRIPE:
                desc = "M_STRIPE";
                break;
            case FlowType.EMV_FLOWTYPE_MSD:
                desc = "MSD";
                break;
            case FlowType.EMV_FLOWTYPE_MSD_LEGACY:
                desc = "MSD_LEGACY";
                break;
            case FlowType.EMV_FLOWTYPE_WAVE2:
                desc = "WAVE2";
                break;

            default:
                desc = "Unknown process";
        }
        return desc + String.format("[0x%02X]", flowType);
    }

    public static String getErrorMessage(int error) {
        String message;
        switch (error) {
            case EMVError.ERROR_POWERUP_FAIL: message = "ERROR_POWERUP_FAIL"; break;
            case EMVError.ERROR_ACTIVATE_FAIL: message = "ERROR_ACTIVATE_FAIL"; break;
            case EMVError.ERROR_WAITCARD_TIMEOUT: message = "ERROR_WAITCARD_TIMEOUT"; break;
            case EMVError.ERROR_NOT_START_PROCESS: message = "ERROR_NOT_START_PROCESS"; break;
            case EMVError.ERROR_PARAMERR: message = "ERROR_PARAMERR"; break;
            case EMVError.ERROR_MULTIERR: message = "ERROR_MULTIERR"; break;
            case EMVError.ERROR_CARD_NOT_SUPPORT: message = "ERROR_CARD_NOT_SUPPORT"; break;

            case EMVError.ERROR_EMV_RESULT_BUSY: message = "ERROR_EMV_RESULT_BUSY"; break;
            case EMVError.ERROR_EMV_RESULT_NOAPP: message = "ERROR_EMV_RESULT_NOAPP"; break;
            case EMVError.ERROR_EMV_RESULT_NOPUBKEY: message = "ERROR_EMV_RESULT_NOPUBKEY"; break;
            case EMVError.ERROR_EMV_RESULT_EXPIRY: message = "ERROR_EMV_RESULT_EXPIRY"; break;
            case EMVError.ERROR_EMV_RESULT_FLASHCARD: message = "ERROR_EMV_RESULT_FLASHCARD"; break;
            case EMVError.ERROR_EMV_RESULT_STOP: message = "ERROR_EMV_RESULT_STOP"; break;
            case EMVError.ERROR_EMV_RESULT_REPOWERICC: message = "ERROR_EMV_RESULT_REPOWERICC"; break;
            case EMVError.ERROR_EMV_RESULT_REFUSESERVICE: message = "ERROR_EMV_RESULT_REFUSESERVICE"; break;
            case EMVError.ERROR_EMV_RESULT_CARDLOCK: message = "ERROR_EMV_RESULT_CARDLOCK"; break;
            case EMVError.ERROR_EMV_RESULT_APPLOCK: message = "ERROR_EMV_RESULT_APPLOCK"; break;
            case EMVError.ERROR_EMV_RESULT_EXCEED_CTLMT: message = "ERROR_EMV_RESULT_EXCEED_CTLMT"; break;
            case EMVError.ERROR_EMV_RESULT_APDU_ERROR: message = "ERROR_EMV_RESULT_APDU_ERROR"; break;
            case EMVError.ERROR_EMV_RESULT_APDU_STATUS_ERROR: message = "ERROR_EMV_RESULT_APDU_STATUS_ERROR"; break;
            case EMVError.ERROR_EMV_RESULT_ALL_FLASH_CARD: message = "ERROR_EMV_RESULT_ALL_FLASH_CARD"; break;
            case EMVError.EMV_RESULT_AMOUNT_EMPTY: message = "EMV_RESULT_AMOUNT_EMPTY"; break;

            default:
                message = "unknow error";
        }
        return message + String.format("[0x%02X]", error);
    }
}
