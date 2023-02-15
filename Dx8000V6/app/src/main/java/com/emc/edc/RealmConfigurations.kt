package com.emc.edc

import com.emc.edc.database.*
import io.realm.Realm
import io.realm.RealmConfiguration

import io.realm.annotations.RealmModule
import io.realm.kotlin.where
import com.emc.edc.database.*

@RealmModule(classes = [DataSettingRO::class])
class DataSettingModule

var dataSettingConfiguration: RealmConfiguration = RealmConfiguration.Builder()
    .name("setting.realm")
    .modules(DataSettingModule())
    .deleteRealmIfMigrationNeeded()
    .allowWritesOnUiThread(true)
    .build()

//var dataSettingConfigurationUpdated: RealmConfiguration = RealmConfiguration.Builder()
//    .name("setting.realm")
//    .modules(DataSettingModule())
//    .allowWritesOnUiThread(true)
//    .build()

//const val schemaConfigData = 0L
//const val schemaTransactionData = 0L

@RealmModule(classes = [ConfigCardRO::class, ConfigCardRangeRO::class, ConfigCardControlRO::class,
    ConfigHostRO::class,ConfigHostDefineTypeRO::class,ConfigTerminalRO::class,ConfigTransactionRO::class,
    DataBitmapRO::class, ProcessingCodeRO::class,ProcessingCodeDataRO::class,ConfigCapkRO::class])
class DataConfigModule

var configDataRealmConfiguration = RealmConfiguration.Builder()
    .name("config.realm")
    .schemaVersion(getSchemaConfigData())
    .modules(DataConfigModule())
    .build()!!

var updateConfigDataRealmConfiguration = RealmConfiguration.Builder()
    .name("config.realm")
    .schemaVersion(getSchemaConfigData())
    .modules(DataConfigModule())
    .allowWritesOnUiThread(true)
    .build()!!

@RealmModule(classes = [TransactionHistoryRO::class])
class DataTransactionModule

var transactionDataConfiguration = RealmConfiguration.Builder()
    .name("transaction.realm")
    .schemaVersion(getSchemaTransactionData())
    .modules(DataTransactionModule())
    .build()!!

var updateTransactionDataConfiguration = RealmConfiguration.Builder()
    .name("transaction.realm")
    .schemaVersion(getSchemaTransactionData())
    .modules(DataTransactionModule())
    .allowWritesOnUiThread(true)
    .build()!!

