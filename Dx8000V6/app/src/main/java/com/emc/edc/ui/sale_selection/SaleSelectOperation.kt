package com.emc.edc.ui.sale_selection

import android.os.Build
import android.os.CountDownTimer
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import org.json.JSONObject
import com.emc.edc.R
import com.emc.edc.Route
import com.emc.edc.ui.theme.*

//import com.emc.edc.ui.search_transaction.SearchTransactionScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SelectOperation(navController: NavController, data: String) {
    val jsonData = JSONObject(data)
    val amount = jsonData.getString("amount")
    val transactionType = jsonData.getString("transaction_type")
    val title = jsonData.getString("title")
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

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        ButtonTitleSelecOperation(navController, countDownTimer, timer, jsonData)
        Spacer(Modifier.height(10.dp))
        Column(
            modifier = Modifier.padding(start = 20.dp, end = 20.dp)
        ) {
            SelectCardButton(navController, jsonData)
            Spacer(Modifier.height(10.dp))
            SelectQRButton(navController, jsonData)
        }
    }
}

@Composable
private fun ButtonTitleSelecOperation(
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
            Icon(Icons.Default.ArrowBack, "Menu", tint = green100)
        }
        Text(
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            text = jsonData.getString("title").uppercase(),
            modifier = Modifier.padding(top = 9.dp),
            color = green100
        )
        Spacer(Modifier.weight(1f))
        Text(
            fontWeight = FontWeight.Medium,
            fontSize = 20.sp,
            text = "${countDownTimer.value} S",
            modifier = Modifier.padding(top = 9.dp, end = 20.dp),
            color = green100
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SelectCardButton(navController: NavController, jsonData: JSONObject) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.credit_card))
    val progress by animateLottieCompositionAsState(
        composition = composition
    )
    Card(
        colors = CardDefaults.cardColors(containerColor =  CardColors,),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        elevation = CardDefaults.cardElevation(8.dp),
    ) {
        Box(modifier = Modifier
            .clickable {
                navController.navigate("${Route.CardEntry.route}/$jsonData") {
                    popUpTo(Route.Home.route)
                }
            }) {
            Row(
                Modifier.fillMaxSize()
                .background(color = green100),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(start = 20.dp),

                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Sale by",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "CARD",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp
                    )
                    Text(
                        text = "Credit Card / Debit Card",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(end = 20.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.End
                ) {
//                    Image(painterResource(R.drawable.coin), "coin")
                    LottieAnimation(
                        composition,
                        progress,
                        modifier = Modifier.size(100.dp)
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SelectQRButton(navController: NavController, jsonData: JSONObject) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.qr))
    val progress by animateLottieCompositionAsState(
        composition = composition
    )
    Card(
        colors = CardDefaults.cardColors(containerColor =  CardColors,),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        elevation = CardDefaults.cardElevation(8.dp),
    ) {
        Box(modifier = Modifier
            .clickable {
                navController.navigate("${Route.CardEntry.route}/$jsonData") {
                    popUpTo(Route.Home.route)
                }
            }) {
            Row(
                Modifier.fillMaxSize()
                    .background(color = green50),
                horizontalArrangement = Arrangement.SpaceBetween

            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(start = 20.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Sale by",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "QR Code",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp
                    )
                    Text(
                        text = "THAI QR, Credit QR, Alipay, WeChat",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )

                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(end = 20.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.End
                ) {
                    Image(painterResource(R.drawable.qr), "coin")

                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalAnimationApi
@Preview(showBackground = true)
@Composable
private fun MainMenuShow() {
    val navController = rememberAnimatedNavController()
    SelectOperation(navController,
        "{'transaction_type' :null , 'amount' : 0,'operation':null,'title':'test'}")
}