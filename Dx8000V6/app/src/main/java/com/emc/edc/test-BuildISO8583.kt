package com.emc.edc

import android.util.Log
import com.emc.edc.utils.buildISO8583
import org.json.JSONObject

fun testBuildISO5853(): String {

    val jsonData = JSONObject()
    jsonData.put("transaction_type", "sale")
    jsonData.put("operation", "magnetic")
    //jsonData.put("card_number", "4552051008195072") // --bit 2
    //jsonData.put("amount", "100.00") // -- bit 4
    //jsonData.put("stan", 21) // -- bit 11
    //jsonData.put("time",1234) // -- bit 12
    //jsonData.put("date", 1235) // -- bit 13
    //jsonData.put("card_exp",1234) // -- bit 14
    //jsonData.put("nii", "120") // -- bit 24
    //jsonData.put("track2", "4552051008195072D2704201000004788765") // -- bit 35
    //jsonData.put("ref_number","123456789123") // -- bit 37
    //jsonData.put("approve_code", "123456") // -- bit 38
    //jsonData.put("response_code", "23") // -- bit 39
    //jsonData.put("tid", "11111111") // -- bit 41
    //jsonData.put("mid", "111111111111111") // -- bit 42
    //jsonData.put("track1", "B0004100023908209^HONDA PRIVILEGE^22121031199200000000  204      ") // bit 45
    //jsonData.put("additional_amount", "123456") // -- bit 54
    //jsonData.put("invoice", 18) // -- bit 62




    //----------------- test lost number------------------------------
    //jsonData.put("card_number", "4552051008195") // --bit 2
    //jsonData.put("amount", "1000") // -- bit 4
    //jsonData.put("stan", 1) // -- bit 11
    //jsonData.put("time",123) // -- bit 12
    //jsonData.put("date", 1234) // -- bit 13 ######### need to check
    //jsonData.put("card_exp",123) // -- bit 14
    jsonData.put("nii", "120") // -- bit 24
    //jsonData.put("track2", "4552051008195072D27042010000047887") // -- bit 35
    jsonData.put("ref_number","1234567891") // -- bit 37 //// แก้เป็น space ///length เกิน ขึ้น Error
    //jsonData.put("approve_code", "1234567") // -- bit 38   //ถ้าไม่ครบขึ้น error //length เกินขึ้น error
    //jsonData.put("response_code", "12") // -- bit 39 ######### need to check // เปลี่ยน 0 เป็น length
   // jsonData.put("tid", "11111111") // -- bit 41 ######### need to check // value > 8 error < value
   // jsonData.put("mid", "11111111111111") // -- bit 42 ######### need to check // > length จะขุึ้น error < length ต้องเขียนฟังก์ชั่นให้เติม 0
    //jsonData.put("track1", "    0004100023908209^HONDAPRIVILEGE^22121031199200000000204") // bit 45
    //jsonData.put("additional_amount", "12345") // -- bit 54
    //jsonData.put("invoice", 18) // -- bit 62













    //jsonData.put("name", "test taA")
    //jsonData.put("track1", "B0004100023908209^HONDA PRIVILEGE^22121031199200000000  204      ")
    //jsonData.put("track2", "4552051008195072D2704201000004788765")
    //jsonData.put("card_exp", "2704")
    //jsonData.put("card_number", "4222430030925070")
    //jsonData.put("card_record_index", 7)
    //jsonData.put("card_label", "KRUNGSRI")
    //.put("card_scheme_type", "VISA")
    //jsonData.put("pan_masking", "NNNN XXXX XXXX XNNN")
    //jsonData.put("host_record_index", 1)
    //jsonData.put("nii", "120")
    //jsonData.put("stan", 21)
    //jsonData.put("ip_address1", "210.1.57.103")
    //jsonData.put("port1", 5500)
    //jsonData.put("host_define_type", 1)
    //jsonData.put("host_label", "Host P'Lert")
    //jsonData.put("batch_number", 1)
    //jsonData.put("invoice", 18)




    val dataISO8583 = buildISO8583(jsonData)
    val requestToHost = dataISO8583.getString("iso8583_payload")
    Log.d("Test build ISO8583 : ", requestToHost)
    return requestToHost
}