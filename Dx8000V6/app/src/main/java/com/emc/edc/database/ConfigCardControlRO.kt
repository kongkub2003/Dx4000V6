package com.emc.edc.database

import io.realm.RealmObject

open class ConfigCardControlRO(
    var card_control_record_index: Int? = 0,
    var sale_allow: Boolean? = false,
    var sale_password: String? = null,
    var void_allow: Boolean? = false,
    var void_password: String? = null,
    var refund_allow: Boolean? = false,
    var refund_password: String? = null,
    var preauth_allow: Boolean? = false,
    var preauth_password: String? = null,
    var maunaul_entry_allow: Boolean? = false,
    var maunaul_entry_password: String? = null,
    var sale_complete_allow: Boolean? = false,
    var sale_complete_password: String? = null,
    var offline_allow: Boolean? = false,
    var offline_password: String? = null,
    var settlement_allow: Boolean? = false,
    var settlement_password: String? = null,
    var pan_masking: String? = "NNXX XXXX XXXX XXNN", // N = can show number X=Cannot Show Mask as X
) : RealmObject() {
    fun isList(type : String) : Boolean = when (type) {
        "" -> true
        else -> false
    }
    fun getType(type : String) : Class<*> = when (type) {
        "card_control_record_index",
        -> Int::class.java
        "sale_allow", "void_allow", "refund_allow", "preauth_allow", "maunaul_entry_allow",
        "sale_complete_allow", "offline_allow", "settlement_allow",
        -> Boolean::class.java
        else -> String::class.java
    }
    fun checkAllow(transactionType: String) : Boolean? {
        return when (transactionType) {
            "sale" -> sale_allow
            "offline_sale" -> offline_allow
            "pre_auth" -> preauth_allow
            "refund" -> refund_allow
            "void" -> void_allow
            "sale_complete" -> sale_complete_allow
            "settlement" -> settlement_allow
            "balance_inquiry" -> true
            else -> false
        }
    }
}