package com.emc.edc.database

import io.realm.RealmList
import io.realm.RealmObject

open class ProcessingCodeRO(
    var name: String = "",
    var data: RealmList<ProcessingCodeDataRO> = RealmList<ProcessingCodeDataRO>(),
) : RealmObject() {
    fun isList(type : String) : Boolean = when (type) {
        "data" -> true
        else -> false
    }
    fun getType(type : String) : Class<*> = when (type) {
        "data",
        -> ProcessingCodeDataRO()::class.java
        else -> String::class.java
    }
}