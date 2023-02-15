package com.emc.edc

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import com.emc.edc.database.ConfigHostRO
import com.emc.edc.database.TransactionHistoryRO
import com.emc.edc.emv.DeviceHelper
import com.emc.edc.globaldata.dataclass.ISO8583
import com.emc.edc.utils.*
import com.usdk.apiservice.aidl.beeper.UBeeper
import com.usdk.apiservice.aidl.constants.RFDeviceName
import com.usdk.apiservice.aidl.led.Light
import com.usdk.apiservice.aidl.led.ULed
import com.usdk.apiservice.aidl.printer.PrinterError
import com.usdk.apiservice.aidl.printer.UPrinter
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.*

fun transactionToJson(transactionType: String?, transaction: TransactionHistoryRO): JSONObject{
    val host = transaction.host_record_index?.let { selectHost(it) }
    val cardData = transaction.card_record_index?.let { getCardData(it) }
    Log.v("TEST", "host: $host")
    Log.v("TEST", "card data: $cardData")

    val jsonData = JSONObject()

    if (host != null && cardData != null) {
        val amountDisplay = Utils().formatMoney(Utils().formatMoneyD2S(transaction.txn_amount!!))
        jsonData.put("time", transaction.time)
        jsonData.put("date", transaction.date)
        jsonData.put("record_number", transaction.record_number)
        jsonData.put("card_record_index", transaction.card_record_index)
        jsonData.put("name", transaction.card_holder_name)
        jsonData.put("card_label", cardData.card_scheme_type)
        jsonData.put("card_number", transaction.full_card_number)
        jsonData.put("card_exp", transaction.expire_date)
        jsonData.put("amount", amountDisplay)
        jsonData.put("additional_amount", amountDisplay)
        jsonData.put("transaction_type", transactionType)
        jsonData.put("operation", transaction.txn_operator)
        jsonData.put("tid", transaction.terminal_id)
        jsonData.put("mid", transaction.merchant_id)
        jsonData.put("nii", transaction.nii)
        jsonData.put("stan", host.stan)
        jsonData.put("ip_address1", host.ip_address1)
        jsonData.put("port1", host.port1)
        jsonData.put("host_record_index", host.host_record_index)
        jsonData.put("host_define_type", host.host_define_type)
        jsonData.put("host_label", host.host_label_name)
        jsonData.put("batch_number", transaction.batch_number!!.toInt())
        jsonData.put("invoice", transaction.invoice_number!!.toInt())
        jsonData.put("ref_number", transaction.reference_number)
        jsonData.put("approve_code", transaction.approval_code)
        jsonData.put("response_code", transaction.response_code)
    }

    return jsonData
}

fun saveOfflineTransaction(
    jsonData: JSONObject,
    context: Context,
    errMessage: MutableState<String>,
    loading: MutableState<Boolean>,
    textLoading: MutableState<String>,
    popupPrintSlipCustomer: MutableState<Boolean>,
    popUpContinue: MutableState<Boolean>,
): Boolean {
    return try {
        textLoading.value = "Printing..."
        val date = Date()
        jsonData.put("datetime_from_host", false)
        jsonData.put(
            "date",
            "${1900 + date.year}" +
                    "${if (1 + date.month < 10) "0${1 + date.month}" else 1 + date.month}" +
                    "${if (date.date < 10) "0${date.date}" else date.date}"
        )
        jsonData.put(
            "time",
            "${if (date.hours < 10) "0${date.hours}" else date.hours}" +
                    "${if (date.minutes < 10) "0${date.minutes}" else date.minutes}" +
                    "${if (date.seconds < 10) "0${date.seconds}" else date.seconds}"
        )
        jsonData.put("transaction_status", "offline")
        if (!jsonData.has("auth_id")) jsonData.put("auth_id", "")
        if (!jsonData.has("ref_num")) jsonData.put("ref_num", "")
        if (!jsonData.has("stan")) jsonData.put("stan", "")
        if (!jsonData.has("invoice_num")) jsonData.put("invoice_num", getTraceInvoice())
        if (!jsonData.has("card_number_mask"))
            jsonData.put(
                "card_number_mask",
                Utils().cardMasking(jsonData.getString("card_number"),
                    jsonData.getString("pan_masking"))
            )
        jsonData.put("res_code", "00")
        Log.d("Test", "JSON Data: $jsonData")
        saveTransaction(jsonData)
        Printer().printSaleSlip(
            jsonData,
            context,
            "merchant"
        )
        true
    } catch (e: Exception) {
        errMessage.value = "Offline sale failed - $e"
        false
    }
}

fun printSlipTransaction(
    jsonData: JSONObject,
    context: Context,
    errMessage: MutableState<String>,
    loading: MutableState<Boolean>,
    textLoading: MutableState<String>,
    popupPrintSlipCustomer: MutableState<Boolean>,
    popUpContinue: MutableState<Boolean>,
): Boolean{
    val printer: UPrinter = DeviceHelper.me().printer
    val beeper: UBeeper = DeviceHelper.me().beeper
    val led: ULed = DeviceHelper.me().getLed(RFDeviceName.INNER)
    /*val host = transaction.host_record_index?.let { selectHost(1) }
    val cardData = transaction.card_record_index?.let { getCardData( 1)}
    Log.v("TEST", "host: $host")

    val jsonData = JSONObject()


        jsonData.put("card_record_index", transaction.card_record_index)
        jsonData.put("name", transaction.card_holder_name)
        jsonData.put("card_label", cardData!!.card_scheme_type)
        jsonData.put("card_number", transaction.full_card_number)
        jsonData.put("card_exp", transaction.expire_date)
        jsonData.put("transaction_type", transaction.txn_type)
        jsonData.put("amount", Utils().formatMoney(Utils().formatMoneyD2S(transaction.txn_amount!!)))
        jsonData.put("operation", transaction.txn_operator)
        jsonData.put("tid", transaction.terminal_id)
        jsonData.put("mid", transaction.merchant_id)
        jsonData.put("nii", transaction.nii)
        jsonData.put("stan", transaction.stan)
        jsonData.put("ip_address1", host!!.ip_address1)
        jsonData.put("port1", host.port1)
        jsonData.put("host_record_index", host.host_record_index)
        jsonData.put("host_define_type", host.host_define_type)
        jsonData.put("host_label", host.host_label_name)
        jsonData.put("batch_number", transaction.batch_number)
        jsonData.put("invoice", transaction.invoice_number)
        jsonData.put("ref_number", transaction.reference_number)
        jsonData.put("date", transaction.date)
        jsonData.put("time", transaction.time)
        jsonData.put("res_code", transaction.response_code)
        jsonData.put("auth_id", transaction.approval_code)
        jsonData.put("ref_num", transaction.reference_number)
        jsonData.put("invoice_num", transaction.invoice_number)
        jsonData.put("card_number_mask", transaction.card_number)
        Log.d("Test", "JSON Data for print slip transaction: $jsonData")*/

        /*jsonData.put("card_record_index", transaction.card_record_index)
        jsonData.put("name", "sitthichai pro")
        jsonData.put("card_label", "cardData.card_scheme_type")
        jsonData.put("card_number", "XX11234")
        jsonData.put("card_exp", "1112")
        jsonData.put("transaction_type", "Sale")
        jsonData.put("amount", "326141")
        jsonData.put("operation", "sale")
        jsonData.put("tid", "1111")
        jsonData.put("mid", "3265")
        jsonData.put("nii", "1624")
        jsonData.put("stan", "524544")
        jsonData.put("ip_address1", "2515412")
        jsonData.put("port1", "41244")
        jsonData.put("host_record_index", 2)
        jsonData.put("host_define_type", 3)
        jsonData.put("host_label", "host1")
        jsonData.put("batch_number", 32)
        jsonData.put("invoice", 325)
        jsonData.put("ref_number", "sdfdf")
        jsonData.put("date", "date1214")
        jsonData.put("time", "time32165")
        jsonData.put("res_code", "0012")
        jsonData.put("auth_id", "2454")
        jsonData.put("ref_num", "254dfd")
        jsonData.put("invoice_num", "2254545")
        jsonData.put("card_number_mask", "NNNN XXXX XXXX XNNN")*/

        if (printer.status == PrinterError.SUCCESS) {
            //if (printer.status == PrinterError.SUCCESS) {
            Printer().printSaleSlip(
                jsonData,
                context,
                "merchant"
            )
            true
        } else {
            errMessage.value = "Printer Error"
            beeper.startBeep(500)
            false
        }
    return  true

}

fun checkAllowMenuEntryTransaction(transactionType: String, pervTransactionType: String): Boolean{
    return when (transactionType) {
        "void_sale" -> pervTransactionType == listOf("sale").find { txn -> pervTransactionType == txn }
        "void_refund" -> pervTransactionType == listOf("refund").find { txn -> pervTransactionType == txn }
        "sale_complete" -> pervTransactionType == listOf("pre_auth").find { txn -> pervTransactionType == txn }
        "offline_sale" -> pervTransactionType == listOf("offline_sale").find { txn -> pervTransactionType == txn }
        "tip_adjust" -> pervTransactionType == listOf("sale","ofline_sale").find { txn -> pervTransactionType == txn }
        else -> false
    }
}