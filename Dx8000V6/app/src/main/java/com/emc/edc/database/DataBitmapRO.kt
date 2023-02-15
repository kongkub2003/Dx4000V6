package com.emc.edc.database

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.LinkingObjects

open class DataBitmapRO(
    var type: String = "",
    var bitmaps: RealmList<Int> = RealmList<Int>(),
) : RealmObject() {
    @LinkingObjects("bitmap")
    val transaction_type: RealmResults<ConfigTransactionRO>? = null
    fun isList(type : String) : Boolean = when (type) {
        "bitmaps" -> true
        else -> false
    }
    fun getType(type : String) : Class<*> {
        return when (type) {
            "bitmaps" -> {
                Int::class.java
            }
            else -> {
                String::class.java
            }
        }
    }
}