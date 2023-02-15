package com.emc.edc.database

import io.realm.RealmObject

open class ProcessingCodeDataRO(
    var acc: String = "",
    var code: Int? = null,
) : RealmObject() {
    fun isList(type : String) : Boolean = when (type) {
        "" -> true
        else -> false
    }
    fun getType(type : String) : Class<*> = when (type) {
        "code",
        -> Int::class.java
        else -> String::class.java
    }
}