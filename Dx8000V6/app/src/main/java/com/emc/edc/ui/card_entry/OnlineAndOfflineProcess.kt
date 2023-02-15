package com.emc.edc.ui.card_entry

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.navigation.NavController
import com.emc.edc.*
import com.emc.edc.emv.DeviceHelper
import com.emc.edc.ui.utils.getInformationTosaveTransaction
import com.emc.edc.utils.ISO8583Extracting
import com.emc.edc.utils.TransportData
import com.emc.edc.utils.buildISO8583
import kotlinx.coroutines.*
import org.json.JSONObject
import java.util.ArrayList

@SuppressLint("CoroutineCreationDuringComposition")
//@Composable
@RequiresApi(Build.VERSION_CODES.O)
@DelicateCoroutinesApi
suspend fun cardEntryOnlineAndOffline(
    navController: NavController,
    hasError: MutableState<Boolean>,
    errMessage: MutableState<String>,
    jsonData: MutableState<JSONObject>,
    responseOnlineStatus: MutableState<Boolean>,
    dialogStatus: MutableState<Boolean>,
    connectionStatus: MutableState<Boolean>,
    textLoading: MutableState<String>
) {
    try {
        val cardData =
            selectCardData(jsonData.value.getString("card_number"))
        val host = selectHost(cardData!!.host_record_index!!)
        val dataISO8583 = buildISO8583(jsonData.value)
        val requestToHost = dataISO8583.getString("iso8583_payload")
        val reversalMessage =
            dataISO8583.getString("iso8583_reversal_payload")
        var reversalFlag = host!!.reversal_flag!!
        var responseFromHost = ArrayList<String>()

        var loop = if (reversalFlag) {
            2
        } else {
            1
        }
        Log.v("LOOP : ", loop.toString())
        while (loop > 0) {
            dialogStatus.value = true
            if (reversalFlag) {//send reversal
                textLoading.value = "Sending reversal"
                delay(1_500)
                responseFromHost =
                    TransportData().sendOnlineTransaction(
                        host.reversal_msg!!,
                        connectionStatus,
                        hasError,
                        errMessage,
                        textLoading
                    )
            }

            if (!reversalFlag) { //send normal transaction
                setReverseFlag(1, false)
                saveReverseMassage(1, null)
                textLoading.value = "Sending Transaction"
                delay(1_500)
                responseFromHost =
                    TransportData().sendOnlineTransaction(
                        requestToHost,
                        connectionStatus,
                        hasError,
                        errMessage,
                        textLoading
                    )
            }

            if (hasError.value) { // communication error
                if (!host.reversal_flag!! &&
                    connectionStatus.value
                ) {// if haven't flag
                    setReverseFlag(1, true)
                    saveReverseMassage(1, reversalMessage)
                }
                loop = 0

            } else { // communication success
                var meaningShow = ""
                val jsonResponse =
                    ISO8583Extracting().extractISO8583TOJSON(
                        responseFromHost
                    )

                if (if (jsonResponse.has("res_code")) {
                        jsonData.value.put("res_code", jsonResponse.getString("res_code"))
                        val (isApprove, meaning) = ISO8583Extracting().checkBit39Payload(
                            jsonResponse.getString("res_code")
                        )
                        meaningShow = meaning.toString()
                        Log.v(
                            "TEST",
                            "Approve: $isApprove Meaning: $meaning"
                        )
                        textLoading.value = "Status is: $meaning"
                        delay(1_700)
                        val transactionIsApprove = isApprove as Boolean
                        transactionIsApprove

                    } else {
                        false
                    }
                ) { // host approve
                    setReverseFlag(1, false)
                    saveReverseMassage(1, null)
                    updateStan(1)

                    if (reversalFlag) { // if reversal has flag
                        reversalFlag = false
                    } else { // if reversal hasn't flag
                        responseOnlineStatus.value = true
                        //dialogStatus.value = false
                        jsonData.value = getInformationTosaveTransaction(
                            jsonData.value,
                            jsonResponse
                        )
                        if(jsonData.value.getString("transaction_type") == "sale"){
                            saveTransaction(jsonData.value)
                        }else{
                            withContext(Dispatchers.Default) {updateTransaction(jsonData.value)}
                        }

                        dialogStatus.value = false
                        //val transaction = TransactionHistoryRO()
                        /*printSlipTransaction(
                            jsonData.value, context, errMessage, loading,
                            textLoading, arksToPrintStatus, arksToPrintStatus
                        )*/
                        //navController.popBackStack()
                    }

                } else { // host not approve
                    updateStan(1)
                    hasError.value = true
                    errMessage.value =
                        "Transaction not approve because:$meaningShow"
                }
            }
            loop -= 1
        }


    } catch (e: Exception) {
        hasError.value = true
        if (errMessage.value == "") {
            errMessage.value = e.toString()
        }
    }

}