package com.emc.edc.ui.utils

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.emc.edc.getCardControl
import com.emc.edc.getTraceInvoice
import com.emc.edc.selectCardData
import com.emc.edc.selectHost
import kotlinx.coroutines.DelicateCoroutinesApi
import org.json.JSONObject


fun getHostInformationFromCard(
    transactionType: String,
    jsonData: JSONObject,
    hasError: MutableState<Boolean>,
    errorMessage: MutableState<String>,
    ): JSONObject {
    //val hasError = mutableStateOf(false)
    //val description = mutableStateOf("")
    var cardData = selectCardData(jsonData.getString("card_number"))
    var host = selectHost(cardData!!.host_record_index!!)
    //cardData = null
    if (cardData != null) {
        val cardControl =
            getCardControl(cardData.card_control_record_index!!, transactionType!!)
        Log.d("test", "card data: $cardData")
        Log.d("test", "card control: $cardControl")
        if (cardControl != null && jsonData != null) {
            if (cardControl.checkAllow(transactionType)!!) {
                jsonData.put("card_record_index", cardData.card_record_index!!)
                jsonData.put("card_label", cardData.card_label)
                jsonData.put("card_scheme_type", cardData.card_scheme_type)
                jsonData.put("pan_masking", cardControl.pan_masking)
                jsonData.put("host_record_index", cardData.host_record_index!!)
                Log.v("TEST", "host: $host")

                if (host != null) {
                    jsonData.put("tid", host.terminal_id)
                    jsonData.put("mid", host.merchant_id)
                    jsonData.put("nii", host.nii)
                    jsonData.put("stan", host.stan)
                    jsonData.put("ip_address1", host.ip_address1)
                    jsonData.put("port1", host.port1)
                    jsonData.put("host_define_type", host.host_define_type)
                    jsonData.put("host_label", host.host_label_name)
                    jsonData.put("batch_number", host.last_batch_number)
                    jsonData.put("invoice", getTraceInvoice())
                }
            } else {
                hasError!!.value = true
                errorMessage!!.value =
                    "Not allow to ${jsonData.getString("transaction_type")}"
                Log.v("TEST", "not allow to ${jsonData.getString("transaction_type")}")
            }
        } else {
            hasError!!.value = true
            errorMessage!!.value = "Not found card control"
            Log.v("TEST", "not found card control")
        }
    } else {
        hasError!!.value = true
        errorMessage!!.value = "Card not support"
        Log.v("TEST", "not support")
    }
    return jsonData
}

