package com.emc.edc.database

import io.realm.RealmObject

open class ConfigCardRO(
    var card_record_index: Int? = 0,
    var low_prefix: String? = "",
    var high_prefix: String? = "",
    var card_label: String? = "",
    var card_type: String? = "",
    var min_length: Int? = 0,
    var max_length: Int? = 0,
    var check_digit_enable: Boolean? = false,
    var check_digit_type: Boolean? = false,
    var card_control_record_index: Int? = 0,
    var host_record_index: Int? = 0,
    var emv_aid: String? = "",
    var force_to_chip: Boolean? =false,
    var credit_debit_type: String? = "",
    var card_scheme_type: String? = "",
) : RealmObject() {
    fun isList(type : String) : Boolean = when (type) {
        "" -> true
        else -> false
    }
    fun getType(type : String) : Class<*> = when (type) {
        "card_record_index", "card_number_length", "card_control_record_index", "host_record_index",
        -> Int::class.java
        "check_digit_enable", "check_digit_type", "force_to_chip",
        -> Boolean::class.java
        else -> String::class.java
    }
}