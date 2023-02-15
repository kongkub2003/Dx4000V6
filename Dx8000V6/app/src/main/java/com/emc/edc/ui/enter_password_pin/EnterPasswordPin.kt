package th.emerchant.terminal.edc_pos.screen.transaction.enter_password_pin

import android.content.Context
import android.os.Build
import android.os.CountDownTimer
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.filled.Circle
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
import com.emc.edc.ui.card_entry.AskPrintCustomerSlip
import com.emc.edc.ui.card_entry.ShowAlertMessage
import com.emc.edc.ui.card_entry.ShowLoading
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import org.json.JSONObject


@ExperimentalFoundationApi
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EnterPasswordPinScreen(
    context: Context?, navController: NavHostController,
    jsonData: JSONObject, route: String? = null
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
        Log.e("Confirm Amount Display", exception.toString())
    }

    val countDownTimer = remember{ mutableStateOf("60") }
    val password = remember{ mutableStateOf("") }
    val numberPassword = remember{ mutableStateOf(6) }
    val callback = { text: String ->
        coroutineScope.launch(handler) {
            enterPasswordPinHandleButtonClick(
                context, text, numberPassword, password, navController, jsonData,
                errMessage, loading, textLoading, popupPrintSlipCustomer,
                popUpContinue, route
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

    if (popupPrintSlipCustomer.value) {
        AskPrintCustomerSlip(
            openDialog = popupPrintSlipCustomer,
            navController = navController,
            jsonData = jsonData,
            context = context!!,
        )
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
        ButtonTitleMenuAmount(navController, countDownTimer, timer,
            jsonData.getString("transaction_title").uppercase())
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
//                .wrapContentSize(Alignment.Center)
                .padding(10.dp)
                .fillMaxWidth(),
        ) {
            Text(
                fontWeight = FontWeight.Light,
                fontSize = 12.sp,
                text = "ENTER PASSWORD PIN",
                modifier = Modifier.padding(top = 9.dp, bottom = 9.dp),
                color =  MaterialTheme.colors.onSurface,
                textAlign = TextAlign.Center,
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(numberPassword.value),
                contentPadding = PaddingValues(1.dp),
                modifier = Modifier.padding(top = 10.dp, start = 80.dp, end = 80.dp)
            ) {
                items(numberPassword.value) { item ->
                    if (item >= password.value.length) {
                        Icon(Icons.Outlined.Circle, "Null", tint = MaterialTheme.colors.onSurface, modifier = Modifier.size(22.dp))
                    }
                    else{
                        Icon(Icons.Filled.Circle, "Filled", tint = MaterialTheme.colors.onSurface, modifier = Modifier.size(22.dp))
                    }
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
            listOf("", "0", "⌫"),
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


@ExperimentalFoundationApi
@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalAnimationApi
@Preview(showBackground = true)
@Composable
private fun MainMenuShow() {
    val navController = rememberAnimatedNavController()
    val jsonData = JSONObject("{amount: 0.0, transaction_title: title}")
    EnterPasswordPinScreen(context = LocalContext.current, navController,jsonData)
}
