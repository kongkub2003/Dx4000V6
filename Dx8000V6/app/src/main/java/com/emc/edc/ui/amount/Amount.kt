package com.emc.edc.ui.amount

import android.content.Context
import android.os.CountDownTimer
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
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
import com.emc.edc.ui.theme.*
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import kotlinx.coroutines.launch


@Composable
fun AmountScreen(context: Context?, navController: NavHostController,
                     transactionType: String, transactionTitle: String) {
    val countDownTimer = remember{ mutableStateOf("60") }
    val amount = remember{ mutableStateOf("0.00") }
    val rawAmount = remember{ mutableStateOf("") }
    val callback = { text: String ->
        amountHandleButtonClick(context, text, rawAmount, amount, navController,
            transactionType, transactionTitle)
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

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()) {
        ButtonTitleMenuAmount(navController, countDownTimer, timer, transactionTitle.uppercase())
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
                color =  green100,
                text = "AMOUNT",
                textAlign = TextAlign.Center,
            )
            when {
                rawAmount.value.length < 6 -> {
                    Text(
                        text = amount.value,
                        fontWeight = FontWeight.Bold,
                        color = green100,
                        textAlign = TextAlign.Center,
                        fontSize = 60.sp,
                    )
                }
                rawAmount.value.length < 8 -> {
                    Text(
                        text = amount.value,
                        fontWeight = FontWeight.Bold,
                        fontSize = 50.sp,
                        color = green100,
                        textAlign = TextAlign.Center,
                    )
                }
                rawAmount.value.length < 10 -> {
                    Text(
                        text = amount.value,
                        fontWeight = FontWeight.Bold,
                        fontSize = 40.sp,
                        color = green100,
                        textAlign = TextAlign.Center,
                    )
                }
                rawAmount.value.length < 12 -> {
                    Text(
                        text = amount.value,
                        fontWeight = FontWeight.Bold,
                        fontSize = 35.sp,
                        color = green100,
                        textAlign = TextAlign.Center,
                    )
                }
                else -> {
                    Text(
                        text = amount.value,
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp,
                        color = green100,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
        Numpad(callback)
    }
}

@Composable
private fun ButtonTitleMenuAmount(
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
                //DeviceHelper.me().icCpuReader.stopSearch()
                //DeviceHelper.me().magReader.stopSearch()
                timer.cancel()
                navController.popBackStack()
            }
        }) {
            Icon(Icons.Default.ArrowBack, "Menu", tint = green100)
        }
        Text(
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            text = transactionTitle,
            modifier = Modifier.padding(top = 9.dp),
            color =  green100
        )
        Spacer(Modifier.weight(1f))
        Text(
            fontWeight = FontWeight.Medium,
            fontSize = 20.sp,
            text = "${countDownTimer.value} S",
            modifier = Modifier.padding(top = 9.dp, end = 20.dp),
            color =  green100
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
            .background(green130)
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
            color= green150,
            text=text,
            fontSize=30.sp
        )
    }
}


@ExperimentalAnimationApi
@Preview(showBackground = true)
@Composable
private fun MainMenuShow() {
    val navController = rememberAnimatedNavController()
    AmountScreen(context = LocalContext.current, navController,
        "transaction type", "transaction title")
}
