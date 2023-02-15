package com.emc.edc.database

import io.realm.RealmObject

open class ConfigHostDefineTypeRO(
    var host_define_type: Int? = 0,
    var host_define_type_name: String? = ""
) : RealmObject() {
    fun isList(type : String) : Boolean = when (type) {
        "" -> true
        else -> false
    }
    fun getType(type : String) : Class<*> = when (type) {
        "host_define_type"
        -> Int::class.java
        else -> String::class.java
    }
}