package com.emc.edc.database

import io.realm.RealmObject

open class DataSettingRO(
    var enable_darkmode: Boolean = false,
    var enable_print_test: Boolean = false,
    var enable_print_slip: Boolean = true,
    var ver_schema_config_data: Long = 0,
    var ver_schema_transaction_data: Long = 0,
    var allow_swipe_card: Boolean = true,
    var allow_insert_card: Boolean = true,
    var allow_pass_card: Boolean = true
) : RealmObject() {
    fun isList(type : String) : Boolean = when (type) {
        "" -> true
        else -> false
    }
    fun getType(type : String) : Class<*> {
        return when (type) {
            "enable_darkmode", "enable_print_test", "enable_print_slip",
            "allow_swipe_card", "allow_insert_card", "allow_pass_card"-> {
                Boolean::class.java
            }
            "ver_schema_config_data", "ver_schema_transaction_data" -> {
                Long::class.java
            }
            else -> {
                String::class.java
            }
        }
    }
}