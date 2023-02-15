package com.emc.edc.database

import io.realm.RealmObject

open class ConfigTerminalRO(
    var serial_number: String? = "01234567899876543210",
    var trace_invoice: Int = 1,
    var merchant_name: String? = "",
    var merchant_location: String? = "",
    var merchant_convince: String? = "",
    var app_name: String? = "magnetic payment App",
    var app_version: String? = "0.1.0",
    var app_password: String? = null,
    var app_check_sum: String? = null,
    var config_tcp_using: String? = null,
    var my_ip: String? = null,
    var my_subnet_mask: String? = null,
    var my_gateway: String? = null,
    var com1_enable: String? = null,
    var com1_buad_rate: String? = null,
    var com1_data_bit: String? = null,
    var com1_parity_bit: String? = null,
    var com1_stop_bit: String? = null,
    var com1_datetime: String? = null,
    var com1_alarm_setting: String? = null,
//    var com1_alarm_datetime: String? = null,
) : RealmObject() {
    fun isList(type : String) : Boolean = when (type) {
        "" -> true
        else -> false
    }
    fun getType(type : String) : Class<*> = when (type) {
        "trace_invoice",
        -> Int::class.java
        else -> String::class.java
    }
}