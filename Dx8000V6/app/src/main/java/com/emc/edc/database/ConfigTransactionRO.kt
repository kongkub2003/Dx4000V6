package com.emc.edc.database

import io.realm.RealmList
import io.realm.RealmObject

open class ConfigTransactionRO(
    var type: String? = "",
    var processing_code: RealmList<String>? = RealmList<String>(),
    var reversal: Boolean? = false,
    var msgType: String? = "",
    var bitmap: RealmList<DataBitmapRO>? = RealmList<DataBitmapRO>(),
) : RealmObject() {
    fun isList(type : String) : Boolean = when (type) {
        "processing_code","bitmap" -> true
        else -> false
    }
    fun getType(type : String) : Class<*> = when (type) {
        "processing_code",
        -> String::class.java
        "bitmap",
        -> DataBitmapRO()::class.java
        "reversal",
        -> Boolean::class.java
        else -> String::class.java
    }
}