package com.emc.edc.ui.transaction_display


import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import com.emc.edc.*
import com.emc.edc.ui.card_entry.cardEntryOnlineAndOffline
import com.emc.edc.utils.Printer
import kotlinx.coroutines.*
import org.json.JSONObject

@RequiresApi(Build.VERSION_CODES.O)
@DelicateCoroutinesApi
suspend fun transactionDisplayConfirm(
    jsonData: JSONObject,
    transactionType: String,
    navController: NavHostController,
    connectionStatus: MutableState<Boolean>,
    hasError: MutableState<Boolean>,
    errMessage: MutableState<String>,
    responseOnlineStatus: MutableState<Boolean>,
    context: Context,
    popupPrintSlipCustomer: MutableState<Boolean>,
    dialogStatus: MutableState<Boolean>,
    textLoading: MutableState<String>,
    popUpContinue: MutableState<Boolean>,
) {
    if (jsonData.getString("title") == "reprint") {
        textLoading.value = "Printing..."
        jsonData.put("ref_num", jsonData.getString("ref_number"))
        jsonData.put("auth_id", jsonData.getString("approve_code"))

        Printer().printSaleSlip(
            jsonData,
            context,
            "merchant"
        )

        dialogStatus.value = false
        textLoading.value = ""

        popupPrintSlipCustomer.value = true
        popUpContinue.value = true
    } else if (jsonData.getString("menu_entry_txn") == "sale_complete") {
        navController.navigate("${Route.EditAmount.route}/$jsonData") {
            popUpTo(Route.Home.route)
        }
    } else {
        //TODO("pos_entry_mode should save to database")
        jsonData.put("pos_entry_mode", "022")
        val jsonData =  mutableStateOf(jsonData)

        /*onlineTransaction(
            navController, hasError, errMessage,
            jsonData, dialogStatus, connectionStatus, textLoading
        )*/
        cardEntryOnlineAndOffline(
            navController, hasError, errMessage, jsonData,
            responseOnlineStatus, dialogStatus, connectionStatus, textLoading
        )
    }

}


