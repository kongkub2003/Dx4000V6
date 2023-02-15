package com.emc.edc.database

import io.realm.RealmObject

open class ConfigHostRO(
    var host_record_index: Int = 0,
    var host_define_type: Int? = 0,
    var stan: Int = 1,
    var host_acquieretype: String? = "",
    var host_label_name: String? = "",
    var terminal_id: String? = "",
    var merchant_id: String? = "",
    var nii: String? = "",
    var ip_address1: String? = null,
    var ip_address2: String? = null,
    var ip_address3: String? = null,
    var ip_address4: String? = null,
    var port1: Int? = null,
    var port2: Int? = null,
    var port3: Int? = null,
    var port4: Int? = null,
    var reversal_flag:  Boolean? = false,
    var reversal_msg: String? = null,
    var last_batch_number: Int? = 1,
    var logo_file: String? = "",
    var acquire_id: Int? = 0,
    var pre_auth_max_day: Int? = 15,
) : RealmObject() {
    fun isList(type : String) : Boolean = when (type) {
        "" -> true
        else -> false
    }
    fun getType(type : String) : Class<*> = when (type) {
        "host_record_index", "host_define_type", "stan", "port1", "port2", "port3", "port4",
        "last_batch_number", "acquie_id", "pre_auth_max_day",
        -> Int::class.java
        "reversal_flag",
        -> Boolean::class.java
        else -> String::class.java
    }
}