package th.emerchant.terminal.edc_pos.screen.transaction.key_in

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.navigation.NavController
import com.emc.edc.*
import com.emc.edc.ui.card_entry.ShowLoading
import kotlinx.coroutines.DelicateCoroutinesApi
import org.json.JSONObject

import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@DelicateCoroutinesApi
fun confirmKeyIn(
    context: Context?,
    navController: NavController,
    jsonData: JSONObject,
    card_number: MutableState<String>,
    month: MutableState<String>,
    year: MutableState<String>,
    cardError: MutableState<Boolean>,
    monthError: MutableState<Boolean>,
    yearError: MutableState<Boolean>,
    checkErrorToContinue: MutableState<Boolean>,
    messageErrorToContinue: MutableState<String>,
    hasError: MutableState<Boolean>,
    description: MutableState<String>
) {
    Log.d("TEST", jsonData.toString())


    val getCurrentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
    val getCurrentYear = Calendar.getInstance().get(Calendar.YEAR) % 100






    if (card_number.value == "" || month.value == "" || year.value == "") {
        messageErrorToContinue.value = "Please check empty field"
        cardError.value = true
        monthError.value = true
        yearError.value = true
        checkErrorToContinue.value = true
    } else if (cardError.value || monthError.value || yearError.value) {
        messageErrorToContinue.value = "Please check error field"
        checkErrorToContinue.value = true
//        Toast.makeText(context, "Please Check error on screen", Toast.LENGTH_SHORT).show()
    } else {
        Log.d("Test", getCurrentYear.toString())
        if (year.value.toInt() <= getCurrentYear) {
            if (month.value.toInt() <= getCurrentMonth) {
                messageErrorToContinue.value = "Card is expire"
                monthError.value = true
                yearError.value = true
                checkErrorToContinue.value = true
            } else {
                checkErrorToContinue.value = false
            }
        } else {
            checkErrorToContinue.value = false
        }
    }

    if (!checkErrorToContinue.value) {
        checkErrorToContinue.value = false
        val cardData = selectCardData(card_number.value)

        if (cardData != null) {
            val cardControl = getCardControl(
                cardData.card_control_record_index!!,
                jsonData.getString("transaction_type")
            )
            Log.d("test", "card data: $cardData")
            Log.d("test", "card control: $cardControl")
            if (cardControl != null) {
                if (cardControl.checkAllow(jsonData.getString("transaction_type"))!!) {
                    jsonData.put("card_exp", year.value + month.value)
                    jsonData.put("card_number", card_number.value)
                    jsonData.put("card_label", cardData.card_label)
                    jsonData.put("card_scheme_type", cardData.card_scheme_type)
                    jsonData.put("pan_masking", cardControl.pan_masking)
                    jsonData.put("card_record_index", cardData.card_record_index)
                    jsonData.put("host_record_index", cardData.host_record_index)
                    jsonData.put("operation", "key_in")
                    jsonData.put("pos_entry_mode", "022")
                }
                val cardData = selectCardData(jsonData.getString("card_number"))
                val host = selectHost(cardData?.host_record_index!!)

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


                    navController.navigate(Route.CardDisplay.route + "/$jsonData") {
                        popUpTo(Route.Home.route)
                    }
                } else {
                    hasError.value = true
                    description.value = "Not allow to ${jsonData.getString("transaction_type")}"
                    Log.v("TEST", "not allow to ${jsonData.getString("transaction_type")}")
                }
            } else {
                hasError.value = true
                description.value = "Not found card control"
                Log.v("TEST", "not found card control")
            }
        }
    }
}


