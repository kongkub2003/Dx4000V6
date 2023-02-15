package com.emc.edc.ui.card_entry

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.navigation.NavController

import com.emc.edc.emv.Emv
import com.usdk.apiservice.aidl.emv.CandidateAID
import kotlinx.coroutines.CoroutineScope
import org.json.JSONObject

fun startTrade(
    jsonData: MutableState<JSONObject>,
    hasError: MutableState<Boolean>,
    errorMess: MutableState<String>,
    navController: NavController,
    seletAIDPopup: MutableState<Boolean>,
    aidList: MutableList<String>,
    aidOriginalList: MutableList<List<CandidateAID>>,
    cardCanDoOnlineStatus: MutableState<Boolean>,
    requestOnlineStatus: MutableState<Boolean>,
    responseOnlineStatus: MutableState<Boolean>,
    endProcessStatus: MutableState<Boolean>,
    hasEMVStartAgain: MutableState<Boolean>,
    hasEMVStartAgainConfirm: MutableState<Boolean>,
) {
    try {
        val emvProcess = Emv(
            jsonData,
            hasError,
            errorMess,
            navController,
            seletAIDPopup,
            aidList,
            aidOriginalList,
            cardCanDoOnlineStatus,
            requestOnlineStatus,
            responseOnlineStatus,
            endProcessStatus,
            hasEMVStartAgain,
            hasEMVStartAgainConfirm
        )

        Log.v("EMV","emv start: $jsonData")
        emvProcess.startEMV()

    } catch (e: Exception) {
        Log.e("TEST", "card is $e")
        errorMess.value = "Error swipe card: $e"
        hasError.value = true
    }
}
