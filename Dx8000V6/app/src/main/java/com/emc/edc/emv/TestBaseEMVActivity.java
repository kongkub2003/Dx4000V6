package com.emc.edc.emv;

import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.CallSuper;

import com.usdk.apiservice.aidl.data.StringValue;
import com.usdk.apiservice.aidl.emv.ActionFlag;
import com.usdk.apiservice.aidl.emv.CAPublicKey;
import com.usdk.apiservice.aidl.emv.CVMFlag;
import com.usdk.apiservice.aidl.emv.CVMMethod;
import com.usdk.apiservice.aidl.emv.CandidateAID;
import com.usdk.apiservice.aidl.emv.CardRecord;
import com.usdk.apiservice.aidl.emv.EMVError;
import com.usdk.apiservice.aidl.emv.EMVEventHandler;
import com.usdk.apiservice.aidl.emv.EMVTag;
import com.usdk.apiservice.aidl.emv.FinalData;
import com.usdk.apiservice.aidl.emv.KernelID;
import com.usdk.apiservice.aidl.emv.KernelINS;
import com.usdk.apiservice.aidl.emv.MessageID;
import com.usdk.apiservice.aidl.emv.OfflinePinVerifyResult;
import com.usdk.apiservice.aidl.emv.SearchCardListener;
import com.usdk.apiservice.aidl.emv.TlvResponse;
import com.usdk.apiservice.aidl.emv.TransData;
import com.usdk.apiservice.aidl.emv.UEMV;
import com.usdk.apiservice.aidl.emv.WaitCardFlag;
import com.usdk.apiservice.aidl.pinpad.KAPId;
import com.usdk.apiservice.aidl.pinpad.KeySystem;
import com.usdk.apiservice.aidl.pinpad.OfflinePinVerify;
import com.usdk.apiservice.aidl.pinpad.OnPinEntryListener;
import com.usdk.apiservice.aidl.pinpad.PinPublicKey;
import com.usdk.apiservice.aidl.pinpad.PinVerifyResult;
import com.usdk.apiservice.aidl.pinpad.PinpadData;
import com.usdk.apiservice.aidl.pinpad.UPinpad;

import java.util.ArrayList;
import java.util.List;

import com.emc.edc.constant.DemoConfig;
import com.emc.edc.emv.entity.CardOption;
import com.emc.edc.emv.entity.EMVOption;
import com.emc.edc.emv.util.BytesUtil;
import com.emc.edc.emv.util.LogUtil;
import com.emc.edc.emv.util.TLV;
import com.emc.edc.emv.util.TLVList;



public class TestBaseEMVActivity {

	protected EMVOption emvOption = EMVOption.create();
	protected CardOption cardOption = CardOption.create();

	protected UEMV emv;
	protected UPinpad pinpad;
	private CardRecord lastCardRecord;
	private int wholeTrkId = 0;
	private boolean emvProcessOptimization = false;

	@CallSuper
	//@Override
	protected void onCreateView(Bundle savedInstanceState) {
		initDeviceInstance();
		//setContentView(R.layout.activity_emv);
		initCardOption();
	}

	protected void initDeviceInstance() {
		emv = DeviceHelper.me().getEMV();
		pinpad = DeviceHelper.me().getPinpad(new KAPId(DemoConfig.REGION_ID, DemoConfig.KAP_NUM), KeySystem.KS_MKSK, DemoConfig.PINPAD_DEVICE_NAME);
	}

	private void openPinpad() {
		try {
			pinpad.open();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private void closePinpad() {
		try {
			pinpad.close();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	protected void initCardOption() {
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

		cardOption.rfDeviceName(DemoConfig.RF_DEVICE_NAME);
		cardOption.trackCheckEnabled(false);
	}

	private void setTrkIdWithWholeData(boolean isSlted, int trkId) {
		if (isSlted) {
			wholeTrkId |= trkId;
		} else {
			wholeTrkId &= ~trkId;
		}
		cardOption.trkIdWithWholeData(wholeTrkId);
	}

	protected void searchRFCard(final Runnable next) {
		//outputBlueText("******* search RF card *******");
		//outputRedText(getString(R.string.pass_card_again));

		Bundle rfCardOption = CardOption.create()
				.supportICCard(true)
				.supportMagCard(false)
				.supportRFCard(true)
				.rfDeviceName(DemoConfig.RF_DEVICE_NAME)
				.toBundle();
		try {
			emv.searchCard(rfCardOption, DemoConfig.TIMEOUT, new SearchListenerAdapter(){
				@Override
				public void onCardPass(int cardType) {
					//outputText("=> onCardPass | cardType = " + cardType);
					next.run();
				}

				@Override
				public void onTimeout() {
					//outputRedText("=> onTimeout");
					stopEMV();
				}

				@Override
				public void onError(int code, String message) {
					//outputRedText(String.format("=> onError | %s[0x%02X]", message, code));
					stopEMV();
				}
			});
		} catch (Exception e) {
			//handleException(e);
		}
	}
	public void searchCard(final Runnable next) {
		//outputBlueText("******  search card ******");
		//outputRedText(getString(R.string.insert_pass_swipe_card));

		try {
			emv.searchCard(cardOption.toBundle(), DemoConfig.TIMEOUT, new SearchCardListener.Stub() {
				@Override
				public void onCardPass(int cardType) {
					//outputText("=> onCardPass | cardType = " + cardType);
					next.run();
				}

				@Override
				public void onCardInsert() {
					//outputText("=> onCardInsert");
					next.run();
				}

				@Override
				public void onCardSwiped(Bundle track) {
					/*outputText("=> onCardSwiped");
					outputText("==> Pan: " + track.getString("PAN"));
					outputText("==> Track 1: " + track.getString("TRACK1"));
					outputText("==> Track 2: " + track.getString("TRACK2"));
					outputText("==> Track 3: " + track.getString("TRACK3"));
					outputText("==> Service code: " + track.getString("SERVICE_CODE"));
					outputText("==> Card exprited date: " + track.getString("EXPIRED_DATE"));*/

					int[] trackStates = track.getIntArray("TRACK_STATES");
					for(int i = 0; i < trackStates.length; i++) {
						//outputText(String.format("==> Track%s State???%d", i+1, trackStates[i]));
					}

					stopEMV();
				}

				@Override
				public void onTimeout() {
					//outputRedText("=> onTimeout");
					stopEMV();
				}

				@Override
				public void onError(int code, String message) {
					//outputRedText(String.format("=> onError | %s[0x%02X]", message, code));
					stopEMV();
				}
			});
		} catch (Exception e) {
			String test = "";
			//handleException(e);
		}
	}

	public void startEMV(EMVOption option) {
		try {
			//outputBlueText("******  start EMV ******");

			initDeviceInstance();
			initCardOption();

			//getKernelVersion();
			//getCheckSum();



			int ret = emv.startEMV(emvOption.flagPSE((byte)0x00).toBundle(), emvEventHandler);
			outputResult(ret, "=> Start EMV");
			openPinpad();
		} catch (Exception e) {
			//handleException(e);
		}
	}

	private void getKernelVersion() {
		try {
			StringValue version = new StringValue();
			int ret = emv.getKernelVersion(version);
			if (ret == EMVError.SUCCESS) {
				//outputBlackText("EMV kernel version: " + version.getData());
			} else {
				//outputRedText("EMV kernel version: fail, ret = " + ret);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private void getCheckSum() {
		try {
			int flag = 0xA2;
			StringValue checkSum = new StringValue();
			int ret = emv.getCheckSum(flag, checkSum);
			if (ret == EMVError.SUCCESS) {
				//outputBlackText("EMV kernel[" + flag + "] checkSum: " + checkSum.getData());
			} else {
				//outputRedText("EMV kernel[" + flag + "] checkSum: fail, ret = " + ret);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	protected void stopEMV() {
		try {
			//outputBlueText("******  stop EMV ******");
			outputResult(emv.stopEMV(), "=> Stop EMV");
			closePinpad();
		} catch (Exception e) {
			//handleException(e);
		}
	}

	protected void stopSearch() {
		try {
			//outputBlueText("******  stop Search ******");
			emv.stopSearch();
		} catch (Exception e) {
			//handleException(e);
		}
	}

	protected void halt() {
		try {
			//outputBlueText("******  close RF device ******");
			emv.halt();
		} catch (Exception e) {
			//handleException(e);
		}
	}

	protected EMVEventHandler emvEventHandler = new EMVEventHandler.Stub() {
		@Override
		public void onInitEMV() throws RemoteException {
			doInitEMV();
		}

		@Override
		public void onWaitCard(int flag) throws RemoteException {
			doWaitCard(flag);
		}

		@Override
		public void onCardChecked(int cardType) throws RemoteException {
			// Only happen when use startProcess()
			doCardChecked(cardType);
		}

		@Override
		public void onAppSelect(boolean reSelect, List<CandidateAID> list) throws RemoteException {
			doAppSelect(reSelect, list);
		}

		@Override
		public void onFinalSelect(FinalData finalData) throws RemoteException {
			doFinalSelect(finalData);
		}

		@Override
		public void onReadRecord(CardRecord cardRecord) throws RemoteException {
			lastCardRecord = cardRecord;
			doReadRecord(cardRecord);
		}

		@Override
		public void onCardHolderVerify(CVMMethod cvmMethod) throws RemoteException {
			doCardHolderVerify(cvmMethod);
		}

		@Override
		public void onOnlineProcess(TransData transData) throws RemoteException {

			doOnlineProcess(transData);
		}

		@Override
		public void onEndProcess(int result, TransData transData) throws RemoteException {
			doEndProcess(result, transData);
		}

		@Override
		public void onVerifyOfflinePin(int flag, byte[] random, CAPublicKey caPublicKey, OfflinePinVerifyResult offlinePinVerifyResult) throws RemoteException {
			doVerifyOfflinePin(flag, random, caPublicKey, offlinePinVerifyResult);
		}

		@Override
		public void onObtainData(int ins, byte[] data) throws RemoteException {
			//outputText("=> onObtainData: instruction is 0x" + Integer.toHexString(ins) + ", data is " + BytesUtil.bytes2HexString(data));
		}

		@Override
		public void onSendOut(int ins, byte[] data) throws RemoteException {
			doSendOut(ins, data);
		}
	};

	public void doInitEMV() throws RemoteException {
		//outputText("=> onInitEMV ");
		manageAID();

		//  init transaction parameters???please refer to transaction parameters
		//  chapter about onInitEMV event in???UEMV develop guide???
		//  For example, if VISA is supported in the current transaction,
		//  the label: DEF_TAG_PSE_FLAG(M) must be set, as follows:

		emv.setTLV(KernelID.VISA, EMVTag.DEF_TAG_PSE_FLAG, "03");
		// For example, if AMEX is supported in the current transaction???
		// labels DEF_TAG_PSE_FLAG(M) and DEF_TAG_PPSE_6A82_TURNTO_AIDLIST(M) must be set, as follows???
		// emv.setTLV(KernelID.AMEX, EMVTag.DEF_TAG_PSE_FLAG, "03");
		// emv.setTLV(KernelID.AMEX, EMVTag.DEF_TAG_PPSE_6A82_TURNTO_AIDLIST, "01");
	}

	protected void manageAID() throws RemoteException {
		//outputBlueText("****** manage AID ******");
		String[] aids = new String[] {
				"A000000333010106",
				"A000000333010103",
				"A000000333010102",
				"A000000333010101",
				"A0000000651010",
				"A0000000043060",
				"A0000000041010",
				"A000000003101002",
				"A000000003"
		};
		for (String aid : aids) {
			int ret = emv.manageAID(ActionFlag.ADD, aid, true);
			outputResult(ret, "=> add AID : " + aid);
		}
	}

	public void doWaitCard(int flag) throws RemoteException {
		switch (flag) {
			case WaitCardFlag.NORMAL:
				searchCard(new Runnable(){
					@Override
					public void run() {
						if (emvProcessOptimization) {
							return;
						}

						respondCard();
					}
				});
				break;
			case WaitCardFlag.ISS_SCRIPT_UPDATE:
			case WaitCardFlag.SHOW_CARD_AGAIN:
				searchRFCard(new Runnable(){
					@Override
					public void run() {
						respondCard();
					}
				});
				break;

			case WaitCardFlag.EXECUTE_CDCVM:
				emv.halt();
				/*uiHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						searchRFCard(new Runnable(){
							@Override
							public void run() {
								respondCard();
							}
						});
					}
				}, 1200);*/
				break;

			default:
				//outputRedText("!!!! unknow flag !!!!");
		}
	}

	protected void respondCard() {
		try {
			emv.respondCard();
		} catch (RemoteException e) {
			//handleException(e);
		}
	}

	public void doCardChecked(int cardType) {
		// Only happen when use startProcess()
	}

	/**
	 * Request cardholder to select application
	 */
	public void doAppSelect(boolean reSelect, final List<CandidateAID> candList) {
		//outputText("=> onAppSelect: cand AID size = " + candList.size());
		if (candList.size() > 1) {
			/*selectApp(candList,new DialogUtil.OnSelectListener() {
				@Override
				public void onCancel() {
					try {
						emv.stopEMV();
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}

				@Override
				public void onSelected(int item) {
					respondAID(candList.get(item).getAID());
				}
			});*/
		} else {
			respondAID(candList.get(0).getAID());
		}
	}
	
	//protected void selectApp(final List<CandidateAID> candList, final DialogUtil.OnSelectListener listener) {
	protected void selectApp(final List<CandidateAID> candList) {
		final List<String> aidInfoList = new ArrayList<>();
		for (CandidateAID candAid : candList) {
			aidInfoList.add(new String(candAid.getAPN()));
		}
		/*runOnUiThread(new Runnable() {
			@Override
			public void run() {
				DialogUtil.showSelectDialog(BaseEMVActivity.this, "Please select app", aidInfoList, 0, listener);

			}
		});*/
	}

	protected void respondAID(byte[] aid) {
		try {
			//outputBlueText("Select aid: " + BytesUtil.bytes2HexString(aid));
			TLV tmAid = TLV.fromData(EMVTag.EMV_TAG_TM_AID, aid);
			outputResult(emv.respondEvent(tmAid.toString()), "...onAppSelect: respondEvent");
		} catch (Exception e) {
			//handleException(e);
		}
	}

	/**
	 * Parameters can be set or adjusted according to the aid selected finally
	 * please refer to transaction parameters chapter about onFinalSelect event in???UEMV develop guide???
	 */
	public void doFinalSelect(FinalData finalData) throws RemoteException {
		//outputText("=> onFinalSelect | " + EMVInfoUtil.getFinalSelectDesc(finalData));

		String tlvList = null;
		switch (finalData.getKernelID()) {
			case KernelID.EMV:
				// Parameter settings, see transaction parameters of EMV Contact Level 2 in???UEMV develop guide???
				// For reference only below
				tlvList = "9F02060000000001009F03060000000000009A031710209F21031505129F410400000001" +
						"9F3501229F3303E0F8C89F40056000F0A0019F1A0201565F2A0201569C0100";
						//"DF9181040100DF91810C0130DF91810E0190";
				break;
			case KernelID.PBOC:
				// if suport PBOC Ecash???see transaction parameters of PBOC Ecash in???UEMV develop guide???.
				// If support qPBOC, see transaction parameters of QuickPass in???UEMV develop guide???.
				// For reference only below
				tlvList = "9F02060000000001009F03060000000000009A031710209F21031505129F4104000000019F660427004080";
				break;
			case KernelID.VISA:
				// Parameter settings, see transaction parameters of PAYWAVE in???UEMV develop guide???.
				tlvList = new StringBuilder()
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
						.append("DF9181040100").toString();
				break;
			case KernelID.MASTER:
				// Parameter settings, see transaction parameters of PAYPASS in???UEMV develop guide???.
				tlvList = new StringBuilder()
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
						.append("DF9182080120").toString();
				break;
			case KernelID.AMEX:
				// Parameter settings, see transaction parameters of AMEX in???UEMV develop guide???.
				break;
			case KernelID.DISCOVER:
				// Parameter settings, see transaction parameters of DISCOVER in???UEMV develop guide???.
				break;
			case KernelID.JCB:
				// Parameter settings, see transaction parameters of JCB in???UEMV develop guide???.
				break;
			default:
				break;
		}

		outputResult(emv.setTLVList(finalData.getKernelID(), tlvList), "...onFinalSelect: setTLVList");
		outputResult(emv.respondEvent(null), "...onFinalSelect: respondEvent");
	}

	/**
	 * Application to process card record data and set parameters
	 * such as display card number, find blacklist, set public key, etc
	 */
	public void doReadRecord(CardRecord record) throws RemoteException {
		//outputText("=> onReadRecord | " + EMVInfoUtil.getRecordDataDesc(record));

		String tagStr = "4f,50,57, 5a,71, 72, 82,84,8a,8e,  91,  95,9a,  99,  9b , 9c  ,5f20  ,5f24  ,5f28,  5f2a,  5f34 ," +
				"5f2d,  9f02 , 9f03 , 9f06 , 9f07,  9f08 , 9f09 , 9f0d , 9f0e , 9f0f,  9f10 , 9f11,  9f12,  9f14, 9f17,  9f1a,  9f1b , 9f1e , 9f1f , 9f21,  9f26  ,  9f27 ,   9f33,  9f34,    9f35,   9f36 ,  9f37," +
				"9f39 ,  9f40 , 9f41,  9f42 , 9f53 , 9f5b,  9f5d , 9f67 , 9f6e , 9f71,  9f7c,  df918110,  df918111,  df918112 , df918124,  df30 , df32  ,df34  ,df35  ,df36 , df37 , df38 , df39";
		String[] tagArray = tagStr.split(",");
		List<String> tags = new ArrayList<>();
		for (int i = 0; i < tagArray.length; i++) {
			String t = tagArray[i].trim();
			if (!TextUtils.isEmpty(t)) {
				tags.add(t);
			}
		}
		List<TlvResponse> list = new ArrayList<>();
		//int ret = emv.getKernelDataList(tags, list);
		//LogUtil.d("getKernelDataList ret = " + ret);
		for (int i = 0; i < list.size(); i++) {
			TlvResponse info = list.get(i);
			LogUtil.d("i = " + i + ", " + BytesUtil.bytes2HexString(info.getTag()) + ", ret = " + info.getResult() + ", " + BytesUtil.bytes2HexString(info.getValue()));

		}
		outputResult(emv.respondEvent(null), "...onReadRecord: respondEvent");
	}

	/**
	 * Request the cardholder to perform the Cardholder verification specified by the kernel.
	 */
	public void doCardHolderVerify(CVMMethod cvm) throws RemoteException {
		//outputText("=> onCardHolderVerify | " + EMVInfoUtil.getCVMDataDesc(cvm));

		Bundle param = new Bundle();
		param.putByteArray(PinpadData.PIN_LIMIT, new byte[] { 0, 4, 5, 6, 7, 8, 9, 10, 11, 12});
		OnPinEntryListener listener = new OnPinEntryListener.Stub() {
			@Override
			public void onInput(int arg0, int arg1) {
			}
			
			@Override
			public void onConfirm(byte[] arg0, boolean arg1) {
				respondCVMResult((byte)1);
			}
			
			@Override
			public void onCancel() {
				respondCVMResult((byte)0);
			}
			
			@Override
			public void onError(int error) {
				respondCVMResult((byte)2);
			}
		};
		
		switch (cvm.getCVM()) {
		case CVMFlag.EMV_CVMFLAG_OFFLINEPIN:
			byte[] cvmFlagValue = BytesUtil.hexString2Bytes(emv.getTLV(EMVTag.DEF_TAG_CVM_FLAG));
			//Offline ciphertext pin
			if (cvmFlagValue != null && cvmFlagValue.length > 0 && cvmFlagValue[0] == 0x31) {
				byte[] cslValue = BytesUtil.hexString2Bytes(emv.getTLV("DF91815D"));
				//The public key recovery fails
				if (cslValue != null && cslValue.length > 1 && (cslValue[1] & 0x40) == 0x40) {
					// In the case of offline ciphertext pin, if the public key recovery fails, it shall be applied without pop-up encryption window.
					// Only the modular version of the kernel is supported
					TLV chvStatus = TLV.fromData(EMVTag.DEF_TAG_CHV_STATUS, new byte[]{0x01});
					emv.respondEvent(chvStatus.toString());
					return;
				}
			}
			pinpad.startOfflinePinEntry(param, listener);
			break;
		case CVMFlag.EMV_CVMFLAG_ONLINEPIN:
			//outputText("=> onCardHolderVerify | onlinpin");
			param.putByteArray(PinpadData.PAN_BLOCK, lastCardRecord.getPan());
			pinpad.startPinEntry(DemoConfig.KEYID_PIN, param, listener);
			break;
		default:
			//outputText("=> onCardHolderVerify | default");
			respondCVMResult((byte)1);
		}
	}

	protected void respondCVMResult(byte result) {
		try {
			TLV chvStatus = TLV.fromData(EMVTag.DEF_TAG_CHV_STATUS, new byte[]{result});
			int ret = emv.respondEvent(chvStatus.toString());
			outputResult(ret, "...onCardHolderVerify: respondEvent");
		} catch (Exception e) {
			//handleException(e);
		}
	}

	/**
	 * Request the application to execute online authorization.
	 */
	public void doOnlineProcess(TransData transData) throws RemoteException {
		//outputText("=> onOnlineProcess | TLVData for online:" + BytesUtil.bytes2HexString(transData.getTLVData()));
		String test = EMVTag.DEF_TAG_TAC_ONLINE;
		String onlineResult = doOnlineProcess();
		int ret = emv.respondEvent(onlineResult);
		outputResult(ret, "...onOnlineProcess: respondEvent");
	}

	/**
	 * pack message, communicate with server, analyze server response message.
	 *
	 * @return result of online process???he data elements are as follows:
	 * DEF_TAG_ONLINE_STATUS (M)
	 * If online communication is success, following is necessary while retured by host service.
	 * EMV_TAG_TM_ARC (C)
	 * DEF_TAG_AUTHORIZE_FLAG (C)
	 * EMV_TAG_TM_AUTHCODE (C)
	 * DEF_TAG_HOST_TLVDATA (C)
	 */
	private String doOnlineProcess() {
		//outputBlueText("****** doOnlineProcess ******");
		//outputBlueText("... ...");
		//outputBlueText("... ...");
		boolean onlineSuccess = true;
		if (onlineSuccess) {
			StringBuffer onlineResult = new StringBuffer();
			onlineResult.append(EMVTag.DEF_TAG_ONLINE_STATUS).append("01").append("00");

			String hostRespCode = "3030";
			onlineResult.append(EMVTag.EMV_TAG_TM_ARC).append("02").append(hostRespCode);

			boolean onlineApproved = true;
			onlineResult.append(EMVTag.DEF_TAG_AUTHORIZE_FLAG).append("01").append(onlineApproved ? "01" : "00");

			String hostTlvData = "9F3501229C01009F3303E0F1C89F02060000000000019F03060000000000009F101307010103A0A802010A010000000052856E2C9B9F2701809F260820F63D6E515BD2CC9505008004E8009F1A0201565F2A0201569F360201C982027C009F34034203009F37045D5F084B9A031710249F1E0835303530343230308408A0000003330101019F090200309F410400000001";
			onlineResult.append(TLV.fromData(EMVTag.DEF_TAG_HOST_TLVDATA, BytesUtil.hexString2Bytes(hostTlvData)).toString());

			return onlineResult.toString();

		} else {
			//outputRedText("!!! online failed !!!");
			return "DF9181090101";
		}
	}
	
	public void doVerifyOfflinePin(int flag, byte[] random, CAPublicKey capKey, OfflinePinVerifyResult result) {
		//outputText("=> onVerifyOfflinePin");

		try {
			/** inside insert card - 0???inside swing card ??? 6???External device is connected to the USB port - 7???External device is connected to the COM port -8 */
			int icToken = 0;
			//Specify the type of "PIN check APDU message" that will be sent to the IC card.Currently only support VCF_DEFAULT.
			byte cmdFmt = OfflinePinVerify.VCF_DEFAULT;
			OfflinePinVerify offlinePinVerify = new OfflinePinVerify((byte)flag, icToken, cmdFmt, random);
			PinVerifyResult pinVerifyResult = new PinVerifyResult();
			boolean ret = pinpad.verifyOfflinePin(offlinePinVerify, getPinPublicKey(capKey), pinVerifyResult);
			if (!ret) {
				//outputRedText("verifyOfflinePin fail: " + pinpad.getLastError());
				stopEMV();
				return;
			}

			byte apduRet = pinVerifyResult.getAPDURet() ;
			byte sw1 = pinVerifyResult.getSW1() ;
			byte sw2 = pinVerifyResult.getSW2() ;
			result.setSW(sw1, sw2);
			result.setResult(apduRet);
		} catch (Exception e) {
			//handleException(e);
		}
	}

	/**
	 * Inform the application that the EMV transaction is completed and the kernel exits.
	 */
	public void doEndProcess(int result, TransData transData) {
		if (result != EMVError.SUCCESS) {
			//outputRedText("=> onEndProcess | " + EMVInfoUtil.getErrorMessage(result));
		} else {
			//outputText("=> onEndProcess | EMV_RESULT_NORMAL | " + EMVInfoUtil.getTransDataDesc(transData));
		}
		//outputText("\n");
	}
	
	public void doSendOut(int ins, byte[] data) {
		switch (ins) {
		case KernelINS.DISPLAY:
			// DisplayMsg: MsgID???1 byte??? + Currency???1 byte???+ DataLen???1 byte??? + Data???30 bytes???
			if (data[0] == MessageID.ICC_ACCOUNT) {
				int len = data[2];
				byte[] account = BytesUtil.subBytes(data, 1+1+1, len);
				
				TLVList accTLVList = TLVList.fromBinary(account);
				String track2 = BytesUtil.bytes2HexString(accTLVList.getTLV("57").getBytesValue());
				//outputText("=> onSendOut | track2 = " + track2);
			}
			break;

		case KernelINS.DBLOG:
			for (int i = data.length - 1; i >= 0; i--) {
				if (data[i] == 0x00) {
					data[i] = 0x20;
				}
			}
			Log.d("DBLOG", new String(data));
			break;

		case KernelINS.CLOSE_RF:
			//outputText("=> onSendOut: Notify the application to halt contactless module");
			halt();
			break;
			
		default:	
			//outputText("=> onSendOut: instruction is 0x" + Integer.toHexString(ins) + ", data is " + BytesUtil.bytes2HexString(data));
		}
	}

	protected void outputResult(int ret, String stepName) {
		switch (ret) {
		case EMVError.SUCCESS:
			//outputBlackText(stepName + " success");
			break;
		case EMVError.REQUEST_EXCEPTION:
			//outputRedText(stepName + " fail: register yet?");
			break;
		case EMVError.SERVICE_CRASH:
			//outputRedText(stepName + " fail: masterContol service crash");
			break;
		default:
			//outputRedText(String.format(stepName + " fail[0x%02X]", ret));
		}
	}

	static PinPublicKey getPinPublicKey(CAPublicKey from) {
		if (from == null) {
			return null;
		}
		PinPublicKey to = new PinPublicKey();
		to.mRid = from.getRid();
		to.mExp = from.getExp();
		to.mExpiredDate = from.getExpDate();
		to.mHash = from.getHash();
		to.mHasHash = from.getHashFlag();
		to.mIndex = from.getIndex();
		to.mMod = from.getMod();
		return to;
	}
}
