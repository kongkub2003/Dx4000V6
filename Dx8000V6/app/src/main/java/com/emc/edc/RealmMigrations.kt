package com.emc.edc

import android.util.Log
import io.realm.*
import com.emc.edc.database.*
import io.realm.kotlin.where

class RealmMigrations(private val EvtsUpdateRO : ArrayList<EvtUpdateRo>) : RealmMigration {
    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        Log.v("TEST", "event update: $EvtsUpdateRO")
        var version: Long = oldVersion

        val schema = realm.schema
        Log.v("TEST", "old: $oldVersion new: $newVersion")

        for (i in 0 until EvtsUpdateRO.size){
            Log.v("TEST", "Event $i --> " +
                    "\rclass: ${EvtsUpdateRO[i].class_name} " +
                    "\rfield: ${EvtsUpdateRO[i].field_name} " +
                    "\raction: ${EvtsUpdateRO[i].evt_action}")
            when (EvtsUpdateRO[i].evt_action.split(" ")[0]) {
                "removed" -> {
                    try {
                        schema.get(EvtsUpdateRO[i].class_name)!!.removeField(EvtsUpdateRO[i].field_name)
                        Log.v("TEST", "remove event success")
                    }
                    catch (e: Exception){
                        Log.v("TEST", "remove event fail - $e")
                    }
                }
                "added" -> {
                    try {
                        addFieldRO(EvtsUpdateRO[i].class_name, EvtsUpdateRO[i].field_name, schema)
//                        schema.get(EvtsUpdateRO[i].class_name)!!.addField(EvtsUpdateRO[i].field_name, String::class.java)
//                        schema.get(EvtsUpdateRO[i].class_name)!!.transform { obj: DynamicRealmObject ->
//                            val name = ""
//                            obj.setString(EvtsUpdateRO[i].field_name, name)
//                        }
                        Log.v("TEST", "add event success")
                    }
                    catch (e: Exception){
                        Log.v("TEST", "add event fail - $e")
                    }
                }
                else -> {
                    Log.v("TEST", "now not support this event")
                }
            }
        }
//        val dataSettingRealm = Realm.getInstance(dataSettingConfigurationUpdated)
//        dataSettingRealm.executeTransaction {
//            val data = dataSettingRealm.where<DataSettingRO>().findFirst()
//            data?.ver_schema_config_data = newVersion
//        }
//        dataSettingRealm.close()
//        while (version<newVersion) {
//            version++
//        }
    }

    private fun addFieldRO(model: String, field: String, schema: RealmSchema) {
        var isList = false
        var typeField : Class<*> = String::class.java
        when (model) {
            "ConfigTerminalRO" -> {
                val ro = ConfigTerminalRO()
                isList = ro.isList(field)
                typeField = ro.getType(field)
            }
            "ConfigHostRO" -> {
                val ro = ConfigHostRO()
                isList = ro.isList(field)
                typeField = ro.getType(field)
            }
            "ConfigCardRO" -> {
                val ro = ConfigCardRO()
                isList = ro.isList(field)
                typeField = ro.getType(field)
            }
            "ConfigCardControlRO" -> {
                val ro = ConfigCardControlRO()
                isList = ro.isList(field)
                typeField = ro.getType(field)
            }
            "ConfigTransactionRO" -> {
                val ro = ConfigTransactionRO()
                isList = ro.isList(field)
                typeField = ro.getType(field)
            }
            "DataBitmapRO" -> {
                val ro = DataBitmapRO()
                isList = ro.isList(field)
                typeField = ro.getType(field)
            }
            "ProcessingCodeRO" -> {
                val ro = ProcessingCodeRO()
                isList = ro.isList(field)
                typeField = ro.getType(field)
            }
            "ProcessingCodeDataRO" -> {
                val ro = ProcessingCodeDataRO()
                isList = ro.isList(field)
                typeField = ro.getType(field)
            }
            "TransactionHistoryRO" -> {
                val ro = TransactionHistoryRO()
                isList = ro.isList(field)
                typeField = ro.getType(field)
            }
        }
        if (isList) {
            schema.get(model)!!.addRealmListField(field, typeField)
        }
        else {
            schema.get(model)!!.addField(field, typeField)
        }
    }
}



