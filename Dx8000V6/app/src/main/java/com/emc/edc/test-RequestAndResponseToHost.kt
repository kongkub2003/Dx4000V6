package com.emc.edc
/*

import androidx.compose.runtime.mutableStateOf
import com.emc.edc.utils.ISO8583Extracting
import com.emc.edc.utils.TransportData
import com.emc.edc.utils.buildISO8583
import kotlinx.coroutines.runBlocking
import org.json.JSONObject

fun testRequestToHost() {

    // Send data to host
    val jsonData = JSONObject()
    jsonData.put("transaction_type", "sale")
    jsonData.put("operation", "magnetic")
    jsonData.put("card_number", "4552051008195072") // --bit 2
    jsonData.put("amount", "100.00") // -- bit 4
    jsonData.put("stan", 21) // -- bit 11
    jsonData.put("time",1234) // -- bit 12
    jsonData.put("date", 1235) // -- bit 13
    jsonData.put("card_exp",1234) // -- bit 14
    jsonData.put("nii", "120") // -- bit 24
    jsonData.put("track2", "4552051008195072D2704201000004788765") // -- bit 35
    jsonData.put("ref_number","123456789123") // -- bit 37
    jsonData.put("approve_code", "123456") // -- bit 38
    jsonData.put("response_code", "23") // -- bit 39
    jsonData.put("tid", "11111111") // -- bit 41
    jsonData.put("mid", "111111111111111") // -- bit 42
    jsonData.put("track1", "B0004100023908209^HONDA PRIVILEGE^22121031199200000000  204      ") // bit 45
    jsonData.put("additional_amount", "123456") // -- bit 54
    jsonData.put("invoice", 18) // -- bit 62
    val errMessage = mutableStateOf("")
    val textLoading = mutableStateOf("")
    val onlineStatus = mutableStateOf(false)
    val dataISO8583 = buildISO8583(jsonData)
    val requestToHost = dataISO8583.getString("iso8583_payload")
    val communication = TransportData()
    val responseFromHost = runBlocking { communication.sendOnlineTransaction(
        requestToHost,
        // loading,
      //  popupPrintSlipCustomer,
      //  popUpContinue
    ) }
    val jsonResponse = ISO8583Extracting().extractISO8583TOJSON(responseFromHost)

}
*/
