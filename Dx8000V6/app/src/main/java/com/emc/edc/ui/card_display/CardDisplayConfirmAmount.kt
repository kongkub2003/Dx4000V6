package com.emc.edc.ui.card_display

import android.os.Build
import android.os.CountDownTimer
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.emc.edc.R
import com.emc.edc.emv.DeviceHelper
import com.emc.edc.ui.card_entry.ShowAlertMessage
import com.emc.edc.ui.card_entry.ShowLoading
import com.emc.edc.ui.card_entry.cardEntryOnlineAndOffline
import com.emc.edc.ui.theme.BgColor
import com.emc.edc.ui.theme.ChipCard
import com.emc.edc.ui.theme.MenuListTextDark
import com.emc.edc.ui.theme.StaticColorText
import com.emc.edc.utils.*
import kotlinx.coroutines.*
import org.json.JSONObject

@RequiresApi(Build.VERSION_CODES.O)
@DelicateCoroutinesApi
@Composable
fun CardDisplay(
    navController: NavController,
    hasError: MutableState<Boolean>,
    errMessage: MutableState<String>,
    jsonData: MutableState<JSONObject>,
    responseOnlineStatus: MutableState<Boolean>,
    dialogStatus: MutableState<Boolean>,
    connectionStatus: MutableState<Boolean>,
    textLoading: MutableState<String>,
    data: String?,
) {

    Log.v("TEST", "Data: $jsonData")

    val countDownTimer = remember { mutableStateOf("60") }

    val timer = object : CountDownTimer(60000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            //if (canDoOnlineStatus.value){ cancel() }
            countDownTimer.value = ((millisUntilFinished / 1000)).toString()
        }

        override fun onFinish() {
            countDownTimer.value = "0"
            navController.popBackStack()
        }
    }
    LaunchedEffect(Unit) {
        timer.start()
    }
    DisposableEffect(Unit) {
        onDispose {
            timer.cancel()
        }
    }
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
    ) {
        ButtonTitleMenuSaleCard(
            jsonData.value.getString("title").uppercase(),
            navController, countDownTimer, timer, responseOnlineStatus
        )
        VirtualCard(
            jsonData.value.getString("card_number"),
            jsonData.value.getString("pan_masking"),
            jsonData.value.getString("name")

        )
        if (jsonData.value.getString("amount") != "") {
            ShowAmount(jsonData.value.getString("amount"))
        }
        ButtonSuccessConfirm(
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

@Composable
private fun ButtonTitleMenuSaleCard(
    transactionType: String,
    navController: NavController,
    countDownTimer: MutableState<String>,
    timer: CountDownTimer,
    responseOnlineStatus: MutableState<Boolean>
) {
    Row(
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth()
    ) {
        IconButton(onClick = {
            timer.cancel()
            DeviceHelper.me().emv.stopEMV()
            DeviceHelper.me().emv.stopSearch()
            navController.popBackStack()
            //responseOnlineStatus.value = true
        }) {
            Icon(Icons.Default.ArrowBack, "Menu", tint = MaterialTheme.colorScheme.onSurface)
        }
        Text(
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            text = transactionType,
            modifier = Modifier.padding(top = 9.dp),
            color = MaterialTheme.colorScheme.onSurface
            //color = if (MaterialTheme.colors.isLight) MainText else MainTextDark
        )
        Spacer(Modifier.weight(1f))
        Text(
            fontWeight = FontWeight.Medium,
            fontSize = 20.sp,
            text = "${countDownTimer.value} S",
            modifier = Modifier.padding(top = 9.dp, end = 20.dp),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun VirtualCard(card_number: String, pan_masking: String, name: String) {
    val cardMasking = remember { mutableStateOf("") }

    if (card_number.length != pan_masking.replace(" ", "").length) {
        return
    }

    cardMasking.value = Utils().cardMasking(card_number, pan_masking)

    Card(
        colors = CardDefaults.cardColors(containerColor = StaticColorText),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .padding(13.dp)
            .height(200.dp)
            .padding(bottom = 10.dp),
        elevation = CardDefaults.cardElevation(8.dp),
    ) {
        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column() {
                    Text(
                        fontWeight = FontWeight.Normal,
                        text = "EXPIRES",
                        modifier = Modifier,
                        fontSize = 8.sp,
                        color = MenuListTextDark,
                        letterSpacing = 2.sp,
                    )
                    Text(
                        fontWeight = FontWeight.Normal,
                        letterSpacing = 2.sp,
                        text = "XX/XX",
                        modifier = Modifier,
                        fontSize = 10.sp,
                        color = MenuListTextDark
                    )
                    Card(
                        colors = CardDefaults.cardColors(containerColor = ChipCard),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .padding(top = 25.dp, bottom = 25.dp)
                            .height(40.dp)
                            .width(50.dp)
                    ) {}

                    Text(
                        fontWeight = FontWeight.Normal,
                        letterSpacing = 4.sp,
                        text = cardMasking.value,
                        modifier = Modifier,
                        fontSize = 15.sp,
                        color = MenuListTextDark
                    )
                    Text(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 4.sp,
                        text = if (name != "") name else "KEY IN",
                        modifier = Modifier,
                        fontSize = 15.sp,
                        color = MenuListTextDark
                    )
                }
                when (Utils().identifyCardScheme(card_number)) {
                    CardScheme.VISA -> Image(painterResource(R.drawable.ic_visa_inc), "coin")
                    CardScheme.MASTERCARD -> Image(
                        painterResource(R.drawable.ic_mastercard_logo),
                        "coin"
                    )
                    else -> Image(painterResource(R.drawable.coin), "coin")
                }

            }

        }
    }
}

@Composable
private fun ShowAmount(amount: String) {
    Column(
        modifier = Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp)
    ) {
        Text(
            fontSize = 15.sp,
            text = "AMOUNT",
            modifier = Modifier.padding(5.dp),
            color = MaterialTheme.colorScheme.onSurface
        )
        Divider()
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.padding(top = 9.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.padding(end = 5.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    text = "฿",
                    color = MenuListTextDark
                    //color = if (MaterialTheme.colors.isLight) MainText else MainTextDark
                )
                Text(
                    overflow = TextOverflow.Clip,
                    text = amount,
                    color = StaticColorText,
                    //fontFamily = VoiceInteractor.Prompt,
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@DelicateCoroutinesApi
@Composable
private fun ButtonSuccessConfirm(
    navController: NavController,
    hasError: MutableState<Boolean>,
    errMessage: MutableState<String>,
    jsonData: MutableState<JSONObject>,
    responseOnlineStatus: MutableState<Boolean>,
    dialogStatus: MutableState<Boolean>,
    connectionStatus: MutableState<Boolean>,
    textLoading: MutableState<String>
) {

    val scope = rememberCoroutineScope()
    val loading = remember { mutableStateOf(false) }
    if (loading.value) {
        ShowLoading(
            openDialog = loading,
            text = textLoading,
        )
    }
    val errMessage = remember { mutableStateOf("") }
    val isCloseButtonShowAlertMessage = remember { mutableStateOf(false) }
    if (hasError.value) {
        ShowAlertMessage(
            description = errMessage, openDialog = hasError,
            navController = navController
        )
    }
    //val errMessage = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth()
    ) {
        Button(
            onClick = {
                scope.launch {
                    cardEntryOnlineAndOffline(
                        navController,
                        hasError,
                        errMessage,
                        jsonData,
                        responseOnlineStatus,
                        loading,
                        connectionStatus,
                        textLoading,
                        )

                }

            },
            colors = ButtonDefaults.textButtonColors(
                containerColor = StaticColorText,
                contentColor = BgColor
            ),
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 6.dp,
                pressedElevation = 8.dp,
                disabledElevation = 0.dp
            ),
        ) {
            Text("Confirm")

        }

    }


}
