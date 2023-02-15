package com.emc.edc.database

import io.realm.RealmObject

open class TemplatePrintSlipRO(
    var logo_path: String? = null
) : RealmObject() {
    fun isList(type : String) : Boolean = when (type) {
        "" -> true
        else -> false
    }
    fun getType(type : String) : Class<*> {
        return when (type) {
            "" -> {
                Boolean::class.java
            }
            else -> {
                String::class.java
            }
        }
    }
}