package com.emc.edc.ui.search_transaction

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.navigation.NavHostController
import com.emc.edc.*


@RequiresApi(Build.VERSION_CODES.O)
fun searchTransacitionButtonClick(
    context: Context?,
    txt: String,
    invoice: MutableState<String>,
    navController: NavHostController,
    transactionType: String,
    transactionTitle: String,
    errMessage: MutableState<String>,
    loading: MutableState<Boolean>,
    textLoading: MutableState<String>,
) {
//    Log.v("TEST","numpad: $txt")
    if (txt=="✓") {
        if (invoice.value != "0"){
            textLoading.value = "Searching Transaction..."
            loading.value = true
            Log.v("TEST","start search transaction[${invoice.value}]")
            val transaction = searchTransaction(invoice.value)
            Log.v("TEST","transaction: $transaction")
            if (transaction != null) {
                textLoading.value = "Checking Transaction..."
                if (checkAllowMenuEntryTransaction(transactionType, transaction.txn_type!!)
                    || transactionTitle=="reprint"
                ){
                    val jsonData = if (transactionTitle=="reprint") transactionToJson(transaction.txn_type!!, transaction)
                    else transactionToJson(transactionType, transaction)
                    val cardData = selectCardData(jsonData.getString("card_number"))
                    if (cardData != null) {
                        val cardControl = getCardControl(cardData.card_control_record_index!!, jsonData.getString("transaction_type"))
                        if (transactionType == "void_sale" || transactionType == "void_refund"){
                            if (cardData != null && cardControl != null) {
                                jsonData.put("title", transactionTitle)
                                jsonData.put("menu_entry_txn", transactionType)
                                jsonData.put("card_label", cardData.card_label)
                                jsonData.put("card_scheme_type", cardData.card_scheme_type)
                                jsonData.put("pan_masking", cardControl.pan_masking)
                                jsonData.put("host_record_index", cardData.host_record_index!!)
                                jsonData.put("card_number_mask", transaction.card_number)

                                navController.navigate("${Route.TransactionDisplay.route}/$jsonData") {
                                    popUpTo(Route.Home.route)
                                }
                            }
                            else{
                                Toast.makeText(context, "Get transaction data fail", Toast.LENGTH_SHORT).show()
                            }
                        }else{
                            if (cardData != null && cardControl != null) {
                                jsonData.put("title", transactionTitle)
                                jsonData.put("menu_entry_txn", transactionType)
                                jsonData.put("card_label", cardData.card_label)
                                jsonData.put("card_scheme_type", cardData.card_scheme_type)
                                jsonData.put("pan_masking", cardControl.pan_masking)
                                jsonData.put("host_record_index", cardData.host_record_index!!)
                                jsonData.put("card_number_mask", transaction.card_number)

                                navController.navigate("${Route.TipAdjust.route}/$jsonData") {
                                    popUpTo(Route.Home.route)
                                }
                            }
                            else{
                                Toast.makeText(context, "Get transaction data fail", Toast.LENGTH_SHORT).show()
                            }
                        }

                    }
                    /*val cardControl = getCardControl(transaction.card_record_index!!, transaction.txn_type!!)
                    if (cardData != null && cardControl != null) {
                        jsonData.put("title", transactionTitle)
                        jsonData.put("menu_entry_txn", transactionType)
                        jsonData.put("card_label", cardData.card_label)
                        jsonData.put("card_scheme_type", cardData.card_scheme_type)
                        jsonData.put("pan_masking", cardControl.pan_masking)
                        jsonData.put("host_record_index", cardData.host_record_index!!)
                        jsonData.put("card_number_mask", transaction.card_number)

                        navController.navigate("${Route.TransactionDisplay.route}/$jsonData") {
                            popUpTo(Route.Home.route)
                        }
                    }
                    else{
                        Toast.makeText(context, "Get transaction data fail", Toast.LENGTH_SHORT).show()
                    }*/
                    Log.v("test","transaction: $jsonData")
                }
                else{
                    loading.value = false
                    Toast.makeText(context, "Transacton at invoice ${invoice.value} can't $transactionType", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                loading.value = false
                Toast.makeText(context, "Not found invoice ${invoice.value}", Toast.LENGTH_SHORT).show()
            }
        }
        else {
            loading.value = false
            Toast.makeText(context, "This invoice must more than 0", Toast.LENGTH_SHORT).show()
        }
    }
    else if (txt=="⌫" && invoice.value.isNotEmpty()) {
        if (invoice.value.length == 1){
            invoice.value = "0"
        }
        else {
            invoice.value = invoice.value.dropLast(1)
        }
    }
    else if (txt!="⌫" && txt!="✓" && invoice.value.length < 9 ){
        if (invoice.value == "0"){
            invoice.value = txt
        }
        else {
            invoice.value += txt
        }
    }
//    Log.v("TEST","amount ${formatAmount(raw_amount.value)}")
}