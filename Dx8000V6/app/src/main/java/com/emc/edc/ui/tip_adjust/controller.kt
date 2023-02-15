package th.emerchant.terminal.edc_pos.screen.transaction.tip_adjust

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.navigation.NavController
import com.emc.edc.Route
import com.emc.edc.utils.TransportData
import com.emc.edc.utils.Utils
import org.json.JSONObject


@RequiresApi(Build.VERSION_CODES.O)
suspend fun tipAmountHandleButtonClick(
    context: Context?,
    txt: String,
    rawTipAmount: MutableState<String>,
    tipAmount: MutableState<String>,
    navController: NavController,
    jsonData: JSONObject,
    errMessage: MutableState<String>,
    loading: MutableState<Boolean>,
    textLoading: MutableState<String>,
    popupPrintSlipCustomer: MutableState<Boolean>,
    popUpContinue: MutableState<Boolean>,
) {
//    Log.v("TEST","numpad: $txt")
    if (txt=="✓") {
        val originalAmount = jsonData.getDouble("amount")
        val totalAmount = originalAmount+tipAmount.value.toDouble()
        if (tipAmount.value.toDouble() < 1) {
            Toast.makeText(context, "This amount is less than 1 baht", Toast.LENGTH_SHORT).show()
            Log.v("TEST","amount ${rawTipAmount.value} not passed")
        }
        else if (totalAmount >= 1 && totalAmount < 10000000000){
            Log.v("TEST","amount ${tipAmount.value} passed")
            jsonData.put("original_amount",Utils().formatMoney(Utils().formatMoneyD2S(originalAmount)))
            jsonData.put("additional_amount",tipAmount.value)
            jsonData.put("amount",Utils().formatMoney(Utils().formatMoneyD2S(totalAmount)))
            Log.v("test","confirm amount: $jsonData")
            navController.navigate("${Route.ConfirmTipAdjust.route}/$jsonData") {
                popUpTo(Route.Home.route)
            }
        }
        else if (totalAmount < 10000000000){
            Toast.makeText(context, "Max tip amount is less than " +
                    Utils().formatMoney(Utils().formatMoneyD2S(10000000000-originalAmount)) +
                    " baht", Toast.LENGTH_SHORT).show()
            Log.v("TEST","amount ${rawTipAmount.value} not passed")
        }
    }
    else if (txt=="⌫" && rawTipAmount.value.isNotEmpty()) {
        rawTipAmount.value = rawTipAmount.value.dropLast(1)
        tipAmount.value = Utils().formatMoney(rawTipAmount.value)
    }
    else if (txt!="⌫" && txt!="✓" && rawTipAmount.value.length < 12 &&
        !(txt=="0" && rawTipAmount.value=="0")) {
        if (rawTipAmount.value=="0") {
            rawTipAmount.value = txt
        }
        else{
            rawTipAmount.value += txt
        }
        tipAmount.value = Utils().formatMoney(rawTipAmount.value)
    }
//    Log.v("TEST","amount ${formatAmount(raw_amount.value)}")
}

@RequiresApi(Build.VERSION_CODES.O)
suspend fun confirmTipAmountButtonClick(
    context: Context?,
    navController: NavController,
    jsonData: JSONObject,
    errMessage: MutableState<String>,
    loading: MutableState<Boolean>,
    textLoading: MutableState<String>,
    popupPrintSlipCustomer: MutableState<Boolean>,
    popUpContinue: MutableState<Boolean>,
) {
    /*val responeSaleComplete = TransportData.sendOnline(jsonData, context!!, errMessage, loading,
        textLoading, popupPrintSlipCustomer, popUpContinue
    )
    Log.v("test","respone sale complete: $responeSaleComplete")
    navController.popBackStack()*/
}