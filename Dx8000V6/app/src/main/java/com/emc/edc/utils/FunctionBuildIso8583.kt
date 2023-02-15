package com.emc.edc.utils

import android.util.Log
import com.emc.edc.configDataRealmConfiguration
import io.realm.Realm
import io.realm.internal.SyncObjectServerFacade
import org.json.JSONObject

fun buildISO8583(data: JSONObject) : JSONObject {
    try {
        Log.v("TEST", "Data for build ISO $data")
        Realm.init(SyncObjectServerFacade.getApplicationContext())
        val realm = Realm.getInstance(configDataRealmConfiguration)
        val message = ISO8583Packing(realm=realm,
            transactionType = if (data.has("transaction_type")) data.getString("transaction_type") else null,
            operation = if (data.has("operation")) data.getString("operation") else null,
            cardNumber = if (data.has("card_number")) data.getString("card_number") else null,
            amount = if (data.has("amount")) data.getString("amount").replace(",", "") else null,
            cardEXP = if (data.has("card_exp")) data.getString("card_exp") else null,
            track1 = if (data.has("track1")) data.getString("track1") else null,
            track2 = if (data.has("track2")) data.getString("track2") else null,
            tid = if (data.has("tid")) data.getString("tid") else null,
            mid = if (data.has("mid")) data.getString("mid") else null,
            nii = if (data.has("nii")) data.getString("nii") else null,
            stan = if (data.has("stan")) data.getInt("stan") else null,
            invoice = if (data.has("invoice")) data.getInt("invoice") else null,
            refNumber = if (data.has("ref_number")) data.getString("ref_number") else null,
            approveCode = if (data.has("approve_code")) data.getString("approve_code") else null,
            responseCode = if (data.has("response_code")) data.getString("response_code") else null,
            additionalAmount = if (data.has("additional_amount")) data.getString("additional_amount") else null,
            originalAmount = if (data.has("original_amount")) data.getString("original_amount") else null,
            date = if (data.has("date")) data.getString("date").substring(data.getString("date").length-4) else null,
            time = if (data.has("time")) data.getString("time") else null,
            pos_entry_mode = if(data.has("pos_entry_mode")) data.getString("pos_entry_mode")else null,
        )
        Log.d("test xxxx", "Start")
        //Log.d("test xxxx", message.iso8583Payload())
        //Log.d("test xxxx", message.iso8583ReversalPayload())

        data.put("iso8583_payload", message.iso8583Payload())
        data.put("iso8583_reversal_payload", message.iso8583ReversalPayload())

        realm.close()

        return data
    } catch (e: Exception) {
        throw Exception("Build ISO8583 Function Error : "+e.message)
    }
}

