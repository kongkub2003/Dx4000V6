package com.emc.edc.emv

import android.os.Bundle
import android.os.RemoteException
import android.text.TextUtils
import android.util.Log
import androidx.annotation.CallSuper
import androidx.compose.runtime.MutableState
import androidx.navigation.NavController
import com.emc.edc.constant.DemoConfig
import com.emc.edc.emv.entity.CardOption
import com.emc.edc.emv.entity.EMVOption
import com.emc.edc.emv.util.EMVInfoUtil
import com.emc.edc.emv.util.EMVInfoUtil.getACTypeDesc
import com.emc.edc.emv.util.LogUtil
import com.emc.edc.emv.util.TLV
import com.emc.edc.emv.util.TLVList
import com.emc.edc.getCAPK
import com.emc.edc.getDataSetting
import com.emc.edc.utils.BytesUtil
import com.emc.edc.utils.Utils
import com.usdk.apiservice.aidl.constants.RFDeviceName
import com.usdk.apiservice.aidl.data.StringValue
import com.usdk.apiservice.aidl.emv.*
import com.usdk.apiservice.aidl.magreader.MagData
import com.usdk.apiservice.aidl.magreader.TrackID
import com.usdk.apiservice.aidl.pinpad.*
import org.json.JSONObject
import java.util.*


open class Emv(
    val jsonData: MutableState<JSONObject>? = null,
    private val hasError: MutableState<Boolean>? = null,
    private val errorMessage: MutableState<String>? = null,
    val navController: NavController? = null,
    private val seletAIDPopup: MutableState<Boolean>? = null,
    private val aidList: MutableList<String>? = null,
    private val aidOriginalList: MutableList<List<CandidateAID>>? = null,
    private val cardCanDoOnlineStatus: MutableState<Boolean>? = null,
    private val requestOnlineStatus: MutableState<Boolean>? = null,
    private val responseOnlineStatus: MutableState<Boolean>? = null,
    private val endProcessStatus: MutableState<Boolean>? = null,
    private val hasEMVStartAgain: MutableState<Boolean>? = null,
    private val hasEMVStartAgainConfirm: MutableState<Boolean>? = null,
) {
    private var emvOption = EMVOption.create()
    private var cardOption = CardOption.create()
    private var emv: UEMV? = DeviceHelper.me().emv;
    private var pinpad: UPinpad? = null
    private var lastCardRecord: CardRecord? = null
    private var wholeTrkId = 0
    private val emvProcessOptimization = false
    private var kernel = ""
    private val utils = Utils()


    @CallSuper //@Override
    protected fun onCreateView(savedInstanceState: Bundle?) {
        initDeviceInstance()
        //setContentView(R.layout.activity_emv);
        initCardOption()
    }
    private fun ByteArray.toHex(): String =
        joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }

    private fun String.decodeHex(): ByteArray {
        check(length % 2 == 0) { "Must have an even length" }

        return chunked(2)
            .map { it.toInt(16).toByte() }
            .toByteArray()
    }


    private fun initDeviceInstance() {
        emv = DeviceHelper.me().emv
        pinpad = DeviceHelper.me().getPinpad(
            KAPId(DemoConfig.REGION_ID, DemoConfig.KAP_NUM),
            KeySystem.KS_MKSK,
            DemoConfig.PINPAD_DEVICE_NAME
        )
    }

    private fun openPinpad() {
        try {
            pinpad!!.open()
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    private fun closePinpad() {
        try {
            pinpad!!.close()
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    private fun initCardOption() {
        val dataSetting = getDataSetting()
        setTrkIdWithWholeData(false, TrackID.TRK1)
        setTrkIdWithWholeData(false, TrackID.TRK2)
        setTrkIdWithWholeData(false, TrackID.TRK2)
        cardOption.supportICCard(dataSetting!!.allow_insert_card)
        cardOption.supportMagCard(dataSetting!!.allow_swipe_card)
        cardOption.supportRFCard(dataSetting!!.allow_pass_card)
        cardOption.supportAllRFCardTypes(false)
        cardOption.rfDeviceName(RFDeviceName.INNER)
        cardOption.trackCheckEnabled(false)
        /*CheckBox wholeTrack1CBox = bindViewById(R.id.wholeTrack1CBox);
		wholeTrack1CBox.setChecked(false);
		wholeTrack1CBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean isSlted) {
				setTrkIdWithWholeData(isSlted, TrackID.TRK1);
			}
		});
		CheckBox wholeTrack2CBox = bindViewById(R.id.wholeTrack2CBox);
		wholeTrack2CBox.setChecked(false);
		wholeTrack2CBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean isSlted) {
				setTrkIdWithWholeData(isSlted, TrackID.TRK2);
			}
		});
		CheckBox wholeTrack3CBox = bindViewById(R.id.wholeTrack3CBox);
		wholeTrack3CBox.setChecked(false);
		wholeTrack3CBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean isSlted) {
				setTrkIdWithWholeData(isSlted, TrackID.TRK3);
			}
		});

		CheckBox insertCBox = bindViewById(R.id.insertCBox);
		insertCBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean isSlted) {
				cardOption.supportICCard(isSlted);
			}
		});
		CheckBox passCBox = bindViewById(R.id.passCBox);
		passCBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean isSlted) {
				cardOption.supportRFCard(isSlted);
			}
		});
		CheckBox swipeCBox = bindViewById(R.id.swipeCBox);
		swipeCBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean isSlted) {
				cardOption.supportMagCard(isSlted);
			}
		});
		CheckBox allRFCardCBox = bindViewById(R.id.allRFCardCBox);
		allRFCardCBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean isSlted) {
				cardOption.supportAllRFCardTypes(isSlted);
			}
		});
		CheckBox loopSearchRFCard = bindViewById(R.id.loopSearchRFCard);
		loopSearchRFCard.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean isSlted) {
				cardOption.loopSearchRFCard(isSlted);
			}
		});*/
        //cardOption.rfDeviceName(DemoConfig.RF_DEVICE_NAME)
        //cardOption.trackCheckEnabled(false)
    }

    private fun setTrkIdWithWholeData(isSlted: Boolean, trkId: Int) {
        wholeTrkId = if (isSlted) {
            wholeTrkId or trkId
        } else {
            wholeTrkId and trkId.inv()
        }
        cardOption.trkIdWithWholeData(wholeTrkId)
    }

    private fun searchRFCard(next: Runnable) {
        //outputBlueText("******* search RF card *******");
        //outputRedText(getString(R.string.pass_card_again));
        val rfCardOption = CardOption.create()
            .supportICCard(true)
            .supportMagCard(false)
            .supportRFCard(true)
            .rfDeviceName(DemoConfig.RF_DEVICE_NAME)
            .toBundle()
        try {
            emv!!.searchCard(rfCardOption, DemoConfig.TIMEOUT, object : SearchListenerAdapter() {
                override fun onCardPass(cardType: Int) {
                    //outputText("=> onCardPass | cardType = " + cardType);
                    next.run()
                }

                override fun onTimeout() {
                    //outputRedText("=> onTimeout");
                    stopEMV()
                }

                override fun onError(code: Int, message: String) {
                    //outputRedText(String.format("=> onError | %s[0x%02X]", message, code));
                    stopEMV()
                    emv!!.stopSearch()
                    if (message == "Card timeout") {
                        Log.d("EMV Error",message)
                        //stopEMV()
                        startEMV()
                        "TODO()"
                    }
                }
            })
        } catch (e: Exception) {
            Log.d("EMV Error","Read Card Error Exception")
            //handleException(e);
        }
    }

    private fun searchCard(next: Runnable) {
        //outputBlueText("******  search card ******");
        //outputRedText(getString(R.string.insert_pass_swipe_card));

        try {
            val cardHandler = object : SearchCardListener.Stub() {
                    override fun onCardPass(cardType: Int) {
                        //outputText("=> onCardPass | cardType = " + cardType);
                        jsonData!!.value.put("operation","contactless")
                        jsonData.value.put("pos_entry_mode","050")
                        next.run()
                    }

                    override fun onCardInsert() {
                        //outputText("=> onCardInsert");
                        //operation!!.value = "contact"
                        jsonData!!.value.put("operation", "contact")
                        jsonData.value.put("pos_entry_mode","052")
                        next.run()
                    }

                    override fun onCardSwiped(track: Bundle?) {
                        val split_track1 = track!!.getString(MagData.TRACK1)!!.split("^")
                        jsonData!!.value.put("operation","magnetic")
                        jsonData.value.put("pos_entry_mode","022")
                       // operation!!.value = "magnetic"
                        jsonData!!.value.put("track3", track!!.getString("TRACK3"))
                        jsonData!!.value.put("card_number", track!!.getString("PAN"))
                        jsonData!!.value.put("track1", track.getString("TRACK1"))
                        jsonData!!.value.put("card_exp", track.getString("EXPIRED_DATE"))
                        jsonData!!.value.put("track2", track.getString("TRACK2"))
                        jsonData!!.value.put("Service code:", track.getString("SERVICE_CODE"))
                        val name = if (split_track1[1].contains("/")) {
                            val split_name = split_track1[1].split("/")
                            split_name[1].replace(" ", "") + " " + split_name[0].replace("\\", "")
                        } else {
                            split_track1[1].replace("\\s+".toRegex(), " ")
                        }
                        jsonData!!.value.put("name", name)
                        requestOnlineStatus!!.value = true
                        /*var loop = true
                        //Timer().schedule(2000){
                        while (loop) {
                            //if (cardCanDoOnlineStatus!!.value) {
                                if (responseOnlineStatus!!.value) {
                                    requestOnlineStatus!!.value = false
                                    responseOnlineStatus!!.value = false
                                    endProcessStatus!!.value = true
                                    loop = false
                                }
                            //}
                        }*/

                        /*val trackStates = track.getIntArray("TRACK_STATES")
                        for (i in trackStates!!.indices) {
                            //outputText(String.format("==> Track%s State：%d", i+1, trackStates[i]));
                        }*/
                        stopEMV()
                    }

                    override fun onTimeout() {
                        //outputRedText("=> onTimeout");
                        stopEMV()
                    }

                    override fun onError(code: Int, message: String) {
                        //outputRedText(String.format("=> onError | %s[0x%02X]", message, code));
                        if (message == "Card timeout") {
                            Log.d("EMV Error",message)
                            //stopEMV()
                            startEMV()
                            "TODO()"
                        } else {
                            stopEMV()
                        }
                    }
                }
            emv?.searchCard(
                cardOption.toBundle(),
                60,
                cardHandler
            )
        } catch (e: Exception) {
            Log.e("EMV SearchCardListener", e.message.toString())
            errorMessage!!.value = "${e.message}"
            hasError!!.value = true
        }
    }

    fun startEMV() {
        try {
            //outputBlueText("******  start EMV ******");
            //initDeviceInstance()
            //emv = DeviceHelper.me().emv
            initCardOption()
            Log.d("EMV", "Start EMV")

            //getKernelVersion();
            //getCheckSum();
            emv!!.startEMV(emvOption.toBundle(), emvEventHandler)
            //val ret = emv!!.startEMV(emvOption.flagPSE(0x00.toByte()).toBundle(), emvEventHandler)
            //outputResult(ret, "=> Start EMV")
            //openPinpad()
        } catch (e: Exception) {
            Log.d("EMV", "Start EMV Error")
            //handleException(e);
            errorMessage!!.value = "${e.message}"
            hasError!!.value = true
        }
    }

    private fun getKernelVersion() {
        try {
            val version = StringValue()
            val ret = emv!!.getKernelVersion(version)
            if (ret == EMVError.SUCCESS) {
                //outputBlackText("EMV kernel version: " + version.getData());
            } else {
                //outputRedText("EMV kernel version: fail, ret = " + ret);
            }
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    private fun getCheckSum() {
        try {
            val flag = 0xA2
            val checkSum = StringValue()
            val ret = emv!!.getCheckSum(flag, checkSum)
            if (ret == EMVError.SUCCESS) {
                //outputBlackText("EMV kernel[" + flag + "] checkSum: " + checkSum.getData());
            } else {
                //outputRedText("EMV kernel[" + flag + "] checkSum: fail, ret = " + ret);
            }
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    fun stopEMV() {
        try {
            emv!!.stopEMV()
            emv!!.stopSearch()
            emv!!.stopProcess()
            //closePinpad()
        } catch (e: Exception) {
            Log.e("test aa3a", e.message.toString())
            errorMessage!!.value = "${e.message}"
            hasError!!.value = true
        }
    }

    protected fun stopSearch() {
        try {
            //outputBlueText("******  stop Search ******");
            emv!!.stopSearch()
        } catch (e: Exception) {
            //handleException(e);
        }
    }

    private fun halt() {
        try {
            //outputBlueText("******  close RF device ******");
            emv!!.halt()
        } catch (e: Exception) {
            //handleException(e);
        }
    }

    private var emvEventHandler: EMVEventHandler = object : EMVEventHandler.Stub() {
        @Throws(RemoteException::class)
        override fun onInitEMV() {
            doInitEMV()
        }

        @Throws(RemoteException::class)
        override fun onWaitCard(flag: Int) {
            doWaitCard(flag)
        }

        @Throws(RemoteException::class)
        override fun onCardChecked(cardType: Int) {
            // Only happen when use startProcess()
            doCardChecked(cardType)
        }

        @Throws(RemoteException::class)
        override fun onAppSelect(reSelect: Boolean, list: List<CandidateAID>) {
            doAppSelect(reSelect, list)
        }

        @Throws(RemoteException::class)
        override fun onFinalSelect(finalData: FinalData) {
            doFinalSelect(finalData)
        }

        @Throws(RemoteException::class)
        override fun onReadRecord(cardRecord: CardRecord) {
            lastCardRecord = cardRecord
            doReadRecord(cardRecord)
        }

        @Throws(RemoteException::class)
        override fun onCardHolderVerify(cvmMethod: CVMMethod) {
            doCardHolderVerify(cvmMethod)
        }

        @Throws(RemoteException::class)
        override fun onOnlineProcess(transData: TransData?) {
            doOnlineProcess(transData)
        }

        @Throws(RemoteException::class)
        override fun onEndProcess(result: Int, transData: TransData) {
            doEndProcess(result, transData)
        }

        @Throws(RemoteException::class)
        override fun onVerifyOfflinePin(
            flag: Int,
            random: ByteArray,
            caPublicKey: CAPublicKey,
            offlinePinVerifyResult: OfflinePinVerifyResult
        ) {
            doVerifyOfflinePin(flag, random, caPublicKey, offlinePinVerifyResult)
        }

        @Throws(RemoteException::class)
        override fun onObtainData(ins: Int, data: ByteArray) {
            //outputText("=> onObtainData: instruction is 0x" + Integer.toHexString(ins) + ", data is " + BytesUtil.bytes2HexString(data));
        }

        @Throws(RemoteException::class)
        override fun onSendOut(ins: Int, data: ByteArray) {
            doSendOut(ins, data)
        }
    }

    @Throws(RemoteException::class)
    fun doInitEMV() {
        //outputText("=> onInitEMV ");
        manageAID()

        //  init transaction parameters，please refer to transaction parameters
        //  chapter about onInitEMV event in《UEMV develop guide》
        //  For example, if VISA is supported in the current transaction,
        //  the label: DEF_TAG_PSE_FLAG(M) must be set, as follows:
        emv!!.setTLV(KernelID.VISA, EMVTag.DEF_TAG_PSE_FLAG, "03")
        // For example, if AMEX is supported in the current transaction，
        // labels DEF_TAG_PSE_FLAG(M) and DEF_TAG_PPSE_6A82_TURNTO_AIDLIST(M) must be set, as follows：
        // emv.setTLV(KernelID.AMEX, EMVTag.DEF_TAG_PSE_FLAG, "03");
        // emv.setTLV(KernelID.AMEX, EMVTag.DEF_TAG_PPSE_6A82_TURNTO_AIDLIST, "01");
    }

    @Throws(RemoteException::class)
    protected fun manageAID() {
        //outputBlueText("****** manage AID ******");
        val aids = arrayOf(
            "A000000333010106",
            "A000000333010103",
            "A000000333010102",
            "A000000333010101",
            "A0000000651010",
            "A0000000043060",
            "A0000000041010",
            "A000000003101002",
            "A0000000031010"
        )
        for (aid in aids) {
            val ret = emv!!.manageAID(ActionFlag.ADD, aid, true)
            outputResult(ret, "=> add AID : $aid")
        }
    }

    @Throws(RemoteException::class)
    fun doWaitCard(flag: Int) {
        emv!!.stopSearch()
        when (flag) {
            WaitCardFlag.NORMAL -> searchCard(Runnable {
                if (emvProcessOptimization) {
                    return@Runnable
                }
                respondCard()
            })
            WaitCardFlag.ISS_SCRIPT_UPDATE, WaitCardFlag.SHOW_CARD_AGAIN -> searchRFCard { respondCard() }
            WaitCardFlag.EXECUTE_CDCVM -> emv!!.halt()
            else -> {}
        }
    }

    private fun respondCard() {
        try {
            emv!!.respondCard()
        } catch (e: RemoteException) {
            Log.e("test aa3a", e.message.toString())
            errorMessage!!.value = "${e.message}"
            hasError!!.value = true
        }
    }

    fun doCardChecked(cardType: Int) {
        // Only happen when use startProcess()
    }

    /**
     * Request cardholder to select application
     */
    fun doAppSelect(reSelect: Boolean, candList: List<CandidateAID>) {
        //outputText("=> onAppSelect: cand AID size = " + candList.size());
        if (candList.size > 1) {
            aidOriginalList!!.add(candList)
            for (candAid in candList) {
                aidList!!.add(String(candAid.appLabel))
            }
            seletAIDPopup!!.value = true
        } else {
            respondAID(candList[0].aid)
        }
    }


    fun selectedAID(select: Int, aidOriginalList: MutableList<List<CandidateAID>>) {
        respondAID(aidOriginalList[0][select].aid)
    }

    private fun respondAID(aid: ByteArray?) {
        try {
            //outputBlueText("Select aid: " + BytesUtil.bytes2HexString(aid));
            val tmAid = TLV.fromData(EMVTag.EMV_TAG_TM_AID, aid)
            emv!!.respondEvent(tmAid.toString())
        } catch (e: Exception) {
            Log.e("test aa3a", e.message.toString())
            errorMessage!!.value = "${e.message}"
            hasError!!.value = true
            //handleException(e);
        }
    }

    /**
     * Parameters can be set or adjusted according to the aid selected finally
     * please refer to transaction parameters chapter about onFinalSelect event in《UEMV develop guide》
     */
    @Throws(RemoteException::class)
    fun doFinalSelect(finalData: FinalData) {
        //outputText("=> onFinalSelect | " + EMVInfoUtil.getFinalSelectDesc(finalData));

        var amount =
            jsonData!!.value.getString("amount").replace(".", "").replace(",", "").padStart(12, '0')

        val currentDate = "${Date().year - 100}" +
                "${if (1 + Date().month < 10) "0${1 + Date().month}" else 1 + Date().month}" +
                "${if (Date().date < 10) "0${Date().date}" else Date().date}"
        val currentTime =
            Date().hours.toString().padStart(2, '0') + Date().minutes.toString()
                .padStart(2, '0') + Date().seconds.toString().padStart(2, '0')
        var tlvList = StringBuilder()

        when (finalData.kernelID) {
            KernelID.EMV.toByte() -> {              // Parameter settings, see transaction parameters of EMV Contact Level 2 in《UEMV develop guide》
                // For reference only below
                if (amount != "") {
                    tlvList
                        .append(
                            EMVTag.EMV_TAG_TM_AUTHAMNTN + (amount.length / 2).toString()
                                .padStart(2, '0') + amount
                        )
                } else {
                    tlvList
                        .append(
                            EMVTag.EMV_TAG_TM_AUTHAMNTN + "0" // need to edit
                                .padStart(2, '0') + amount
                        )
                }
                .append(
                    EMVTag.EMV_TAG_TM_TRANSDATE + (currentDate.length / 2).toString()
                        .padStart(2, '0') + currentDate
                )
                    .append(
                        EMVTag.EMV_TAG_TM_TRANSTIME + (currentTime.length / 2).toString()
                            .padStart(2, '0') + currentTime
                    )
                    .append(EMVTag.EMV_TAG_TM_CNTRYCODE + "0764")
                    .append(EMVTag.EMV_TAG_TM_CURCODE + "0764")
                    .append(EMVTag.EMV_TAG_TM_TRANSTYPE + "22")
                    .append(EMVTag.EMV_TAG_TM_TERMTYPE + "22")
                    .append(EMVTag.EMV_TAG_TM_FLOORLMT + "10000000")
                    .append(EMVTag.DEF_TAG_TAC_DECLINE + "0000000000")
                    .append(EMVTag.DEF_TAG_TAC_ONLINE + "0000000000")
                    .append(EMVTag.DEF_TAG_TAC_DEFAULT + "0000000000")
                    .append(EMVTag.DEF_TAG_RAND_SLT_THRESHOLD + "00000001")
                    .append(EMVTag.DEF_TAG_RAND_SLT_PER + "01")
                    .append(EMVTag.DEF_TAG_RAND_SLT_MAXPER + "90")
                    .append(EMVTag.EMV_TAG_TM_CAP + "E0B8C8")
                    .append(EMVTag.EMV_TAG_TM_CAP_AD + "E080F0A001")
                    .append(EMVTag.DEF_TAG_RAND_SLT_THRESHOLD + "04"+"00000000")
                    .append(EMVTag.DEF_TAG_RAND_SLT_PER +"01"+ "01")
                    //.append(EMVTag.DEF_TAG_GAC_CONTROL + "02"+"0102")




                /*tlvList.append(
                    "9F02060000000001009F03060000000000009A031710209F21031505129F410400000001" +
                            "9F3501229F3303E0F8C89F40056000F0A0019F1A0201565F2A0201569C0100"
                )*/
                // Parameter settings, see transaction parameters of EMV Contact Level 2 in《UEMV develop guide》
                // For reference only below
                    .append(
                    "9F02060000000001009F03060000000000009A031710209F21031505129F410400000001" +
                            "9F3501229F3303E0F8C89F40056000F0A0019F1A0201565F2A0201569C0100" +
                            "DF9181040100DF91810C0130DF91810E0190")
            }
            KernelID.PBOC.toByte() ->                // if suport PBOC Ecash，see transaction parameters of PBOC Ecash in《UEMV develop guide》.
                // If support qPBOC, see transaction parameters of QuickPass in《UEMV develop guide》.
                // For reference only below
                tlvList.append("9F02060000000001009F03060000000000009A031710209F21031505129F4104000000019F660427004080")
            KernelID.VISA.toByte() -> {
                kernel = "visa"
                // Parameter settings, see transaction parameters of PAYWAVE in《UEMV develop guide》.

                tlvList
                    .append("9C0100")
                    .append("9F0206000000000100")
                    .append("9A03171020")
                    .append("9F2103150512")
                    .append("9F410400000001")
                    .append("9F350122")
                    .append("9F1A020156")
                    .append("5F2A020156")
                    .append("9F1B0400003A98")
                    .append("9F660436004000")
                    .append("DF06027C00")
                    .append("DF812406000000100000")
                    .append("DF812306000000100000")
                    .append("DF812606000000100000")
                    .append("DF918165050100000000")
                    .append("DF040102")
                    .append("DF810602C000")
                    .append("DF9181040100").toString()


            }

            KernelID.MASTER.toByte() ->                // Parameter settings, see transaction parameters of PAYPASS in《UEMV develop guide》.
                tlvList
                    .append("9F350122")
                    .append("9F3303E0F8C8")
                    .append("9F40056000F0A001")
                    .append("9A03171020")
                    .append("9F2103150512")
                    .append("9F0206000000000100")
                    .append("9F1A020156")
                    .append("5F2A020156")
                    .append("9C0100")
                    .append("DF918111050000000000")
                    .append("DF91811205FFFFFFFFFF")
                    .append("DF91811005FFFFFFFFFF")
                    .append("DF9182010102")
                    .append("DF9182020100")
                    .append("DF9181150100")
                    .append("DF9182040100")
                    .append("DF812406000000010000")
                    .append("DF812506000000010000")
                    .append("DF812606000000010000")
                    .append("DF812306000000010000")
                    .append("DF9182050160")
                    .append("DF9182060160")
                    .append("DF9182070120")
                    .append("DF9182080120").toString()
            KernelID.AMEX.toByte() -> {}
            KernelID.DISCOVER.toByte() -> {}
            KernelID.JCB.toByte() -> {}
            else -> {}
        }
        var result = outputResult(
            emv!!.setTLVList(finalData.kernelID.toInt(), tlvList.toString()),
            "...onFinalSelect: setTLVList"
        )
        val output = outputResult(emv!!.respondEvent(null), "...onFinalSelect: respondEvent")
        val test1 = result
        val test2 = output
    }

    /**
     * Application to process card record data and set parameters
     * such as display card number, find blacklist, set public key, etc
     */
    @Throws(RemoteException::class)
    fun doReadRecord(record: CardRecord?) {
        jsonData!!.value.put("card_number", BytesUtil.bytes2HexString(record!!.pan))
        jsonData!!.value.put("card_exp", BytesUtil.bytes2HexString(record!!.expiry).substring(2,6))
        jsonData!!.value.put("name", Utils().hexToAscii(emv!!.getTLV("5F20")))


        val capkIndex = record?.pubKIndex?.let { byteArrayOf(it) }
        if (capkIndex != null) {
            Log.d("test aaa capk index", "=> ${capkIndex.toHex()}")
        }
        Log.d("test aaa aid", "=> ${BytesUtil.bytes2HexString(record?.aid)}")
        Log.d("test aaa algo ID", "=> ${record?.algorithmID}")
//        Log.d("test aaa", "=> ${ emv!!.getTLV()}")
        Log.d("test aaa", "=> onFinalSelect ${EMVInfoUtil.getRecordDataDesc(record)}")

        Log.d(
            "test rid",
            "test rid => ${BytesUtil.bytes2HexString(record?.aid).subSequence(0, 10).toString()}"
        )

//            val listPubKey = CAPK.capk.single { k ->
//                k.rid == BytesUtil.bytes2HexString(record.aid)
//                    .subSequence(0, 10) && k.index == capkIndex.toHex()
//            }

        val listPubKey = getCAPK(
            BytesUtil.bytes2HexString(record?.aid).subSequence(0, 10).toString(),
            capkIndex!!.toHex()
        )
        Log.d("test list pub", "test rid => $listPubKey")

        if (listPubKey != null) {
            val capKey = CAPublicKey()
            capKey.index = record!!.pubKIndex
            capKey.rid = listPubKey.rid!!.decodeHex()
            capKey.exp = listPubKey.exponent!!.decodeHex()
            capKey.mod = listPubKey.mod!!.decodeHex()
            capKey.hashFlag = 0x00.toByte()
            capKey.expDate = listPubKey.exp!!.decodeHex()
            capKey.hash = listPubKey.sha1!!.decodeHex()
            Log.d("Test", "get rid ${capKey.rid.toHex()}")
            Log.d("Test", "get mod ${capKey.mod.toHex()}")
            Log.d("Test", "get exp ${capKey.exp.toHex()}")
            Log.d("Test", "get expDate ${capKey.expDate.toHex()}")

            val ret = emv!!.setCAPubKey(capKey)
            Log.d(
                "test capk",
                "$ret => add CAPKey rid = : "
            )

            Log.d("test rid", "test rid => $capKey")
        }




        //outputText("=> onReadRecord | " + EMVInfoUtil.getRecordDataDesc(record));
        val tagStr =
            "4f,50,57, 5a,71, 72, 82,84,8a,8e,  91,  95,9a,  99,  9b , 9c  ,5f20  ,5f24  ,5f28,  5f2a,  5f34 ," +
                    "5f2d,  9f02 , 9f03 , 9f06 , 9f07,  9f08 , 9f09 , 9f0d , 9f0e , 9f0f,  9f10 , 9f11,  9f12,  9f14, 9f17,  9f1a,  9f1b , 9f1e , 9f1f , 9f21,  9f26  ,  9f27 ,   9f33,  9f34,    9f35,   9f36 ,  9f37," +
                    "9f39 ,  9f40 , 9f41,  9f42 , 9f53 , 9f5b,  9f5d , 9f67 , 9f6e , 9f71,  9f7c,  df918110,  df918111,  df918112 , df918124,  df30 , df32  ,df34  ,df35  ,df36 , df37 , df38 , df39"
        val tagArray = tagStr.split(",").toTypedArray()
        val tags: MutableList<String> = ArrayList()
        for (i in tagArray.indices) {
            val t = tagArray[i].trim { it <= ' ' }
            if (!TextUtils.isEmpty(t)) {
                tags.add(t)
            }
        }
        val list: List<TlvResponse> = ArrayList()
        //int ret = emv.getKernelDataList(tags, list);
        //LogUtil.d("getKernelDataList ret = " + ret);
        for (i in list.indices) {
            val info = list[i]
            LogUtil.d(
                "i = " + i + ", " + BytesUtil.bytes2HexString(info.tag) + ", ret = " + info.result + ", " + BytesUtil.bytes2HexString(
                    info.value
                )
            )
        }

            outputResult(emv!!.respondEvent(null), "...onReadRecord: respondEvent")
    }

    /**
     * Request the cardholder to perform the Cardholder verification specified by the kernel.
     */
    @Throws(RemoteException::class)
    fun doCardHolderVerify(cvm: CVMMethod) {
        //outputText("=> onCardHolderVerify | " + EMVInfoUtil.getCVMDataDesc(cvm));
        val param = Bundle()
        param.putByteArray(PinpadData.PIN_LIMIT, byteArrayOf(0, 4, 5, 6, 7, 8, 9, 10, 11, 12))
        val listener: OnPinEntryListener = object : OnPinEntryListener.Stub() {
            override fun onInput(arg0: Int, arg1: Int) {}
            override fun onConfirm(arg0: ByteArray, arg1: Boolean) {
                respondCVMResult(1.toByte())
            }

            override fun onCancel() {
                respondCVMResult(0.toByte())
            }

            override fun onError(error: Int) {
                respondCVMResult(2.toByte())
            }
        }
        when (cvm.cvm) {
            CVMFlag.EMV_CVMFLAG_OFFLINEPIN.toByte() -> {
                val cvmFlagValue = BytesUtil.hexString2Bytes(
                    emv!!.getTLV(EMVTag.DEF_TAG_CVM_FLAG)
                )
                //Offline ciphertext pin
                if (cvmFlagValue != null && cvmFlagValue.size > 0 && cvmFlagValue[0].toInt() == 0x31) {
                    val cslValue = BytesUtil.hexString2Bytes(
                        emv!!.getTLV("DF91815D")
                    )
                    //The public key recovery fails
                    if (cslValue != null && cslValue.size > 1 && cslValue[1].toInt() and 0x40 == 0x40) {
                        // In the case of offline ciphertext pin, if the public key recovery fails, it shall be applied without pop-up encryption window.
                        // Only the modular version of the kernel is supported
                        val chvStatus = TLV.fromData(EMVTag.DEF_TAG_CHV_STATUS, byteArrayOf(0x01))
                        emv!!.respondEvent(chvStatus.toString())
                        return
                    }
                }
                pinpad!!.startOfflinePinEntry(param, listener)
            }
            CVMFlag.EMV_CVMFLAG_ONLINEPIN.toByte() -> {
                //outputText("=> onCardHolderVerify | onlinpin");
                param.putByteArray(PinpadData.PAN_BLOCK, lastCardRecord!!.pan)
                pinpad!!.startPinEntry(DemoConfig.KEYID_PIN, param, listener)
            }
            else ->            //outputText("=> onCardHolderVerify | default");
                respondCVMResult(1.toByte())
        }
    }

    protected fun respondCVMResult(result: Byte) {
        try {
            val chvStatus = TLV.fromData(EMVTag.DEF_TAG_CHV_STATUS, byteArrayOf(result))
            val ret = emv!!.respondEvent(chvStatus.toString())
            outputResult(ret, "...onCardHolderVerify: respondEvent")
        } catch (e: Exception) {
            //handleException(e);
        }
    }

    /**
     * Request the application to execute online authorization.
     */
    @Throws(RemoteException::class)
    open fun doOnlineProcess(transData: TransData?) {
        //outputText("=> onOnlineProcess | TLVData for online:" + BytesUtil.bytes2HexString(s));
        //val test = EMVTag.DEF_TAG_TAC_ONLINE
        var loop = true
        //var responseCode = ""
        //while (loop) {
            requestOnlineStatus!!.value = true
        //if(cardCanDoOnlineStatus!!.value) {
         /*   if (responseOnlineStatus!!.value) {
                //responseCode = utils.asciiToHex(jsonData!!.value.getString("res_code"))
                Log.d("EMV","2 Gen AC")
                val onlineResult = doOnlineProcess()
                val ret = emv!!.respondEvent(onlineResult)
                outputResult(ret, "...onOnlineProcess: respondEvent")
                requestOnlineStatus!!.value = false
                responseOnlineStatus!!.value = false
                loop = false
            }
        }*/
        //}


    }
    fun emvResponseOnline(jsonData : JSONObject){
        val onlineResult = doOnlineProcess(jsonData)
        val ret = emv!!.respondEvent(onlineResult)
        //requestOnlineStatus!!.value = false
        //responseOnlineStatus!!.value = false

    }

    /**
     * pack message, communicate with server, analyze server response message.
     *
     * @return result of online process，he data elements are as follows:
     * DEF_TAG_ONLINE_STATUS (M)
     * If online communication is success, following is necessary while retured by host service.
     * EMV_TAG_TM_ARC (C)
     * DEF_TAG_AUTHORIZE_FLAG (C)
     * EMV_TAG_TM_AUTHCODE (C)
     * DEF_TAG_HOST_TLVDATA (C)
     */
    private fun doOnlineProcess(jsonData : JSONObject): String {
        var responseCode = ""
        var onlineSuccess = false
        var onlineApproved = false
        Log.d("Test Count", "1")

        if (jsonData!!.has("res_code")){
            responseCode = utils.asciiToHex(jsonData.getString("res_code"))
            if(responseCode == "3030") {
                onlineApproved = true
            }
            onlineSuccess = true
        }
        return if (onlineSuccess) {
            Log.d("EMV","Online Success")
            val onlineResult = StringBuffer()
            onlineResult.append(EMVTag.DEF_TAG_ONLINE_STATUS).append("01").append("00")
            val hostRespCode = responseCode
            onlineResult.append(EMVTag.EMV_TAG_TM_ARC).append("02").append(hostRespCode)
            onlineResult.append(EMVTag.DEF_TAG_AUTHORIZE_FLAG).append("01")
                .append(if (onlineApproved) "01" else "00")
            val hostTlvData =
                "9F3501229C01009F3303E0F1C89F02060000000000019F03060000000000009F101307010103A0A802010A010000000052856E2C9B9F2701809F260820F63D6E515BD2CC9505008004E8009F1A0201565F2A0201569F360201C982027C009F34034203009F37045D5F084B9A031710249F1E0835303530343230308408A0000003330101019F090200309F410400000001"
            onlineResult.append(
                TLV.fromData(
                    EMVTag.DEF_TAG_HOST_TLVDATA,
                    BytesUtil.hexString2Bytes(hostTlvData)
                ).toString()
            )
            onlineResult.toString()

        } else {
            //outputRedText("!!! online failed !!!");
            Log.d("EMV","Online Error")
            "DF9181090101"

        }
    }

    fun doVerifyOfflinePin(
        flag: Int,
        random: ByteArray?,
        capKey: CAPublicKey?,
        result: OfflinePinVerifyResult
    ) {
        //outputText("=> onVerifyOfflinePin");
        try {
            /** inside insert card - 0；inside swing card – 6；External device is connected to the USB port - 7；External device is connected to the COM port -8  */
            val icToken = 0
            //Specify the type of "PIN check APDU message" that will be sent to the IC card.Currently only support VCF_DEFAULT.
            val cmdFmt = OfflinePinVerify.VCF_DEFAULT
            val offlinePinVerify = OfflinePinVerify(flag.toByte(), icToken, cmdFmt, random)
            val pinVerifyResult = PinVerifyResult()
            val ret = pinpad!!.verifyOfflinePin(
                offlinePinVerify,
                getPinPublicKey(capKey),
                pinVerifyResult
            )
            if (!ret) {
                //outputRedText("verifyOfflinePin fail: " + pinpad.getLastError());
                stopEMV()
                return
            }
            val apduRet = pinVerifyResult.apduRet
            val sw1 = pinVerifyResult.sW1
            val sw2 = pinVerifyResult.sW2
            result.setSW(sw1.toInt(), sw2.toInt())
            result.result = apduRet.toInt()
        } catch (e: Exception) {
            //handleException(e);
        }
    }

    /**
     * Inform the application that the EMV transaction is completed and the kernel exits.
     */
    fun doEndProcess(result: Int, transData: TransData?) {
        val getACType = getACTypeDesc(transData!!.acType)
        jsonData!!.value.put("gen_ac", getACType)
        //jsonData!!.value.put("gen_ac", "AAC[0x00]")

        Log.d("Get AC Type", getACType)
        if (result != EMVError.SUCCESS) {
            val Error = EMVInfoUtil.getErrorMessage(result)
            if (Error == "ERROR_EMV_RESULT_STOP[0xEE07]"){}
            else{
                errorMessage!!.value = Error
                hasError!!.value = true
            }

        } else {
            if (kernel == "visa") {
                var track2 = emv!!.getTLV("57")
                if (track2.length == 38) {
                    track2 = track2.substring(0, track2.length - 1)
                }
                jsonData!!.value.put("card_number", BytesUtil.bytes2HexString(transData!!.pan))
                jsonData!!.value.put("card_exp", BytesUtil.bytes2HexString(transData!!.expiry).substring(2,6))
                jsonData!!.value.put("name", Utils().hexToAscii(emv!!.getTLV("5F20")))
                jsonData!!.value.put("track2", track2)
                requestOnlineStatus!!.value = true
                //Timer().schedule(1000) {
                    //if (cardCanDoOnlineStatus!!.value) {
                /*var loop = true
                while (loop) {
                        if (responseOnlineStatus!!.value) {
                            requestOnlineStatus!!.value = false
                            responseOnlineStatus!!.value = false
                            endProcessStatus!!.value = true
                            loop = false
                        }
                }*/

            } else {
                endProcessStatus!!.value = true
            }

            //outputText("=> onEndProcess | EMV_RESULT_NORMAL | " + EMVInfoUtil.getTransDataDesc(transData));
        }

        //outputText("\n");
    }

    fun doSendOut(ins: Int, data: ByteArray) {
        when (ins) {
            KernelINS.DISPLAY ->            // DisplayMsg: MsgID（1 byte） + Currency（1 byte）+ DataLen（1 byte） + Data（30 bytes）
                if (data[0] == MessageID.ICC_ACCOUNT.toByte()) {
                    val len = data[2].toInt()
                    val account = BytesUtil.subBytes(data, 1 + 1 + 1, len)
                    val accTLVList = TLVList.fromBinary(account)
                    var track2 = BytesUtil.bytes2HexString(accTLVList.getTLV("57").bytesValue)
                    if (track2.length == 38) {
                        track2 = track2.substring(0, track2.length - 1)
                    }
                    jsonData!!.value.put("track2", track2.toString())
                }
            KernelINS.DBLOG -> {
                var i = data.size - 1
                while (i >= 0) {
                    if (data[i].toInt() == 0x00) {
                        data[i] = 0x20
                    }
                    i--
                }
                Log.d("DBLOG", String(data))
            }
            KernelINS.CLOSE_RF ->            //outputText("=> onSendOut: Notify the application to halt contactless module");
                halt()
            else -> {}
        }
    }

    private fun outputResult(ret: Int, stepName: String?) {
        when (ret) {
            EMVError.SUCCESS -> {}
            EMVError.REQUEST_EXCEPTION -> {}
            EMVError.SERVICE_CRASH -> {}
            else -> {}
        }
    }

    open fun getPinPublicKey(from: CAPublicKey?): PinPublicKey? {
        if (from == null) {
            return null
        }
        val to = PinPublicKey()
        to.mRid = from.rid
        to.mExp = from.exp
        to.mExpiredDate = from.expDate
        to.mHash = from.hash
        to.mHasHash = from.hashFlag
        to.mIndex = from.index
        to.mMod = from.mod
        return to
    }

}

