package com.emc.edc

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import com.emc.edc.database.TransactionHistoryRO
import com.emc.edc.emv.DeviceHelper
import com.usdk.apiservice.aidl.beeper.UBeeper

@Composable
fun testPrinter(navController: NavController, context: Context){
    val transaction = TransactionHistoryRO()
    val errMessage = remember { mutableStateOf("") }
    val loading = remember { mutableStateOf(false) }
    val textLoading = remember { mutableStateOf("") }
    var arksToPrintStatus = remember { mutableStateOf(false) }

    /*printSlipTransaction(
        transaction, context, errMessage, loading,
        textLoading, arksToPrintStatus, arksToPrintStatus
    )*/

}