package com.emc.edc

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.CallSuper
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import com.emc.edc.database.DataSettingRO
import com.emc.edc.emv.DeviceHelper
import com.emc.edc.ui.home.MenuView
import com.emc.edc.ui.theme.EmcTheme
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.kotlin.where
import kotlinx.coroutines.DelicateCoroutinesApi


@ExperimentalFoundationApi
@ExperimentalAnimationApi
@DelicateCoroutinesApi
@ExperimentalComposeUiApi
@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : ComponentActivity(), DeviceHelper.ServiceReadyListener {
    @SuppressLint("CoroutineCreationDuringComposition")
    @CallSuper
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //.................................................................
        //init realm
        Realm.init(this)
        val mustUpdateConfigTerminalRO = true
        val mustUpdateProcessingCodeRO = true
        val mustUpdateTransactionRO = true
        val mustUpdateConfigCardRO = true
        val mustUpdateConfigCardControlRO = true
        val mustUpdateConfigHostRO = true
        val mustUpdateConfigCapkRO = true
        try {
            val dataSettingApp = Realm.getInstance(dataSettingConfiguration)
            val dataSetting = dataSettingApp.where<DataSettingRO>().findFirst()
            Log.v("TEST", "Get setting value: $dataSetting")
            if (dataSetting == null) {
                dataSettingApp.beginTransaction()
                val data_setting = DataSettingRO()
                dataSettingApp.copyToRealm(data_setting)
                dataSettingApp.commitTransaction()
            }
            dataSettingApp.close()
        } catch (e: Exception) {
            Log.v("TEST", e.toString())
        }

        var dataRealmRealmConfiguration: RealmConfiguration

        try {
            dataRealmRealmConfiguration = updateConfigDataRealmConfiguration
            Realm.getInstance(updateConfigDataRealmConfiguration)
        } catch (e: Exception) {
            Log.v("TEST", "error: $e")
            val EvtsUpdateRO: ArrayList<EvtUpdateRo> = GetListUpdateRO(e.toString())
            Log.v("TEST", EvtsUpdateRO.toString())

            Realm.getInstance(dataSettingConfiguration).use { dataSettingRealm ->
                dataSettingRealm.executeTransaction {
                    val data = dataSettingRealm.where<DataSettingRO>().findFirst()
                    Log.v("TEST", "data setting: $data")
                    data!!.ver_schema_config_data = getSchemaConfigData() + 1
                }
            }

            dataRealmRealmConfiguration = RealmConfiguration.Builder()
                .name("config.realm")
                .schemaVersion(getSchemaConfigData())
                .migration(RealmMigrations(EvtsUpdateRO))
                .modules(DataConfigModule())
                .allowWritesOnUiThread(true)
                .build()!!
            Log.v(
                "TEST",
                "realm version: ${Realm.getInstance(dataRealmRealmConfiguration).version}"
            )
        }
        if (mustUpdateConfigTerminalRO) {
            val realm = Realm.getInstance(dataRealmRealmConfiguration)
            UpdateConfigTerminal(realm)
            realm.close()
        }
        if (mustUpdateProcessingCodeRO) {
            val realm = Realm.getInstance(dataRealmRealmConfiguration)
            UpdateDatabaseProcessingCode(realm)
            realm.close()
        }
        if (mustUpdateTransactionRO) {
            val realm = Realm.getInstance(dataRealmRealmConfiguration)
            UpdateDatabaseTransaction(realm)
            realm.close()
        }
        if (mustUpdateConfigCardRO) {
            val realm = Realm.getInstance(dataRealmRealmConfiguration)
            UpdateDatabaseConfigCard(realm)
            realm.close()
        }
        if (mustUpdateConfigCardControlRO) {
            val realm = Realm.getInstance(dataRealmRealmConfiguration)
            UpdateDatabaseConfigCardControl(realm)
            realm.close()
        }
        if (mustUpdateConfigHostRO) {
            val realm = Realm.getInstance(dataRealmRealmConfiguration)
            UpdateDatabaseConfigHost(realm)
            realm.close()
        }
        if (mustUpdateConfigCapkRO) {
            val realm = Realm.getInstance(dataRealmRealmConfiguration)
            UpdateDatabaseConfigCapk(realm)
            realm.close()
        }

        try {
            Realm.getInstance(transactionDataConfiguration)
        } catch (e: Exception) {
            Log.v("TEST", "error: $e")
            val EvtsUpdateRO: ArrayList<EvtUpdateRo> = GetListUpdateRO(e.toString())
            Log.v("TEST", EvtsUpdateRO.toString())

            Realm.getInstance(dataSettingConfiguration).use { dataSettingRealm ->
                dataSettingRealm.executeTransaction {
                    val data = dataSettingRealm.where<DataSettingRO>().findFirst()
                    Log.v("TEST", "data setting: $data")
                    data!!.ver_schema_transaction_data = getSchemaTransactionData() + 1
                }
            }
            val transactionDataConfigurationUpdated = RealmConfiguration.Builder()
                .name("transaction.realm")
                .schemaVersion(getSchemaTransactionData())
                .migration(RealmMigrations(EvtsUpdateRO))
                .modules(DataTransactionModule())
                .allowWritesOnUiThread(true)
                .build()!!

            val transactionRealm = Realm.getInstance(transactionDataConfigurationUpdated)

            Log.v("TEST", "realm version: ${transactionRealm.version}")
        }

        //#init realm
        DeviceHelper.me().init(this)
        DeviceHelper.me().bindService()
        DeviceHelper.me().setServiceListener(this)

        setContent {

            EmcTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background

                ) {
                    Navigation(this)
                }
            }
        }

    }

    override fun onReady(version: String?) {
        DeviceHelper.me().register(true)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            DeviceHelper.me().unregister()
            DeviceHelper.me().unbindService()
            DeviceHelper.me().setServiceListener(null)
        } catch (e: IllegalStateException) {
            Log.d("test", "unregister fail: " + e.message)
        }
    }
}


@Composable
fun Greeting(name: String) {
    Text("$name")
}



@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("CoroutineCreationDuringComposition", "UnusedMaterial3ScaffoldPaddingParameter")
@DelicateCoroutinesApi
//@ExperimentalMaterialApi
@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalFoundationApi
@Composable
fun MainScreen(
    navController: NavHostController, context: Context
) {
    //DeviceHelper.me().emv
    val toggleTheme: () -> Unit = {}
    val currentTheme = remember {
        mutableStateOf(false)
    }
    MenuView(navController,context,toggleTheme,currentTheme)
}





@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    EmcTheme {
        Greeting("Android")
    }
}