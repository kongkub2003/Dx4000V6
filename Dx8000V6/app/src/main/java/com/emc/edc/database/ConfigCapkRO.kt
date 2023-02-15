package com.emc.edc.database

import io.realm.RealmObject

open class ConfigCapkRO(
    var rid: String? = null,
    var capk_index: String? = null,
    var mod: String? = null,
    var exponent: String? = null,
    var issuer: String? = null,
    var key_length: Int? = null,
    var sha1: String? = null,
    var exp: String? = null,
) : RealmObject() {
    fun isList(type : String) : Boolean = when (type) {
        "" -> true
        else -> false
    }
    fun getType(type : String) : Class<*> = when (type) {
        "key_length"
        -> Int::class.java
        else -> String::class.java
    }
}