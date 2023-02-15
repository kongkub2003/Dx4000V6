package com.emc.edc.database

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmField
import java.util.*

open class TransactionHistoryRO(
    @PrimaryKey
    @RealmField("_id")
    var id: UUID = UUID.randomUUID(),
    var record_number: Int? = null,
    var name: String? = null,
    var date: String? = null,
    var time: String? = null,
    var datetime_from_host: Boolean? = null,
    var txn_amount: Double? = null,
    var dcc_txn_amount: Double? = null,
    var tip_amount: Double? = null,
    var dcc_tip_amount: Double? = null,
    var original_amount: Double? = null,
    var dcc_original_amount: Double? = null,
    var txn_type: String? = null,
    var txn_operator: String? = null,
    var txn_status: String? = null,
    var reference_number: String? = null,
    var approval_code: String? = null,
    var response_code: String? = null,
    var terminal_id: String? = null,
    var merchant_id: String? = null,
    var stan: String? = null,
    var invoice_number: String? = null,
    var batch_number: String? = null,
    var option_bit61: String? = null,
    var option_bit63: String? = null,
    var card_record_index: Int? = null,
    var host_record_index: Int? = null,
    var host_define_type: Int? = null,
    var nii: String? = null,
    var pos_entry_mode: String? = null,
    var pos_condition_code: String? = null,
    var full_card_number: String? = null,
    var card_number: String? = null,
    var expire_date: String? = null,
    var card_holder_name: String? = null,
    var local_currency_code: String? = null,
    var local_currency_text: String? = null,
    var dcc_currency_code: String? = null,
    var dcc_currency_text: String? = null,
    var dcc_exchange_rate: String? = null,
    var reff1_Text: String? = null,
    var reff2_Text: String? = null,
    var processing_code: String? = null,
) : RealmObject() {
    fun isList(type : String) : Boolean = when (type) {
        "" -> true
        else -> false
    }
    fun getType(type : String) : Class<*> {
        return when (type) {
            "record_number" -> {
                Int::class.java
            }
            "datetime_from_host" -> {
                Boolean::class.java
            }
            else -> {
                String::class.java
            }
        }
    }
}