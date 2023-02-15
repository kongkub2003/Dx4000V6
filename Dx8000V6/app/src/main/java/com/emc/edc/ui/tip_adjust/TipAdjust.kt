package th.emerchant.terminal.edc_pos.screen.transaction.tip_adjust

import android.content.Context
import android.os.Build
import android.os.CountDownTimer
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
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
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import org.json.JSONObject

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TipAmountScreen(
    context: Context?, navController: NavHostController, jsonData: JSONObject
) {
    val popupPrintSlipCustomer = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val loading = remember { mutableStateOf(false) }
    val textLoading = remember { mutableStateOf("") }
    val errMessage = remember { mutableStateOf("") }
    val errMessageOpenDialog = remember { mutableStateOf(false) }
    val popUpContinue = remember { mutableStateOf(false) }
    val isCloseButtonShowAlertMessage = remember { mutableStateOf(true) }
    val handler = CoroutineExceptionHandler { _, exception ->
        Log.e("Tip Amount Display", exception.toString())
    }

    val countDownTimer = remember{ mutableStateOf("60") }
    val tipAmount = remember{ mutableStateOf("0.00") }
    val rawTipAmount = remember{ mutableStateOf("0") }
    val callback = { text: String ->
        coroutineScope.launch(handler) {
            tipAmountHandleButtonClick(
                context, text, rawTipAmount, tipAmount, navController, jsonData,
                errMessage, loading, textLoading,
                popupPrintSlipCustomer, popUpContinue
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
            navController.navigateUp()
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
        ButtonTitleMenuAmount(navController, countDownTimer, timer, jsonData.getString("transaction_type").uppercase().replace("_"," "))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .wrapContentSize(Alignment.Center)
                .padding(10.dp)
                .fillMaxWidth(),
        ) {
            Text(
                modifier = Modifier.padding(top = 10.dp),
                fontWeight = FontWeight.Light,
                fontSize = 12.sp,
                color =  MaterialTheme.colors.onSurface,
                text = "TIP AMOUNT",
                textAlign = TextAlign.Center,
            )
            when {
                rawTipAmount.value.length < 6 -> {
                    Text(
                        text = tipAmount.value,
                        fontWeight = FontWeight.Bold,
                        color = Color.Green,
                        textAlign = TextAlign.Center,
                        fontSize = 60.sp,
                    )
                }
                rawTipAmount.value.length < 8 -> {
                    Text(
                        text = tipAmount.value,
                        fontWeight = FontWeight.Bold,
                        fontSize = 50.sp,
                        color = Color.Green,
                        textAlign = TextAlign.Center,
                    )
                }
                rawTipAmount.value.length < 10 -> {
                    Text(
                        text = tipAmount.value,
                        fontWeight = FontWeight.Bold,
                        fontSize = 40.sp,
                        color = Color.Green,
                        textAlign = TextAlign.Center,
                    )
                }
                rawTipAmount.value.length < 12 -> {
                    Text(
                        text = tipAmount.value,
                        fontWeight = FontWeight.Bold,
                        fontSize = 35.sp,
                        color = Color.Green,
                        textAlign = TextAlign.Center,
                    )
                }
                else -> {
                    Text(
                        text = tipAmount.value,
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp,
                        color = Color.Green,
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
    Row(
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth()
    ) {
        IconButton(onClick = {
            timer.cancel()
            navController.navigateUp()
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
    val jsonData = JSONObject("{amount: 0.0, transaction_type: transaction type}")
    TipAmountScreen(context = LocalContext.current, navController,jsonData)
}
