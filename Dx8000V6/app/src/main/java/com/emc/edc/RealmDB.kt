package com.emc.edc

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.emc.edc.database.ConfigHostRO
import com.emc.edc.database.DataSettingRO
import com.emc.edc.database.TransactionHistoryRO
import com.emc.edc.utils.Utils
import io.realm.Realm
import io.realm.RealmList
import io.realm.Sort
import io.realm.internal.SyncObjectServerFacade
import io.realm.kotlin.where
import org.json.JSONObject
import com.emc.edc.database.*
import java.util.*

fun getDarkMode() : Boolean {
    Realm.init(SyncObjectServerFacade.getApplicationContext())
    val dataSettingApp = Realm.getInstance(dataSettingConfiguration)
    val dataSetting = dataSettingApp.where<DataSettingRO>().findFirst()
    Log.v("TEST", "Get setting value: $dataSetting")
    dataSettingApp.close()
    return dataSetting?.enable_darkmode ?: false
}

fun changeDarkMode(hasEnable: Boolean) {
    Realm.init(SyncObjectServerFacade.getApplicationContext())
    val dataSettingApp = Realm.getInstance(dataSettingConfiguration)
    dataSettingApp.executeTransaction {
        val dataSettimg = dataSettingApp.where<DataSettingRO>().findFirst()
        dataSettimg?.enable_darkmode = hasEnable
    }
    dataSettingApp.close()
}

fun checkPasswordApp(password: String): Boolean {
    Realm.init(SyncObjectServerFacade.getApplicationContext())
    val realm = Realm.getInstance(configDataRealmConfiguration)
    val dataTerminal = realm.where<ConfigTerminalRO>().equalTo("app_password",password).findFirst()
    val checkPassword = dataTerminal != null
    realm.close()
    return checkPassword
}

fun getDataSetting(): DataSettingRO? {
    Realm.init(SyncObjectServerFacade.getApplicationContext())
    val realm = Realm.getInstance(dataSettingConfiguration)
    val dataSetting = realm.where<DataSettingRO>().findFirst()
    val dataSettingCopy = realm.copyFromRealm(dataSetting)
    realm.close()
    return dataSettingCopy
}

fun editDataSetting(dataSetting: DataSettingRO): Boolean {
    return try {
        Realm.init(SyncObjectServerFacade.getApplicationContext())
        val realm = Realm.getInstance(dataSettingConfiguration)
        realm.beginTransaction()
        realm.delete(DataSettingRO::class.java)
        realm.copyToRealm(dataSetting)
        realm.commitTransaction()
        realm.close()
        true
    }
    catch (e:Exception){
        Log.d("TEST","Edit data setting failed")
        false
    }
}

fun getMerchantData(): JSONObject {
    Realm.init(SyncObjectServerFacade.getApplicationContext())
    val realm = Realm.getInstance(configDataRealmConfiguration)
    val dataMerchant = realm.where<ConfigTerminalRO>().findFirst()
    val dataMerchantCopy = realm.copyFromRealm(dataMerchant)
    realm.close()
    return JSONObject("{" +
            "merchant_name: \"${dataMerchantCopy?.merchant_name}\", " +
            "merchant_location: \"${dataMerchantCopy?.merchant_location}\"," +
            "merchant_convince: \"${dataMerchantCopy?.merchant_convince}\"" +
            "}")
}


fun getSchemaConfigData() : Long {
    return if (Realm.getInstance(dataSettingConfiguration).where<DataSettingRO>().findFirst() != null){
        Realm.getInstance(dataSettingConfiguration).where<DataSettingRO>().findFirst()!!.ver_schema_config_data
    }
    else{
        0
    }
}

fun getSchemaTransactionData() : Long {
    return if (Realm.getInstance(dataSettingConfiguration).where<DataSettingRO>().findFirst() != null){
        Realm.getInstance(dataSettingConfiguration).where<DataSettingRO>().findFirst()!!.ver_schema_transaction_data
    }
    else{
        0
    }
}

fun saveTransaction(data: JSONObject) : Boolean{
    try {
        Realm.init(SyncObjectServerFacade.getApplicationContext())

        val realm = Realm.getInstance(transactionDataConfiguration)
        val last_record_number =
            try { realm.where<TransactionHistoryRO>().findAll().last()!!.record_number }
            catch (e: Exception) { 1 }
        Log.v("test","record number: $last_record_number")
        val transactionHistory = TransactionHistoryRO(
            txn_type = data.getString("transaction_type"),
            txn_status = data.getString("transaction_status"),
            txn_operator = data.getString("operation"),
            full_card_number = data.getString("card_number"),
            card_number = data.getString("card_number_mask"),
            card_holder_name = data.getString("name"),
            expire_date = data.getString("card_exp"),
            record_number = last_record_number!! +1,
            date = data.getString("date"),
            time = data.getString("time"),
            datetime_from_host = data.getBoolean("datetime_from_host"),
            txn_amount = data.getString("amount").replace(",","").toDouble(),
            terminal_id = data.getString("tid"),
            merchant_id = data.getString("mid"),
            batch_number = data.getString("batch_number"),
            nii = data.getString("nii"),
            stan = data.getString("stan"),
            invoice_number = data.getString("invoice_num"),
            reference_number = data.getString("ref_num"),
            approval_code = data.getString("auth_id"),
            response_code = data.getString("res_code"),
            card_record_index = data.getInt("card_record_index"),
            host_record_index = data.getInt("host_record_index"),
            host_define_type = data.getInt("host_define_type"),

        )
        Log.v("TEST", "save transaction: $transactionHistory")
        realm.beginTransaction()
        realm.copyToRealm(transactionHistory)
        realm.commitTransaction()
        realm.close()

        val realmTerminal = Realm.getInstance(updateConfigDataRealmConfiguration)
        realmTerminal.executeTransaction {
            val config_terminal = realmTerminal.where<ConfigTerminalRO>().findFirst()
            config_terminal!!.trace_invoice++
        }
        val config_terminal = realmTerminal.where<ConfigTerminalRO>().findFirst()
        Log.v("TEST", "update invoice: ${config_terminal.toString()}")
        realmTerminal.close()

        return true
    }
    catch (e: Exception){
        Log.v("TEST", "save transaction filed: $e")
        return false
    }
}

fun updateTransaction(data: JSONObject) : Boolean{
    try {
        Realm.init(SyncObjectServerFacade.getApplicationContext())
        val realm = Realm.getInstance(updateTransactionDataConfiguration)
        realm.executeTransaction {
            val transaction = realm.where<TransactionHistoryRO>().equalTo("record_number",data.getInt("record_number")).findFirst()
            if (transaction != null) {
                transaction.txn_type = data.getString("transaction_type")
                transaction.txn_status = data.getString("transaction_status")
                transaction.date = data.getString("date")
                transaction.time = data.getString("time")
                transaction.datetime_from_host = data.getBoolean("datetime_from_host")
                transaction.txn_amount = data.getString("amount").replace(",", " ").toDouble()
                transaction.original_amount =
                    data.getString("additional_amount").replace(",", "").toDouble()
                transaction.batch_number = data.getString("batch_number")
                transaction.stan = data.getString("stan")
                transaction.reference_number = data.getString("ref_num")
                transaction.approval_code = data.getString("auth_id")
                transaction.response_code = data.getString("res_code")
            }
        }
        val transaction = realm.where<TransactionHistoryRO>().equalTo("record_number",data.getInt("record_number")).findFirst()
        Log.v("TEST", "update transaction: ${transaction.toString()}")
        realm.close()

        return true
    }
    catch (e: Exception){
        Log.v("TEST", "update transaction filed: $e")
        return false
    }
}

fun updateStan(hostRecordIndex: Int, stan: Int? = null) : Boolean{
    try {
        Realm.init(SyncObjectServerFacade.getApplicationContext())
        val realm = Realm.getInstance(updateConfigDataRealmConfiguration)

        realm.executeTransaction {
            val config_host = realm.where<ConfigHostRO>().equalTo("host_record_index",hostRecordIndex).findFirst()
            if (stan!=null) {
                config_host!!.stan = stan
            }else{
                if (config_host!!.stan == 999999) {
                    config_host.stan = 1
                } else {
                    config_host.stan++
                }
            }
        }
        val config_host = realm.where<ConfigHostRO>().equalTo("host_record_index",hostRecordIndex).findFirst()
        Log.v("TEST", "update stan: ${config_host.toString()}")
        realm.close()

        return true
    }
    catch (e: Exception){
        Log.v("TEST", "update stan filed: $e")
        return false
    }
}

fun saveReverseMassage(hostRecordIndex: Int, reverseMassage: String?) : Boolean{
    try {
        Realm.init(SyncObjectServerFacade.getApplicationContext())
        val realm = Realm.getInstance(updateConfigDataRealmConfiguration)

        realm.executeTransaction {
            val config_host = realm.where<ConfigHostRO>().equalTo("host_record_index",hostRecordIndex).findFirst()
            if (config_host != null) config_host.reversal_msg = reverseMassage
        }
        val config_host = realm.where<ConfigHostRO>().equalTo("host_record_index",hostRecordIndex).findFirst()
        Log.v("TEST", "save reverse msg: ${config_host.toString()}")
        realm.close()

        return true
    }
    catch (e: Exception){
        Log.v("TEST", "save reverse massage filed: $e")
        return false
    }
}

fun setReverseFlag(hostRecordIndex: Int, reverseFlag: Boolean) : Boolean{
    try {
        Realm.init(SyncObjectServerFacade.getApplicationContext())
        val realm = Realm.getInstance(updateConfigDataRealmConfiguration)

        realm.executeTransaction {
            val config_host = realm.where<ConfigHostRO>().equalTo("host_record_index",hostRecordIndex).findFirst()
            if (config_host != null) {
                config_host.reversal_flag = reverseFlag
            }
        }
        val config_host = realm.where<ConfigHostRO>().equalTo("host_record_index",hostRecordIndex).findFirst()
        Log.v("TEST", "set reverse flag: ${config_host.toString()}")
        realm.close()

        return true
    }
    catch (e: Exception){
        Log.v("TEST", "set reverse flag filed: $e")
        return false
    }
}

fun selectCardData(cardNumber: String): (ConfigCardRO?) {
    Realm.init(SyncObjectServerFacade.getApplicationContext())
    val realm = Realm.getInstance(configDataRealmConfiguration)
    val cardRange = realm.where<ConfigCardRangeRO>()
        .lessThanOrEqualTo("low_range", cardNumber.toLong())
        .greaterThan("high_range", cardNumber.toLong())
        .equalTo("card_number_length", cardNumber.length).findFirst()
    Log.v("TEST", "range: $cardRange")
    return if (cardRange != null) {
        val cardData = realm.copyFromRealm(realm.where<ConfigCardRO>()
            .equalTo("card_record_index", cardRange?.card_record_index).findFirst())
        realm.close()
        cardData
    }
    else{
        realm.close()
        null
    }
}

fun getCardData(cardRecordIndex: Int): ConfigCardRO? {
    Realm.init(SyncObjectServerFacade.getApplicationContext())
    val realm = Realm.getInstance(configDataRealmConfiguration)
    val cardData = realm.copyFromRealm(realm.where<ConfigCardRO>().equalTo("card_record_index", cardRecordIndex).findFirst())
    realm.close()
    return cardData
}

fun getCardControl(cardControlIndex: Int, transactionType: String): ConfigCardControlRO? {
    Realm.init(SyncObjectServerFacade.getApplicationContext())
    val realm = Realm.getInstance(configDataRealmConfiguration)
    val cardControl = realm.copyFromRealm(realm.where<ConfigCardControlRO>()
        .equalTo("card_control_record_index", cardControlIndex).findFirst())
    realm.close()
    return cardControl
}

fun selectHost(hostIndex: Int): ConfigHostRO? {
    Realm.init(SyncObjectServerFacade.getApplicationContext())
    val realm = Realm.getInstance(configDataRealmConfiguration)
    val host = realm.where<ConfigHostRO>().equalTo("host_record_index", hostIndex).findFirst()
    val hostCopy = if (host != null) realm.copyFromRealm(host) else null
    Log.v("TEST", "select host: ${hostCopy.toString()}")
    realm.close()
    return hostCopy
}

fun getTraceInvoice(): Int {
    Realm.init(SyncObjectServerFacade.getApplicationContext())
    val realm = Realm.getInstance(configDataRealmConfiguration)
    val terminal = realm.copyFromRealm(realm.where<ConfigTerminalRO>().findFirst())
    realm.close()
    Log.v("TEST", "terminal: $terminal")
    return terminal!!.trace_invoice
}

fun getHostList() : RealmList<ConfigHostRO> {
    Realm.init(SyncObjectServerFacade.getApplicationContext())
    val realm = Realm.getInstance(configDataRealmConfiguration)
    val hostList = realm.where<ConfigHostRO>().findAll()
    val hostListCopy = RealmList<ConfigHostRO>()
    hostListCopy.addAll(hostList)
    realm.close()
    return hostListCopy
}

fun checkReversalTransaction(transactionType: String): Boolean {
    return try {
        Realm.init(SyncObjectServerFacade.getApplicationContext())
        val realm = Realm.getInstance(configDataRealmConfiguration)
        val transaction = realm.copyFromRealm(realm.where<ConfigTransactionRO>().equalTo("type", transactionType).findFirst())
        realm.close()
        transaction!!.reversal!!
    }
    catch (e:Exception) {
        false
    }
}

fun getLastTransaction(): TransactionHistoryRO? {
    return try {
        Realm.init(SyncObjectServerFacade.getApplicationContext())
        val realm = Realm.getInstance(transactionDataConfiguration)
        val lastTransaction = realm.copyFromRealm(realm.where<TransactionHistoryRO>().findAll().last())
        realm.close()
        lastTransaction
    }
    catch (e:Exception){
        null
    }
}

fun getTodayStr(): String {
    val date = Date()
    return "${1900 + date.year}" +
            "${if (1 + date.month < 10) "0${1 + date.month}" else 1 + date.month}" +
            "${if (date.date < 10) "0${date.date}" else date.date}"
}

@RequiresApi(Build.VERSION_CODES.O)
fun getTodaySales(): String {
    val today = getTodayStr()
    Log.v("TEST", "today: $today")
    Realm.init(SyncObjectServerFacade.getApplicationContext())
    val realm = Realm.getInstance(transactionDataConfiguration)
    val todaySale = realm.where<TransactionHistoryRO>().equalTo("txn_type","sale")
        .equalTo("date",today).sum("txn_amount")
    val test = Utils().formatMoney(Utils().formatMoneyD2S(todaySale as Double))
    realm.close()
    return Utils().formatMoney(Utils().formatMoneyD2S(todaySale as Double))
}

fun getTodayTransaction(): Array<Any> {
    val date = Date()
    val today = "${1900+date.year}" +
            "${if (1+date.month<10) "0${1+date.month}" else 1+date.month}" +
            "${if (date.date<10) "0${date.date}" else date.date}"
    Log.v("TEST", "today: $today")
    Realm.init(SyncObjectServerFacade.getApplicationContext())
    val realm = Realm.getInstance(transactionDataConfiguration)
    val todayTransaction = realm.where<TransactionHistoryRO>().equalTo("date",today)
        .sort("time", Sort.DESCENDING).findAll()
    val transactionlist = RealmList<TransactionHistoryRO>()
    transactionlist.addAll(todayTransaction)
    val transactionTotal = Utils().formatMoney(Utils().formatMoneyD2S(todayTransaction.sum("txn_amount") as Double))
    realm.close()
    return arrayOf(transactionTotal, transactionlist)
}

fun updateHost(host: ConfigHostRO): Boolean {
    try {
        Realm.init(SyncObjectServerFacade.getApplicationContext())
        val realm = Realm.getInstance(updateConfigDataRealmConfiguration)

        realm.executeTransaction {
            val config_host = realm.where<ConfigHostRO>().equalTo("host_record_index",host.host_record_index).findFirst()
            if (config_host != null) {
                config_host.stan = host.stan
                config_host.last_batch_number = host.last_batch_number
                config_host.reversal_flag = host.reversal_flag
                config_host.reversal_msg = host.reversal_msg
            }
        }
        val config_host = realm.where<ConfigHostRO>().equalTo("host_record_index",host.host_record_index).findFirst()
        Log.v("TEST", "update setting host: ${config_host.toString()}")
        realm.close()

        return true
    }
    catch (e: Exception){
        Log.v("TEST", "set reverse flag filed: $e")
        return false
    }
}


fun updateInvoice(invoice: Int? = null): Boolean{
    try {
        Realm.init(SyncObjectServerFacade.getApplicationContext())
        val realm = Realm.getInstance(updateConfigDataRealmConfiguration)

        realm.executeTransaction {
            val config_terminal = realm.where<ConfigTerminalRO>().findFirst()
            if (invoice!=null) {
                config_terminal!!.trace_invoice = invoice
            }else{
                if (config_terminal!!.trace_invoice == 999999) {
                    config_terminal.trace_invoice = 1
                } else {
                    config_terminal.trace_invoice++
                }
            }
        }
        val config_terminal = realm.where<ConfigTerminalRO>().findFirst()
        Log.v("TEST", "update stan: ${config_terminal.toString()}")
        realm.close()

        return true
    }
    catch (e: Exception){
        Log.v("TEST", "update invoice filed: $e")
        return false
    }
}

fun searchTransaction(invoice: String): TransactionHistoryRO?{
    Realm.init(SyncObjectServerFacade.getApplicationContext())
    val realm = Realm.getInstance(transactionDataConfiguration)
    val transaction = realm.where<TransactionHistoryRO>().equalTo("invoice_number", invoice).findFirst()
    val transactionCopy = if (transaction != null) realm.copyFromRealm(transaction) else null
    realm.close()
    return transactionCopy
}

fun getCAPK(rid: String, capk_index: String): ConfigCapkRO? {
    Realm.init(SyncObjectServerFacade.getApplicationContext())
    val realm = Realm.getInstance(configDataRealmConfiguration)
    val configCapk = realm.where<ConfigCapkRO>().equalTo("rid", rid).equalTo("capk_index", capk_index).findFirst()
    val configCapkCopy = if (configCapk != null) realm.copyFromRealm(configCapk) else null
    realm.close()
    return configCapkCopy
}
