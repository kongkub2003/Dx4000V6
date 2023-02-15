package com.emc.edc.ui.card_entry

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.CountDownTimer
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.emc.edc.*
import com.emc.edc.R
import com.emc.edc.emv.DeviceHelper
import com.emc.edc.emv.Emv
import com.emc.edc.ui.card_display.CardDisplay
import com.emc.edc.ui.theme.*
import com.emc.edc.ui.utils.getHostInformationFromCard
import com.usdk.apiservice.aidl.beeper.UBeeper
import com.usdk.apiservice.aidl.led.ULed
import org.json.JSONObject

import com.usdk.apiservice.aidl.constants.RFDeviceName;
import com.usdk.apiservice.aidl.emv.CandidateAID
import com.usdk.apiservice.aidl.led.Light
import kotlinx.coroutines.*
import java.util.*


@SuppressLint("CoroutineCreationDuringComposition")
@ExperimentalFoundationApi
@ExperimentalAnimationApi
@RequiresApi(Build.VERSION_CODES.O)
@DelicateCoroutinesApi

@Composable
fun CardEntry(
    context: Context, navController: NavController, data: String
) {
    val jsonData = remember { mutableStateOf(JSONObject(data)) }
    val amount = jsonData.value.getString("amount")
    val transactionType = jsonData.value.getString("transaction_type")
    val errMessageOpenDialog = remember { mutableStateOf(false) }

    val title = jsonData.value.getString("title")
    val hasError = remember { mutableStateOf(false) }
    val hasEMVStartAgain = remember { mutableStateOf(false) }
    val hasEMVStartAgainConfirm = remember { mutableStateOf(false) }
    val beeper: UBeeper = DeviceHelper.me().beeper
    val led: ULed = DeviceHelper.me().getLed(RFDeviceName.INNER)
    val requestOnlineStatus = remember { mutableStateOf(false) }
    val responseOnlineStatus = remember { mutableStateOf(false) }
    val endProcessStatus = remember { mutableStateOf(false) }
    val cardCanDoOnlineStatus = remember { mutableStateOf(false) }
    val loading = remember { mutableStateOf(false) }
    val textLoading = remember { mutableStateOf("") }
    var dialogStatus = remember { mutableStateOf(false) }
    var arksToPrintStatus = remember { mutableStateOf(false) }
    val errMessage = remember { mutableStateOf("") }
    val seletAIDPopup = remember { mutableStateOf(false) }
    val aidList: MutableList<String> = remember { mutableStateListOf() }
    val aidOriginalList: MutableList<List<CandidateAID>> = remember { mutableStateListOf() }
    val onlineStatus = remember { mutableStateOf(false) }
    var connectionStatus = remember { mutableStateOf(false) }
    val countDownTimer = remember { mutableStateOf("60") }
    val scope = rememberCoroutineScope()
    var confirmAmountEnableStatus = remember { mutableStateOf(true) }
    val timer = object : CountDownTimer(60000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            if (endProcessStatus.value) {

                cancel()
            }
            countDownTimer.value = ((millisUntilFinished / 1000)).toString()
        }

        override fun onFinish() {
            scope.launch {
                countDownTimer.value = "0"
                DeviceHelper.me().emv.stopEMV()
                DeviceHelper.me().emv.stopSearch()
                navController.popBackStack()
            }
        }
    }

    LaunchedEffect(Unit) {
        timer.start()
        scope.launch {
            startTrade(
                jsonData,
                hasError,
                errMessage,
                navController,
                seletAIDPopup,
                aidList,
                aidOriginalList,
                cardCanDoOnlineStatus,
                requestOnlineStatus,
                responseOnlineStatus,
                endProcessStatus,
                hasEMVStartAgain,
                hasEMVStartAgainConfirm
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            timer.cancel()
            scope.launch {
                DeviceHelper.me().emv.stopEMV();
                DeviceHelper.me().emv.stopSearch()
            }
        }
    }

    if (confirmAmountEnableStatus.value) {
        if (requestOnlineStatus.value) {
            timer.cancel()
            countDownTimer.value = "0"
            if (!onlineStatus.value) {
                onlineStatus.value = true
                jsonData.value = getHostInformationFromCard(
                    transactionType,
                    jsonData.value,
                    hasError,
                    errMessage
                )
            }

            CardDisplay(
                navController,
                hasError,
                errMessage,
                jsonData,
                responseOnlineStatus,
                dialogStatus,
                connectionStatus,
                textLoading,
                data
            )
        }
    } else {
        if (requestOnlineStatus.value) {
            if (!onlineStatus.value) {
                timer.cancel()
                countDownTimer.value = "0"

                scope.launch {
                    onlineStatus.value = true
                    jsonData.value = getHostInformationFromCard(
                        transactionType,
                        jsonData.value,
                        hasError,
                        errMessage
                    )

                    cardEntryOnlineAndOffline(
                        navController,
                        hasError,
                        errMessage,
                        jsonData,
                        responseOnlineStatus,
                        dialogStatus,
                        connectionStatus,
                        textLoading
                    )
                }
            }
        }
    }

    if (responseOnlineStatus.value && !endProcessStatus.value) {
        //if (!onlineStatus.value) {
        if (jsonData.value.getString("operation") == "contact") {
            //if (connectionStatus.value) {
            Emv().emvResponseOnline(jsonData.value)
            //endProcessStatus!!.value = true //it still on doEndProcess of EMV
            //}
        } else {
            endProcessStatus!!.value = true
        }
        //}
    }

    if (endProcessStatus.value && responseOnlineStatus.value) {
        if (jsonData.value.getString("operation") == "contact" ||
            jsonData.value.getString("operation") == "contactless"
        ) {
            if (jsonData!!.value.getString("gen_ac") == "TC[0x01]") {
                textLoading.value = "EMV Approve"
                hasError.value = false
                errMessage.value = ""
                /*printSlipTransaction(
                    jsonData.value, context, errMessage, loading,
                    textLoading, arksToPrintStatus, arksToPrintStatus
                )*/
            } else if (jsonData!!.value.getString("gen_ac") == "AAC[0x00]") {
                hasError.value = true
                if (errMessage.value == "") {
                    errMessage.value = "EMV Decline"
                }
            }
        }
        requestOnlineStatus.value = false
        responseOnlineStatus.value = false
        dialogStatus.value = false
        arksToPrintStatus.value = true
        timer.cancel()
        DeviceHelper.me().emv.stopEMV();
        DeviceHelper.me().emv.stopSearch()
    }

    if (hasError.value) {
        if (connectionStatus.value &&
            jsonData.value.getString("operation") == "contact"
            && !endProcessStatus.value
        ) {
            Emv().emvResponseOnline(jsonData.value)
            //responseOnlineStatus.value = true // actually connection error then we need to do 2 gen AC
        } else {
            dialogStatus.value = false
            requestOnlineStatus.value = false
            //responseOnlineStatus.value = true // actually connection error then we need to do 2 gen AC
            led.turnOn(Light.RED);
            timer.cancel()
            DeviceHelper.me().emv.stopEMV();
            DeviceHelper.me().emv.stopSearch()

            ShowAlertMessage(
                description = errMessage, openDialog = hasError,
                navController = navController
            )

        }

    }

    if (dialogStatus.value) {
        loading.value = true
        ShowLoading(openDialog = loading, text = textLoading)
    }

    if (!hasError.value) {
        led.turnOff(Light.RED)
    }

    if (seletAIDPopup.value) {
        ShowSelectAIDList("AID Select", seletAIDPopup, aidList, navController, aidOriginalList)
    }

    if (confirmAmountEnableStatus.value &&
        !requestOnlineStatus.value ||
        !confirmAmountEnableStatus.value
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            ButtonTitleMenuOperationWait(navController, countDownTimer, timer, title, scope)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .wrapContentSize(Alignment.Center)
                    .padding(10.dp)
                    .fillMaxWidth(),
            ) {
                Text(
                    fontWeight = FontWeight.Light,
                    fontSize = 12.sp,
                    text = "Please Choose One",
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                )
            }
            "TODO()"

            GetAmountDetail(amount)
            ButtonKeyIn(amount, transactionType, navController, title)
            Spacer(modifier = Modifier.height(20.dp))
            WaitOperationInfo()
        }
    }
}

@Composable
private fun ButtonTitleMenuOperationWait(
    navController: NavController,
    countDownTimer: MutableState<String>,
    timer: CountDownTimer,
    title: String,
    scope: CoroutineScope,
) {
    Row(
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth()
    ) {
        IconButton(onClick = {
            scope.launch {
                timer.cancel()
                DeviceHelper.me().emv.stopProcess()
                DeviceHelper.me().emv.stopSearch()
                DeviceHelper.me().emv.stopEMV()
                navController.popBackStack()
            }
        }) {
            Icon(Icons.Default.ArrowBack, "Menu", tint = Color.Black)
        }
        Text(
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            text = title.uppercase(Locale.getDefault()),
            modifier = Modifier.padding(top = 9.dp),
            color = Color.Black
        )
        Spacer(Modifier.weight(1f))
        Text(
            fontWeight = FontWeight.Medium,
            fontSize = 20.sp,
            text = "${countDownTimer.value} S",
            modifier = Modifier.padding(top = 9.dp, end = 20.dp),
            color = Color.Black
        )
    }
}

@Preview
@Composable
private fun WaitOperationInfo() {
    val compositionCardSwipe by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.swipe))
    val progressCardSwipe by animateLottieCompositionAsState(
        composition = compositionCardSwipe,
        iterations = LottieConstants.IterateForever
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = green130
            ),
    ) {
        Row(
            modifier = Modifier.padding(30.dp)
        ) {
            Column(
                modifier = Modifier.width(150.dp)
            ) {

                Text(
                    text = "INSERT",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,

                    )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Image(painterResource(R.drawable.poc), "coin1")
            }

        }
        Row(
            modifier = Modifier
                .width(400.dp)
                .height(100.dp)
                .background(
                    color = green130
                ),
            horizontalArrangement = Arrangement.End

        ) {
            Image(painterResource(R.drawable.pod), "coin1", modifier = Modifier.size(90.dp))
            Row(
                modifier = Modifier
                    .width(300.dp), horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "TAP",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    modifier = Modifier.padding(30.dp)
                )
            }
        }
        Row(
            modifier = Modifier
                .width(400.dp)
                .height(200.dp)
                .padding(30.dp)
                .background(
                    color = green130
                )
        ) {
            Text(
                text = "SWIPE",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
            )
            Row(
                modifier = Modifier
                    .width(400.dp),
                horizontalArrangement = Arrangement.End
            ) {

                Image(painterResource(R.drawable.swipe), "coin1", modifier = Modifier.size(200.dp))
            }
        }
    }

}


@Composable
private fun GetAmountDetail(amount: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when {
            amount.length < 9 -> {
                Text(
                    text = "฿ $amount",
                    color = green100,
                    fontWeight = FontWeight.Bold,
                    fontSize = 45.sp
                )
            }
            else -> {
                Text(
                    text = "฿ $amount",
                    color = green100,
                    fontWeight = FontWeight.Bold,
                    fontSize = 35.sp
                )
            }
        }

    }
}

@Composable
private fun ButtonKeyIn(
    amount: String, transactionType: String,
    navController: NavController,
    title: String
) {
    val jsonData = JSONObject()
    if (amount != "") {
        jsonData.put("amount", amount)
    } else {
        jsonData.put("amount", "")
    }

    jsonData.put("name", " ")
    jsonData.put("transaction_type", transactionType)
    jsonData.put("operation", "key_in")
//    Log.d("TEST",jsonData.toString())
    jsonData.put("title", title)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                DeviceHelper.me().emv.stopSearch()
                navController.navigate("key_in/$jsonData") {
                    popUpTo(Route.Home.route)
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = green150),
            shape = RoundedCornerShape(50),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 6.dp,
                pressedElevation = 8.dp,
                disabledElevation = 0.dp
            )

        ) {
            Text(
                text = "Manual Input Card Number", fontSize = 10.sp,
                color = Color.White, fontWeight = FontWeight.Normal
            )
        }
    }

}



