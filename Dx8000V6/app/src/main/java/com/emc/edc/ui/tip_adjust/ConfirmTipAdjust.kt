package th.emerchant.terminal.edc_pos.screen.transaction.tip_adjust

import android.content.Context
import android.os.Build
import android.os.CountDownTimer
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.emc.edc.ui.card_entry.AskPrintCustomerSlip
import com.emc.edc.ui.card_entry.ShowAlertMessage
import com.emc.edc.ui.card_entry.ShowInquiryMessage
import com.emc.edc.ui.card_entry.ShowLoading
import com.emc.edc.ui.theme.BgColor
import com.emc.edc.ui.theme.StaticColorText
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch
import org.json.JSONObject


@DelicateCoroutinesApi
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ConfirmTipAmountScreen(
    context: Context?, navController: NavHostController, jsonData: JSONObject
) {
    val countDownTimer = remember{ mutableStateOf("60") }
    val originalAmount = jsonData.getString("original_amount")
    val tipAmount = jsonData.getString("additional_amount")
    val totalAmount = jsonData.getString("amount")

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
            DisplayAmount("Original Amount",originalAmount,MaterialTheme.colors.onSurface,50)
            DisplayAmount("Tip Amount",tipAmount,MaterialTheme.colors.onSurface,50)
            DisplayAmount("Total Amount",totalAmount,Color.Green,60)
        }
        Spacer(Modifier.weight(1f))
        ButtonSuccessConfirm(jsonData, navController, context!!)
    }
}

@Composable
private fun DisplayAmount(title: String, amount: String, color: Color, size: Int){
    val rawAmount = amount.replace(",","").replace(".","")
    Text(
        fontWeight = FontWeight.Light,
        fontSize = 12.sp,
        color =  MaterialTheme.colors.onSurface,
        text = title,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(5.dp)
    )
    when {
        rawAmount.length < 6 -> {
            Text(
                text = amount,
                fontWeight = FontWeight.Bold,
                color = color,
                textAlign = TextAlign.Center,
                fontSize = size.sp,
            )
        }
        rawAmount.length < 8 -> {
            Text(
                text = amount,
                fontWeight = FontWeight.Bold,
                fontSize = (size-10).sp,
                color = color,
                textAlign = TextAlign.Center,
            )
        }
        rawAmount.length < 10 -> {
            Text(
                text = amount,
                fontWeight = FontWeight.Bold,
                fontSize = (size-20).sp,
                color = color,
                textAlign = TextAlign.Center,
            )
        }
        rawAmount.length < 12 -> {
            Text(
                text = amount,
                fontWeight = FontWeight.Bold,
                fontSize = (size-25).sp,
                color = color,
                textAlign = TextAlign.Center,
            )
        }
        else -> {
            Text(
                text = amount,
                fontWeight = FontWeight.Bold,
                fontSize = (size-30).sp,
                color = color,
                textAlign = TextAlign.Center,
            )
        }
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

@OptIn(ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@DelicateCoroutinesApi
@Composable
private fun ButtonSuccessConfirm(
    jsonData: JSONObject,
    navController: NavController,
    context: Context
) {
    val popupPrintSlipCustomer = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val loading = remember { mutableStateOf(false) }
    val textLoading = remember { mutableStateOf("") }
    val errMessage = remember { mutableStateOf("") }
    val errMessageOpenDialog = remember { mutableStateOf(false) }
    val popUpBalanceInquiry = remember { mutableStateOf(false) }
    val balanceInquiryTitle = remember { mutableStateOf("") }
    val balanceInquiryAmount = remember { mutableStateOf("") }
    val popUpContinue = remember { mutableStateOf(false) }
    val isCloseButtonShowAlertMessage = remember { mutableStateOf(false) }
    val handler = CoroutineExceptionHandler { _, exception ->
        Log.e("Tip Adjust", exception.message.toString())
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

    if (errMessageOpenDialog.value) {
        ShowAlertMessage(
            description = errMessage,
            openDialog = errMessageOpenDialog,
            isCloseButton = isCloseButtonShowAlertMessage
        )
    }

    if (popUpBalanceInquiry.value) {
        ShowInquiryMessage(
            title = balanceInquiryTitle,
            amount = balanceInquiryAmount,
            openDialog = popUpBalanceInquiry,
            navController = navController,
        )
    }

    Column(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth()
    ) {
        Button(
            onClick = {
                loading.value = true
                coroutineScope.launch(handler) {
                    confirmTipAmountButtonClick(
                        context, navController, jsonData, errMessage, loading,
                        textLoading, popupPrintSlipCustomer, popUpContinue
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

@DelicateCoroutinesApi
@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalAnimationApi
@Preview(showBackground = true)
@Composable
private fun MainMenuShow() {
    val navController = rememberAnimatedNavController()
    val jsonData = JSONObject(
        "{amount: 110.0, original_amount: 100.00, additional_amount: 10.00, transaction_type: transaction type}",
    )
    ConfirmTipAmountScreen(context = LocalContext.current, navController,jsonData)
}
