@file:OptIn(ExperimentalFoundationApi::class)

package com.emc.edc.ui.search_transaction

import android.content.Context
import android.os.Build
import android.os.CountDownTimer
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.emc.edc.emv.DeviceHelper
import com.emc.edc.ui.card_entry.ShowAlertMessage
import com.emc.edc.ui.card_entry.ShowLoading
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SearchTransactionScreen(context: Context?, navController: NavHostController,
                            transactionType: String, transactionTitle: String) {
    val popupPrintSlipCustomer = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val loading = remember { mutableStateOf(false) }
    val textLoading = remember { mutableStateOf("") }
    val errMessage = remember { mutableStateOf("") }
    val errMessageOpenDialog = remember { mutableStateOf(false) }
    val popUpContinue = remember { mutableStateOf(false) }
    val isCloseButtonShowAlertMessage = remember { mutableStateOf(true) }
    val handler = CoroutineExceptionHandler { _, exception ->
        Log.e("Confirm Amount Display", exception.toString())
    }

    val countDownTimer = remember{ mutableStateOf("60") }
    val invoice = remember{ mutableStateOf("0") }
    val callback = { text: String ->
        coroutineScope.launch(handler) {
            searchTransacitionButtonClick(context, text, invoice, navController,
                transactionType, transactionTitle, errMessage, loading, textLoading
            )
        }
    }

    val timer =  object: CountDownTimer(60000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
//            Log.d("test", "count down ${millisUntilFinished.toString()}")
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

    if (loading.value) {
        ShowLoading(
            openDialog = loading,
            text = textLoading,
        )
    }

    if (errMessageOpenDialog.value) {
        ShowAlertMessage(
            description = errMessage,
            openDialog = errMessageOpenDialog,
            isCloseButton = isCloseButtonShowAlertMessage
        )
    }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()) {
        ButtonTitleMenu(navController, countDownTimer, timer, transactionTitle.uppercase())
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
                color =  MaterialTheme.colors.onSurface,
                text = "ENTER TRACE INVOICE",
                textAlign = TextAlign.Center,
            )
            Text(
                text = invoice.value,
                fontWeight = FontWeight.Bold,
                color = Color.Green,
                textAlign = TextAlign.Center,
                fontSize = 60.sp,
            )
        }
        Numpad(callback)
    }
}

@Composable
private fun ButtonTitleMenu(
    navController: NavController,
    countDownTimer: MutableState<String>,
    timer: CountDownTimer,
    transactionTitle: String
) {
    val scope = rememberCoroutineScope()
    Row(
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth()
    ) {
        IconButton(onClick = {
            scope.launch {
                DeviceHelper.me().icCpuReader.stopSearch()
                DeviceHelper.me().magReader.stopSearch()
                timer.cancel()
                navController.popBackStack()
            }
        }) {
            Icon(Icons.Default.ArrowBack, "Menu", tint = MaterialTheme.colors.onSurface)
        }
        Text(
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            text = transactionTitle,
            modifier = Modifier.padding(top = 9.dp),
            color =  MaterialTheme.colors.onSurface
        )
        Spacer(Modifier.weight(1f))
        Text(
            fontWeight = FontWeight.Medium,
            fontSize = 20.sp,
            text = "${countDownTimer.value} S",
            modifier = Modifier.padding(top = 9.dp, end = 20.dp),
            color =  MaterialTheme.colors.onSurface
        )
    }
}

@Composable
private fun Numpad(callback: (text: String) -> Any) {
    Column(
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier
            .fillMaxSize()
    ) {
        NumpadRow(
            listOf("1", "2", "3"),
            listOf(0.33f, 0.34f, 0.33f),
            callback
        )
        NumpadRow(
            listOf("4", "5", "6"),
            listOf(0.33f, 0.34f, 0.33f),
            callback
        )
        NumpadRow(
            listOf("7", "8", "9"),
            listOf(0.33f, 0.34f, 0.33f),
            callback
        )
        NumpadRow(
            listOf("⌫", "0", "✓"),
            listOf(0.33f, 0.34f, 0.33f),
            callback
        )
    }
}

@Composable
private fun NumpadRow(
    texts: List<String>,
    weights: List<Float>,
    callback: (text: String) -> Any
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Gray)
            .padding(top = 5.dp, bottom = 5.dp)
    ) {
        for (i in texts.indices) {
            NumpadButton(
                text = texts[i],
                modifier = Modifier.weight(weights[i]),
                callback = callback
            )
        }
    }
}

@Composable
private fun NumpadButton(
    text: String,
    callback: (text: String) -> Any,
    modifier: Modifier = Modifier
) {
    TextButton(
        modifier = modifier,
        onClick = {
            callback(text)
        }
    ) {
        Text(
            color=Color.White,
            text=text,
            fontSize=30.sp
        )
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalAnimationApi
@Preview(showBackground = true)
@Composable
private fun MainMenuShow() {
    val navController = rememberAnimatedNavController()
    SearchTransactionScreen(context = LocalContext.current, navController,
        "transaction type", "transaction title")
}
