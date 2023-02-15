package com.emc.edc.ui.utils

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.emc.edc.emv.DeviceHelper
import com.emc.edc.globaldata.dataclass.Route
import com.emc.edc.ui.card_entry.ShowTestHostList
import com.emc.edc.utils.Utils
import kotlinx.coroutines.DelicateCoroutinesApi
import org.json.JSONObject

@Composable
@ExperimentalFoundationApi
@RequiresApi(Build.VERSION_CODES.O)
@DelicateCoroutinesApi
fun TheButton(dataRoute: Route, navController: NavController) {
    val context = LocalContext.current
    val openDialog = remember { mutableStateOf(false) }
    val titleDialog = remember { mutableStateOf("") }

    if (openDialog.value) {
        if (titleDialog.value == "Echo Test") {
            ShowTestHostList(title = titleDialog.value, openDialog = openDialog)
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier
                .width(90.dp)
                .height(90.dp),
            backgroundColor = Color.White
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clickable {
                        when (dataRoute.group) {
                            "card_entry" -> {
                                getRouteCardEntry(
                                    navController,
                                    dataRoute
                                )
                            }
                            "menu_entry" -> {
                                getRouteMenuEntry(
                                    navController,
                                    dataRoute,
                                    openDialog,
                                    titleDialog
                                )
                            }
                            "user_entry" -> {
                                getRouteUserMenu(
                                    context,
                                    navController,
                                    dataRoute,
                                    openDialog,
                                    titleDialog
                                )
                            }
                        }
                    }
            ) {
                Image(
                    painterResource( dataRoute.image),
                    dataRoute.title,
                    contentScale = ContentScale.Inside
                )
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = dataRoute.title,
            fontSize = 10.sp,
            color = MaterialTheme.colors.onSurface
        )
        Spacer(modifier = Modifier.height(5.dp))
    }
}

@DelicateCoroutinesApi
private fun getRouteCardEntry(
    navController: NavController,
    dataRoute: Route
) {
    DeviceHelper.me().emv.stopSearch()
    if (dataRoute.path == com.emc.edc.Route.WaitOperation.route) {
        if (dataRoute.txnType == "balance_inquiry") {
            val data =
                "{" +
                        "title: \"${dataRoute.title}\"," +
                        "amount:\"\"," +
                        "transaction_type: \"${dataRoute.txnType}\"," +
                        "operation: \"magnetic\"" +
                        "}"
            val jsonData = JSONObject(data)
            navController.navigate("${dataRoute.path}/$jsonData") {
                popUpTo(com.emc.edc.Route.Home.route)
            }
        }
    } else if (dataRoute.path == "amount") {
        val jsonData = JSONObject()
        jsonData.put("title", dataRoute.title)
        jsonData.put("transaction_type", dataRoute.txnType)
        navController.navigate("${dataRoute.path}/$jsonData") {
            popUpTo(com.emc.edc.Route.Home.route)
        }
    }
}

@DelicateCoroutinesApi
private fun getRouteMenuEntry(
    navController: NavController,
    dataRoute: Route,
    openDialog: MutableState<Boolean>,
    titleDialog: MutableState<String>
) {
    if (dataRoute.popup) {
        openDialog.value = dataRoute.popup
        titleDialog.value = dataRoute.title
//        Log.d("Test", "Click Success")
    }
    else {
        DeviceHelper.me().emv.stopSearch()
        val jsonData = JSONObject()
        jsonData.put("route", com.emc.edc.Route.SearchTransaction.route)
        jsonData.put("transaction_title", dataRoute.title)
        jsonData.put("transaction_type", dataRoute.txnType)
        navController.navigate("${dataRoute.path}/$jsonData") {
            popUpTo(com.emc.edc.Route.Home.route)
        }
    }
}

@DelicateCoroutinesApi
private fun getRouteUserMenu(
    context: Context,
    navController: NavController,
    dataRoute: Route,
    openDialog: MutableState<Boolean>,
    titleDialog: MutableState<String>
) {

    if (dataRoute.popup) {
        openDialog.value = dataRoute.popup
        titleDialog.value = dataRoute.title
//        Log.d("Test", "Click Success")
    } else {
        if (dataRoute.title == "Print Latest"){
            Utils().reprintLastTransaction(context)
        }
        else if (dataRoute.path != ""){
            DeviceHelper.me().emv.stopSearch()
            val jsonData = JSONObject()
            jsonData.put("route", com.emc.edc.Route.SearchTransaction.route)
            jsonData.put("transaction_title",dataRoute.txnType)
            jsonData.put("transaction_type", dataRoute.txnType)
            navController.navigate("${dataRoute.path}/$jsonData") {
                popUpTo(com.emc.edc.Route.Home.route)
            }
        }
    }
}