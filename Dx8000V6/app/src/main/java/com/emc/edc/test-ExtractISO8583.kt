package com.emc.edc

import com.emc.edc.utils.ISO8583Extracting
import org.json.JSONObject

fun testExtractISO8583(): JSONObject {
    //val data = "00, 46, 60, 80, 00, 01, 20, 02, 10, 30, 38, 01, 00, 0e, 80, 00, 04, 00, 30, 00, 00, 00, 00, 01, 00, 00, 00, 00, 21, 15, 37, 04, 11, 14, 01, 20, 54, 45, 53, 54, 31, 35, 30, 30, 30, 30, 32, 31, 35, 36, 33, 34, 38, 34, 30, 30, 31, 31, 31, 31, 31, 31, 31, 31, 00, 06, 30, 30, 30, 30, 31, 38"

    // Test Extract data
    var arraylist = ArrayList<String>()

    // test every bit
    //var data = "00F460012080000200703C05802EE91E1F0612345612345800000010000012345666666643213333654011112209987654321F123456789123456789000000123456123456303001234567891234563031303130313031303130313031303033303030303130310303103030303030303030303030353635303132353634363534313232353640303330303030313031030310303030303030303030303035363530313235363436353431323235360004303130311234567812345678123456781234567800053031303130000412345678000598745612300005987456123000059874561230000530323031341234567812345678"
    //var data ="0023602548791502007000000000000000193456789265356782169F065423000152609085"

    // test-bit 2
    var data = "00136001208000020040000000000000000512345F"

    // test-bit 3
    //var data = "0012600120800002002000000000000000003000"

    //test-bit 4
    //var data = "0015600120800002001000000000000000000000100000"

    // test-bit 11
    //var data = "0012600120800002000020000000000000123456"

    // test-bit 12
    //var data ="0012600120800002000010000000000000666666"

    // test-bit 13
    //var data = "00116001208000020000080000000000004321"

    // test-bit 14
    //var data = "00116001208000020000040000000000003333"

    // test-bit 22
    //var data = "00116001208000020000004000000000006540"

    // test-bit 24
    //var data = "00116001208000020000000100000000001111"

    // test-bit 25
    //var data = "001060012080000200000000800000000022"

    // test-bit 35
    //var data = "001560012080000200000000002000000009987654321F"

    // test-bit 37
    //var data = "001B600120800002000000000008000000123456789123456789000000"

    // test-bit 38
    //var data = "0015600120800002000000000004000000123456123456"

    // test-bit 39
    //var data = "00116001208000020000000000020000003030"

    // test-bit 41
    //var data = "00176001208000020000000000008000000123456789123456"

    // test-bit 42
    //var data = "001E600120800002000000000000400000303130313031303130313031303130"

    // test-bit 43
    //var data = "003760012080000200000000000020000030333030303031303103031030303030303030303030303536353031323536343635343132323536"

    // test-bit 45
    //var data = "00386001208000020000000000000800004030333030303031303103031030303030303030303030303536353031323536343635343132323536"

    // test-bit 48
    //var data = "0015600120800002000000000000010000000430313031"

    // test-bit 52 --check
    //var data = "00176001208000020000000000000010001234567812345678"

    // test-bit 53
    //var data = "00176001208000020000000000000008001234567812345678"

    // test-bit 54
    //var data = "001660012080000200000000000000040000053031303130"
    //var data = "0093600120800002000000000000000400013098745612311111111111111111111111111111111111111111111111111111111222222222222222222222222222222222222222222222222222222222222222333333333333333333333333333333333655555555555555555555551111111111111111111111111111111111111111111555555555555555555555555555555551" // test error


    // test-bit 55
    //var data = "0015600120800002000000000000000200000412345678"

    // test-bit 60
    //var data = "001660012080000200000000000000001000059874561230"

    // test-bit 61
    //var data = "001660012080000200000000000000000800059874561230"

    // test-bit 62
    //var data = "001660012080000200000000000000000400059874561230"

    // test-bit 63
    //var data = "001660012080000200000000000000000200059874561230"

    // test-bit 64 -- check
    //var data = "00176001208000020000000000000000011234567812345678"



    for (i in data.indices step 2) {
        arraylist.add(data[i].toString() + data[i+1].toString())
        i + 2
    }

    /*var arraylist = ArrayList<String>()
    val list = listOf("00", "46", "60", "80", "00", "01", "20", "02", "10", "30", "38", "01", "00", "0e", "80", "00", "04", "00", "30", "00", "00", "00", "00", "01", "00", "00", "00", "00", "21", "15", "37", "04", "11", "14", "01", "20", "54",
        "45", "53", "54", "31", "35", "30", "30", "30", "30", "32", "31", "35", "36", "33", "34", "38", "34", "30", "30", "31", "31", "31", "31", "31", "31", "31", "31", "00", "06", "30", "30", "30", "30", "31", "38")

    for (i in list.indices) {
        arraylist.add(list[i])
    }*/

   // val testExtraction =  testIso8583Extract(arraylist)
    return ISO8583Extracting().extractISO8583TOJSON(arraylist)
}