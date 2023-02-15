package com.emc.edc.ui.transaction_display


import android.content.Context
import android.os.Build
import android.os.CountDownTimer
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.emc.edc.R
import com.emc.edc.ui.card_entry.AskPrintCustomerSlip
import com.emc.edc.ui.card_entry.ShowAlertMessage
import com.emc.edc.ui.card_entry.ShowLoading
import com.emc.edc.ui.theme.BgColor
import com.emc.edc.ui.theme.MainText
import com.emc.edc.ui.theme.MainTextDark
import com.emc.edc.ui.theme.StaticColorText
import com.emc.edc.utils.Utils
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import kotlinx.coroutines.*
import org.json.JSONObject

import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@DelicateCoroutinesApi
@Composable
fun TransactionDisplay(navController: NavHostController, data: String, context: Context) {
    val jsonData = JSONObject(data)
    Log.v("TEST", "Data: $jsonData")

    val countDownTimer = remember { mutableStateOf("60") }

    val timer = object : CountDownTimer(60000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
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
            jsonData.getString("title").uppercase(),
            navController, countDownTimer, timer
        )
        DetailTransaction(jsonData)
        Spacer(Modifier.weight(1f))
        ButtonSuccessConfirm(jsonData, jsonData.getString("title"), navController, context)
    }
}

@Composable
private fun ButtonTitleMenuSaleCard(
    transactionType: String,
    navController: NavController,
    countDownTimer: MutableState<String>,
    timer: CountDownTimer
) {
    Row(
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth()
    ) {
        IconButton(onClick = {
            timer.cancel()
            navController.popBackStack()
        }) {
            Icon(Icons.Default.ArrowBack, "Menu", tint = MaterialTheme.colors.onSurface)
        }
        Text(
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            text = transactionType,
            modifier = Modifier.padding(top = 9.dp),
            color = if (MaterialTheme.colors.isLight) MainText else MainTextDark
        )
        Spacer(Modifier.weight(1f))
        Text(
            fontWeight = FontWeight.Medium,
            fontSize = 20.sp,
            text = "${countDownTimer.value} S",
            modifier = Modifier.padding(top = 9.dp, end = 20.dp),
            color = MaterialTheme.colors.onSurface
        )
    }
}

@Composable
private fun DetailTransaction(jsonData: JSONObject) {
    val date = remember { mutableStateOf("") }
    val time = remember { mutableStateOf("") }

    date.value = Utils().convertToDate(jsonData.getString("date"))
    time.value = Utils().convertToTime(jsonData.getString("time"))

    Card(
        backgroundColor = StaticColorText,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.padding(13.dp),
        elevation = 8.dp,
    ) {
        Column(
            modifier = Modifier.padding(15.dp).padding(top = 10.dp, bottom = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column() {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Spacer(Modifier.weight(1f))
                        Icon(Icons.Filled.EventNote, "date-time", tint = MaterialTheme.colors.onSurface)
                        Text(
                            fontWeight = FontWeight.Normal,
                            text = "${date.value} - ${time.value}",
                            modifier = Modifier.padding(top = 2.dp),
                            fontSize = 12.sp,
                            color = if (MaterialTheme.colors.isLight) MainText else MainTextDark
                        )
                    }
                    CardDetail(jsonData.getString("card_scheme_type"),jsonData.getString("card_number_mask"))
                    SubDetailTransaction("Transaction: ",jsonData.getString("transaction_type").replace("_"," ").uppercase())
                    SubDetailTransaction("Amount: ",jsonData.getString("amount"))
                    SubDetailTransaction("Invoice: ",jsonData.getString("invoice"))
                    SubDetailTransaction("STAN: ",jsonData.getString("stan"))
                }
            }
        }
    }
}

@Composable
private fun SubDetailTransaction(title: String, content: String){
    Row(
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth()
    ) {
        Text(
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            text = title,
            modifier = Modifier.padding(top = 9.dp),
            color = if (MaterialTheme.colors.isLight) MainText else MainTextDark
        )
        Spacer(Modifier.weight(1f))
        Text(
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            text = content,
            modifier = Modifier.padding(top = 9.dp),
            color = MaterialTheme.colors.onSurface
        )
    }
}

@Composable
private fun CardDetail(cardScheme: String, cardNumber: String){
    Row(
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            text = "Card: ",
//            modifier = Modifier.padding(top = 9.dp),
            color = if (MaterialTheme.colors.isLight) MainText else MainTextDark
        )
        Spacer(Modifier.weight(1f))
        val checkCardBrand =
            when (cardScheme.lowercase(Locale.getDefault())) {
                "visa card" -> R.drawable.ic_visa_inc_small
                "mastercard" -> R.drawable.ic_mastercard_logo_small
                else -> R.drawable.ic_coin_small
            }
        Image(
            painterResource(checkCardBrand),
            cardScheme,
            contentScale = ContentScale.Inside,
            modifier = Modifier.size(30.dp)
        )
        Text(
            fontWeight = FontWeight.Medium,
            fontSize = 18.sp,
            text = cardNumber,
            modifier = Modifier.padding(start = 2.dp),
            color = MaterialTheme.colors.onSurface
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@DelicateCoroutinesApi
@Composable
private fun ButtonSuccessConfirm(
    jsonData: JSONObject,
    transactionType: String,
    navController: NavHostController,
    context: Context
) {
    val popupPrintSlipCustomer = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val loading = remember { mutableStateOf(false) }
    val textLoading = remember { mutableStateOf("") }
    val hasError = remember { mutableStateOf(false) }
    val errMessage = remember { mutableStateOf("") }
    val errMessageOpenDialog = remember { mutableStateOf(false) }
    val popUpContinue = remember { mutableStateOf(false) }
    val isCloseButtonShowAlertMessage = remember { mutableStateOf(false) }
    var connectionStatus  = remember { mutableStateOf(false) }
    var responseOnlineStatus  = remember { mutableStateOf(false) }
    val handler = CoroutineExceptionHandler { _, exception ->
        Log.e("Transaction Display", exception.message.toString())
    }
    if (popupPrintSlipCustomer.value) {
        AskPrintCustomerSlip(
            openDialog = popupPrintSlipCustomer,
            navController = navController,
            jsonData = jsonData,
            context = context,
        )
    }

    if (loading.value) {
        ShowLoading(
            openDialog = loading,
            text = textLoading,
        )
    }

    if (hasError.value) {
        ShowAlertMessage(
            description = errMessage,
            openDialog = hasError,
            navController = navController
        )
    }

    Column(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth()
    ) {
        Button(
            onClick = {
                coroutineScope.launch(handler) {
                    transactionDisplayConfirm(
                        jsonData, transactionType, navController, connectionStatus,
                        hasError, errMessage,responseOnlineStatus, context,
                        popupPrintSlipCustomer, loading, textLoading, popUpContinue
                    )
                }
            },
            colors = ButtonDefaults.textButtonColors(
                backgroundColor = StaticColorText,
                contentColor = BgColor
            ),
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            elevation = ButtonDefaults.elevation(
                defaultElevation = 6.dp,
                pressedElevation = 8.dp,
                disabledElevation = 0.dp
            )
        ) {
            Text("Confirm")
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@DelicateCoroutinesApi
@ExperimentalAnimationApi
@Preview(showBackground = true)
@Composable
fun CardDisplayPreview() {
    val navController = rememberAnimatedNavController()
    TransactionDisplay(
        navController,
        "{amount:\"0.00\",transaction_type:\"sale\"," +
                "card_number_mask:\"1122 3344 XXXX 7788\", title:\"title\"," +
                "name:\"NAME\", date:\"20211202\", time:\"000000\"," +
                "card_scheme_type:\"\", invoice: \"1\", stan:\"1\"}",
        LocalContext.current
    )
}
