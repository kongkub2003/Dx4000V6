package th.emerchant.terminal.edc_pos.screen.transaction.key_in

import android.content.Context
import android.os.Build
import android.os.CountDownTimer
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.emc.edc.emv.DeviceHelper
import com.emc.edc.ui.card_entry.ShowAlertMessage
import com.emc.edc.ui.card_entry.ShowLoading
import com.emc.edc.ui.theme.*
import com.emc.edc.utils.Utils
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.usdk.apiservice.aidl.beeper.UBeeper
import com.usdk.apiservice.aidl.constants.RFDeviceName
import com.usdk.apiservice.aidl.led.Light
import com.usdk.apiservice.aidl.led.ULed
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.delay
import org.json.JSONObject
import java.util.*

@OptIn(ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.N)
@ExperimentalComposeUiApi
@Composable
fun KeyIn(context: Context?, navController: NavController, data: String) {
    val jsonData = JSONObject(data)
    //    Log.v("TEST","json: jsonData")
    Log.v("TEST", "Data: $jsonData")


    val errMessage = remember { mutableStateOf("") }
    val responseOnlineStatus = remember { mutableStateOf(false) }

    var dialogStatus = remember { mutableStateOf(false) }
    val loading = remember { mutableStateOf(false) }
    val textLoading = remember { mutableStateOf("") }

    val cardNumber = remember { mutableStateOf("") }
    val month = remember { mutableStateOf("") }
    val year = remember { mutableStateOf("") }
    val cardError = remember { mutableStateOf(false) }
    val monthError = remember { mutableStateOf(false) }
    val yearError = remember { mutableStateOf(false) }
    val countDownTimer = remember { mutableStateOf("60") }
    val timer = object : CountDownTimer(60000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
//            Log.d("test", millisUntilFinished.toString())
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
    if (dialogStatus.value) {
        loading.value = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ShowLoading(openDialog = loading, text = textLoading)
        }
    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        ButtonTitleMenuKeyIn(navController, countDownTimer, timer, jsonData)
        VirtualCard(cardNumber, month, year, cardError, monthError, yearError)
        if (jsonData.getString("amount") != "") {
            ShowAmount(jsonData.getString("amount"))
        }

        Spacer(Modifier.weight(1f))
//        ButtonSuccessConfirm(navController, jsonData, card_number, month, year)
            ButtonSuccessConfirm(
                context,
                navController,
                jsonData,
                cardNumber,
                month,
                year,
                cardError,
                monthError,
                yearError,
                dialogStatus,
                textLoading,
                errMessage,
                responseOnlineStatus
            )
        }
    }




@Composable
private fun ButtonTitleMenuKeyIn(
    navController: NavController,
    countDownTimer: MutableState<String>,
    timer: CountDownTimer,
    jsonData: JSONObject,
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
            text = jsonData.getString("title").uppercase(),
            modifier = Modifier.padding(top = 9.dp),
            color = MaterialTheme.colors.onSurface
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

@RequiresApi(Build.VERSION_CODES.N)
@ExperimentalComposeUiApi
@Composable
private fun VirtualCard(
    card_number: MutableState<String>,
    month: MutableState<String>,
    year: MutableState<String>,
    cardError: MutableState<Boolean>,
    monthError: MutableState<Boolean>,
    yearError: MutableState<Boolean>
) {
    val (card_number_ref, month_ref, year_ref) = remember { FocusRequester.createRefs() }
    val inputService = LocalSoftwareKeyboardController.current
    val localFocusManager = LocalFocusManager.current
    val utils = Utils()

    LaunchedEffect("") {
        delay(300)
        inputService?.show()
        card_number_ref.requestFocus()
    }

    Card(
        backgroundColor = StaticColorText,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .padding(13.dp)
//            .height(200.dp)
            .padding(bottom = 10.dp),
        elevation = 8.dp,
    ) {
        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            Text(
                fontWeight = FontWeight.SemiBold,
                text = "Card Number",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp),
                fontSize = 15.sp,
                color = MenuListTextDark,
                letterSpacing = 2.sp,
            )
            Spacer(modifier = Modifier.height(5.dp))
            OutlinedTextField(
                modifier = Modifier
                    .padding(0.dp)
                    .focusRequester(card_number_ref)
                    .onKeyEvent {
                        if (it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                            month_ref.requestFocus()
                        }
                        false
                    },
                value = card_number.value,
                onValueChange = {
                    if (it.length <= 19) {
                        card_number.value = it
                        if (it.length < 13) {
                            cardError.value = true
                        } else {
                            when (utils.cardIsValid(it)) {
                                false -> cardError.value = true
                                true -> cardError.value = false
                            }
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                placeholder = { Text("xxxx xxxx xxxx xxxx xxx") },
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.White
                ),
                keyboardActions = KeyboardActions(
                    onDone = { month_ref.requestFocus() }
                ),
                isError = cardError.value
            )

            Spacer(modifier = Modifier.height(15.dp))
            Text(
                fontWeight = FontWeight.SemiBold,
                text = "EXPIRES",
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(card_number_ref)
                    .padding(top = 5.dp),
                fontSize = 15.sp,
                color = MenuListTextDark,
                letterSpacing = 2.sp,
            )
            Row {
                OutlinedTextField(
                    modifier = Modifier
                        .padding(0.dp)
                        .focusRequester(month_ref)
                        .width(100.dp)
                        .onKeyEvent {
                            if (it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                                year_ref.requestFocus()
                            }
                            false
                        },
                    value = month.value,
                    onValueChange = { data ->
                        if (data.length <= 2) {
                            month.value = data
                            if (data.length < 2) {
                                monthError.value = true
                            } else {
                                val monthCheck = listOf(
                                    "01",
                                    "02",
                                    "03",
                                    "04",
                                    "05",
                                    "06",
                                    "07",
                                    "08",
                                    "09",
                                    "10",
                                    "11",
                                    "12"
                                )
                                monthError.value = monthCheck.none { it == data }
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = { Text("Month") },
                    placeholder = { Text("xx") },
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.White
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { year_ref.requestFocus() }
                    ),
                    isError = monthError.value
                )

                OutlinedTextField(
                    modifier = Modifier
                        .padding(start = 30.dp)
                        .focusRequester(year_ref)
                        .width(100.dp),
                    value = year.value,
                    onValueChange = { data ->
                        if (data.length <= 2) {
                            year.value = data
                            if (data.length < 2) {
                                yearError.value = true
                            } else {
                                val nowYear = Calendar.getInstance().get(Calendar.YEAR) % 100
                                Log.d("test", nowYear.toString())
                                Log.d("test", (data.toInt() >= nowYear).toString())
                                yearError.value = when (data.toInt() >= nowYear) {
                                    true -> false
                                    false -> true
                                }
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = { Text("Year") },
                    placeholder = { Text("xx") },
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.White
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            inputService?.hide()
                            localFocusManager.clearFocus()
                        }
                    ),
                    isError = yearError.value
                )
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
            color = MaterialTheme.colors.onSurface,
            modifier = Modifier.padding(5.dp)
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
                    color = if (MaterialTheme.colors.isLight) MainText else MainTextDark
                )
                Text(
                    overflow = TextOverflow.Clip,
                    text = amount,
                    color = StaticColorText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                )
            }
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@DelicateCoroutinesApi
@Composable
 fun ButtonSuccessConfirm(
    context: Context?, navController: NavController,
    jsonData: JSONObject,
    card_number: MutableState<String>,
    month: MutableState<String>,
    year: MutableState<String>,
    cardError: MutableState<Boolean>,
    monthError: MutableState<Boolean>,
    yearError: MutableState<Boolean>,
    dialogStatus: MutableState<Boolean>,
    textLoading: MutableState<String>,
    errMessage: Any?,
    responseOnlineStatus: MutableState<Boolean>,
) {
    val checkErrorToContinue = remember { mutableStateOf(false) }
    val messageErrorToContinue = remember { mutableStateOf("") }
    val hasError = remember { mutableStateOf(false)}
    val description = remember { mutableStateOf("")}
    val beeper: UBeeper = DeviceHelper.me().beeper
    val led: ULed = DeviceHelper.me().getLed(RFDeviceName.INNER)
    var confirmAmountEnableStatus = remember { mutableStateOf(true) }
    val errMessage = remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val responseOnlineStatus = remember { mutableStateOf(false) }
    var dialogStatus = remember { mutableStateOf(false) }
    var connectionStatus = remember { mutableStateOf(false) }
    val textLoading = remember { mutableStateOf("") }

    if (hasError.value) {
        beeper.startBeep(500)
        led.turnOn(Light.RED);
//        description.value = "Invalid Card"
        ShowAlertMessage(
            description = description,
            openDialog = hasError,
            navController = navController
        )
        if (!hasError.value) {
            led.turnOff(Light.RED);

        }
    }

    if (!hasError.value) {
        led.turnOff(Light.GREEN);
    }
    Column(modifier = Modifier
        .padding(20.dp)
        .fillMaxWidth()) {
        Button(
            onClick = {
                confirmKeyIn(
                context,
                navController,
                jsonData,
                card_number,
                month,
                year,
                cardError,
                monthError,
                yearError,
                checkErrorToContinue,
                messageErrorToContinue,
                hasError,
                description,
            )
              /*  if (confirmAmountEnableStatus.value) {
                    navController.navigate(Route.CardDisplay.route + "/$jsonData") {
                        popUpTo(Route.Home.route)
                    }
                } else {

                    scope.launch {
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

                            }*/


                if (checkErrorToContinue.value) {
                    Toast.makeText(context, messageErrorToContinue.value, Toast.LENGTH_SHORT).show()
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
            Text("Continue")
        }
    }
}

@ExperimentalAnimationApi
@RequiresApi(Build.VERSION_CODES.N)
@ExperimentalComposeUiApi
@Preview(showBackground = true)
@Composable
private fun DefaultPreviewKeyIn() {
    val navController = rememberAnimatedNavController()
    KeyIn(LocalContext.current, navController,"{amount:\"0.00\", " +
            "transaction_type:\"sale\", title:\"title\"}")
}