package com.emc.edc.database

import io.realm.RealmObject

open class ConfigCardRangeRO(
    var card_record_index: Int? = 0,
    var low_range: Long? = 0,
    var high_range: Long? = 0,
    var card_number_length: Int? = 0
) : RealmObject() {
    fun isList(type : String) : Boolean = when (type) {
        "" -> true
        else -> false
    }
    fun getType(type : String) : Class<*> = when (type) {
        "card_record_index", "card_num_length"
        -> Int::class.java
        "low_range", "high_range"
        -> Long::class.java
        else -> String::class.java
    }
}