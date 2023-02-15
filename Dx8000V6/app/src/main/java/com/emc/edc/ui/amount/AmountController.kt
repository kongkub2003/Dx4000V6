package com.emc.edc.ui.amount

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.navigation.NavHostController
import org.json.JSONObject
import com.emc.edc.Route
import com.emc.edc.utils.Utils

fun amountHandleButtonClick(
    context: Context?,
    txt: String,
    raw_amount: MutableState<String>,
    amount: MutableState<String>,
    navController: NavHostController,
    transactionType: String,
    transactionTitle: String
) {
//    Log.v("TEST","numpad: $txt")
    if (txt=="✓") {
        if (raw_amount.value.length >= 3){
            Log.v("TEST","amount ${raw_amount.value} passed")
            /*val data="{" +
                    "title: \"$transactionTitle\"," +
                    "amount:\"${Utils().formatMoney(raw_amount.value)}\"," +
                    "transaction_type: \"$transactionType\"," +
                    "operation: \"magnetic\"" +
                    "}"*/
            val data="{" +
                    "title: \"$transactionTitle\"," +
                    "amount:\"${Utils().formatMoney(raw_amount.value)}\"," +
                    "transaction_type: \"$transactionType\"" +
                    "}"
            val jsonData = JSONObject(data)
            navController.navigate("${Route.Select.route}/$jsonData") {
                popUpTo(Route.Home.route)
            }
        }
        else {
            Toast.makeText(context, "This amount is less than 1 baht", Toast.LENGTH_SHORT).show()
            Log.v("TEST","amount ${raw_amount.value} not passed")
        }
    }
    else if (txt=="⌫" && raw_amount.value.isNotEmpty()) {
        raw_amount.value = raw_amount.value.dropLast(1)
        amount.value = Utils().formatMoney(raw_amount.value)
    }
    else if (txt!="⌫" && txt!="✓" && raw_amount.value.length < 12 &&
        !(txt=="0" && raw_amount.value=="0")) {
        if (raw_amount.value=="0") {
            raw_amount.value = txt
        }
        else{
            raw_amount.value += txt
        }
        amount.value = Utils().formatMoney(raw_amount.value)
    }
//    Log.v("TEST","amount ${formatAmount(raw_amount.value)}")
}