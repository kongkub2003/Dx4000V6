package com.emc.edc.ui.utils

import com.emc.edc.utils.Utils
import org.json.JSONObject
import java.util.*

fun getInformationTosaveTransaction(jsonData : JSONObject, jsonResponse : JSONObject) : JSONObject{
    jsonData.put("datetime_from_host", true)
    jsonData.put(
        "date",
        (1900 + Date().year).toString() + jsonResponse.getString(
            "date"
        )
    )
    jsonData.put("time", jsonResponse.getString("time"))
    jsonData.put("transaction_status", "online")
    jsonData.put(
        "res_code",
        jsonResponse.getString("res_code")
    )
    jsonData.put(
        "auth_id",
        jsonResponse.getString("auth_id")
    )
    jsonData.put(
        "ref_num",
        jsonResponse.getString("ref_num")
    )
    jsonData.put(
        "invoice_num",
        if (jsonResponse.has("invoice"))
            jsonResponse.getString("invoice").toInt().toString()
        else jsonData.getString("invoice")
    )
    jsonData.put(
        "card_number_mask",
        Utils().cardMasking(
            jsonData.getString("card_number"),
            jsonData.getString("pan_masking")
        )
    )
    return jsonData
}