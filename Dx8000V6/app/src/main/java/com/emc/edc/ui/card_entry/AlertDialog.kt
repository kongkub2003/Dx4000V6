package com.emc.edc.ui.card_entry

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.emc.edc.emv.DeviceHelper
import com.emc.edc.ui.theme.BgColor
import com.emc.edc.ui.theme.StaticColorText
import com.usdk.apiservice.aidl.beeper.UBeeper
import com.usdk.apiservice.aidl.emv.CandidateAID
import com.usdk.apiservice.aidl.emv.UEMV
import kotlinx.coroutines.*
import org.json.JSONObject
import com.emc.edc.R
import com.emc.edc.emv.Emv
import com.emc.edc.getHostList
import com.emc.edc.getLastTransaction
import com.emc.edc.transactionToJson
import com.emc.edc.ui.theme.ComingSoonColor
import com.emc.edc.ui.theme.MainText
import com.emc.edc.utils.Printer

import kotlin.random.Random

@Composable
fun ShowAlertMessage(
    description: MutableState<String>,
    openDialog: MutableState<Boolean>,
    navController: NavController
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.failed_lottie))
    val progress by animateLottieCompositionAsState(composition)

    Dialog(
        onDismissRequest = {
            openDialog.value = true
        },
    ) {
        Card(
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    text = description.value,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(15.dp))
                LottieAnimation(
                    composition,
                    progress,
                    modifier = Modifier.size(100.dp)
                )
                Spacer(Modifier.height(15.dp))
                Button(
                    shape = RoundedCornerShape(25.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = StaticColorText,
                        contentColor = BgColor
                    ),
                    onClick = {
                        openDialog.value = false
                        navController.popBackStack()
                    }) {
                    Text("Back")
                }
            }

        }
    }
}

@Composable
fun ShowAlertMessage(
    description: MutableState<String>,
    openDialog: MutableState<Boolean>,
    isCloseButton: MutableState<Boolean>
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.failed_lottie))
    val progress by animateLottieCompositionAsState(composition)

    Dialog(
        onDismissRequest = {
            openDialog.value = true
        },
    ) {
        Card(
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    text = description.value,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(15.dp))
                LottieAnimation(
                    composition,
                    progress,
                    modifier = Modifier.size(100.dp)
                )
                Spacer(Modifier.height(15.dp))
                if (isCloseButton.value) {
                    Button(
                        shape = RoundedCornerShape(25.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = StaticColorText,
                            contentColor = BgColor
                        ),
                        onClick = {
                            openDialog.value = false
                        }) {
                        Text("Close")
                    }
                }
            }

        }
    }
}
@Composable
fun ShowInquiryMessage(
    title: MutableState<String>,
    amount: MutableState<String>,
    openDialog: MutableState<Boolean>,
    navController: NavController
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.success))
    val progress by animateLottieCompositionAsState(composition)

    Dialog(
        onDismissRequest = {
            openDialog.value = true
        },
    ) {
        androidx.compose.material.Card(
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                androidx.compose.material.Text(
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    text = title.value,
                    color = androidx.compose.material.MaterialTheme.colors.onSurface
                )
                Spacer(Modifier.height(15.dp))
                androidx.compose.material.Text(
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    text = "Balance is",
                    color = androidx.compose.material.MaterialTheme.colors.onSurface
                )
                Spacer(Modifier.height(5.dp))
                androidx.compose.material.Card(
                    shape = RoundedCornerShape(10.dp),
                    elevation = 0.dp,
                    backgroundColor = ComingSoonColor
                ) {
                    androidx.compose.material.Text(
                        modifier = Modifier
                            .padding(10.dp),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 30.sp,
                        text = amount.value,
                        color = androidx.compose.material.MaterialTheme.colors.onSurface
                    )
                }
                Spacer(Modifier.height(15.dp))
                LottieAnimation(
                    composition,
                    progress,
                    modifier = Modifier.size(100.dp)
                )
                androidx.compose.material.Button(
                    shape = RoundedCornerShape(25.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = androidx.compose.material.ButtonDefaults.textButtonColors(
                        backgroundColor = StaticColorText,
                        contentColor = BgColor
                    ),
                    onClick = {
                        openDialog.value = false
                        navController.popBackStack()
                    }) {
                    androidx.compose.material.Text("Back")
                }
            }

        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalFoundationApi
@Composable
fun ShowSelectAIDList(
    title: String,
    openDialog: MutableState<Boolean>,
    aidList: MutableList<String>,
    navController: NavController,
    aidOriginalList: MutableList<List<CandidateAID>>
) {
    val currentCoroutine = rememberCoroutineScope()
    val emv: UEMV? = DeviceHelper.me().emv;

    val handler = CoroutineExceptionHandler { _, exception ->
        Log.e("TCP Test", exception.message.toString())
    }

    Dialog(
        onDismissRequest = {
            openDialog.value = true
        },
    ) {
        Card(
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(
                Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    text = title, color = MaterialTheme.colorScheme.onSurface
                )
                Divider()
//                Spacer(Modifier.height(15.dp))
                LazyColumn(
                    modifier = Modifier.height(300.dp)
                ) {
                    Log.v("TEST", "host list: $aidList")
                    if (aidList.size > 0) {
                        items(aidList.size) { item ->
                            Card(
                                colors = CardDefaults.cardColors(Color(
                                    red = Random.nextInt(0, 255),
                                    green = Random.nextInt(0, 255),
                                    blue = Random.nextInt(0, 255)
                                )),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .padding(
                                        top = 5.dp,
                                        bottom = 5.dp
                                    )
                                    .height(50.dp)
                                    .fillMaxWidth(),
                            ) {
                                Box(
                                    modifier = Modifier.clickable {
//                                        val ip = hostList[item]!!.ip_address1!!
//                                        val port = hostList[item]!!.port1!!
                                        currentCoroutine.launch(handler) {
                                            Emv().selectedAID(item, aidOriginalList)
                                            openDialog.value = false
                                        }
                                    },
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Text(
                                        modifier = Modifier.padding(start = 20.dp),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        text = "Index: ${aidList[item]}"
                                    )
                                }
                            }

                        }
                    }
                }
                Spacer(modifier = Modifier.height(5.dp))
                Button(
                    shape = RoundedCornerShape(25.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = StaticColorText,
                        contentColor = BgColor
                    ),
                    onClick = {
                        emv!!.stopEMV()
                        emv.stopSearch()
                        openDialog.value = false
                        navController.popBackStack()
                    }) {
                    Text("Cancel")
                }
            }
        }
    }
}
@Composable
fun ShowAlertMessageEMVStartAgain(
    description: MutableState<String>,
    openDialog: MutableState<Boolean>,
    navController: NavController,
    startAgain: Job
) {
    val currentCoroutine = rememberCoroutineScope()
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.failed_lottie))
    val progress by animateLottieCompositionAsState(composition)

    val handler = CoroutineExceptionHandler { _, exception ->
        Log.e("TCP Test", exception.message.toString())
    }

    Dialog(
        onDismissRequest = {
            openDialog.value = true
        },
    ) {
        Card(
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    text = description.value,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(15.dp))
                LottieAnimation(
                    composition,
                    progress,
                    modifier = Modifier.size(100.dp)
                )
                Spacer(Modifier.height(15.dp))
                Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                    Button(
                        shape = RoundedCornerShape(25.dp),
//                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = StaticColorText,
                            contentColor = BgColor
                        ),
                        onClick = {
                            openDialog.value = false
                            navController.popBackStack()
                        }) {
                        Text("Back")
                    }
                    Button(
                        shape = RoundedCornerShape(25.dp),
//                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = StaticColorText,
                            contentColor = BgColor
                        ),
                        onClick = {
                            currentCoroutine.launch(handler) {
                                openDialog.value = false
                                startAgain.start()
                            }
                        }) {
                        Text("Confirm")
                    }
                }
            }

        }
    }
}
@SuppressLint("CoroutineCreationDuringComposition")
@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalFoundationApi
@Composable
fun ShowLoading(
    openDialog: MutableState<Boolean>,
    text: MutableState<String>
) {
    val beeper: UBeeper = DeviceHelper.me().beeper
    val currentCoroutine = rememberCoroutineScope()
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )
    Dialog(
        onDismissRequest = {
            openDialog.value = true
        },
    ) {
        Card(
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    text = text.value,
                    color = MaterialTheme.colorScheme.onSurface

                )
                Spacer(Modifier.height(15.dp))
                LottieAnimation(
                    composition,
                    progress,
                    modifier = Modifier.size(100.dp)
                )
            }

        }

    }
}
@Composable
fun AskPrintCustomerSlip(
    openDialog: MutableState<Boolean>,
    navController: NavController,
    jsonData: JSONObject?,
    context: Context
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.printing))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )
    Dialog(
        onDismissRequest = {
            // Dismiss the dialog when the user clicks outside the dialog or on the back
            // button. If you want to disable that functionality, simply use an empty
            // onCloseRequest.
            openDialog.value = true
        },
    ) {
        Card(
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    text = "Do you want to print customer copy?",
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(15.dp))

                LottieAnimation(
                    composition,
                    progress,
                    modifier = Modifier
                        .size(100.dp)
                        .fillMaxWidth()
                )

                Spacer(Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        shape = RoundedCornerShape(25.dp),
                        modifier = Modifier.width(100.dp),
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = StaticColorText,
                            contentColor = BgColor
                        ),
                        onClick = {
                            openDialog.value = false
                            if (jsonData != null) {
                                Printer().printSaleSlip(
                                    jsonData,
                                    context,
                                    "customer"
                                )
                            }
                            else {
                                val lastTransaction = getLastTransaction()
                                if (lastTransaction != null) {
                                    val lastTransactionJson = transactionToJson(lastTransaction.txn_type!!,lastTransaction)
                                    Printer().printSaleSlip(
                                        lastTransactionJson,
                                        context,
                                        "customer"
                                    )
                                    Log.v("TEST", "Last transaction json: $lastTransactionJson")
                                }
                            }
                            navController.popBackStack()
                        }) {
                        Text("YES")
                    }
                    Button(
                        shape = RoundedCornerShape(25.dp),
                        modifier = Modifier.width(100.dp),
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = MainText,
                            contentColor = BgColor
                        ),
                        onClick = {
                            navController.popBackStack()
                        }) {
                        Text("NO")
                    }
                }
            }
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalFoundationApi
@Composable
fun ShowTestHostList(
    title: String,
    openDialog: MutableState<Boolean>,
) {
    val openLoading = remember { mutableStateOf(false) }
    val openLoadingMessage = remember { mutableStateOf("") }

    val openErrDialog = remember { mutableStateOf(false) }
    val withCloseAlertDialogButton = remember { mutableStateOf(false) }
    val openErrDialogMessage = remember { mutableStateOf("") }

    val openSuccessDialog = remember { mutableStateOf(false) }
    val openSuccessDialogMessage = remember { mutableStateOf("") }
    val currentCoroutine = rememberCoroutineScope()

    val handler = CoroutineExceptionHandler { _, exception ->
        Log.e("TCP Test", exception.message.toString())
    }

    if (openLoading.value) {
        ShowLoading(
            openLoading,
            openLoadingMessage
        )
    }

    if (openErrDialog.value) {
        ShowAlertMessage(
            openErrDialogMessage,
            openErrDialog,
            withCloseAlertDialogButton
        )
    }

    if (openSuccessDialog.value) {
        ShowSuccessMessage(
            openSuccessDialogMessage,
            openSuccessDialog
        )
    }

    Dialog(
        onDismissRequest = {
            openDialog.value = true
        },
    ) {
        androidx.compose.material.Card(
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(
                Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(5.dp))
                androidx.compose.material.Text(
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    text = title, color = androidx.compose.material.MaterialTheme.colors.onSurface
                )
                androidx.compose.material.Divider()
//                Spacer(Modifier.height(15.dp))
                LazyColumn(
                    modifier = Modifier.height(300.dp)
                ) {
                    val hostList = getHostList()
                    Log.v("TEST", "host list: $hostList")
                    if (hostList != null) {
                        items(hostList.size) { item ->
                            androidx.compose.material.Card(
                                backgroundColor = Color(
                                    red = Random.nextInt(0, 255),
                                    green = Random.nextInt(0, 255),
                                    blue = Random.nextInt(0, 255)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .padding(
                                        top = 5.dp,
                                        bottom = 5.dp
                                    )
                                    .height(50.dp)
                                    .fillMaxWidth(),
                            ) {
                                Box(
                                    modifier = Modifier.clickable {
                                        val ip = hostList[item]!!.ip_address1!!
                                        val port = hostList[item]!!.port1!!
                                        currentCoroutine.launch(handler) {
                                            openLoading.value = true
                                            openLoadingMessage.value = "Conecting.."
                                           /* val echoIsSuccess = testHost(
                                                ip,
                                                port,
                                                openLoadingMessage,
                                                openErrDialogMessage
                                            )

                                            if (echoIsSuccess) {
                                                openSuccessDialogMessage.value = "Echo is success"
                                                openSuccessDialog.value = true
                                            } else {
                                                openErrDialog.value = true
                                            }
                                            delay(2_500)
                                            openSuccessDialog.value = false
                                            openErrDialog.value = false
                                            openLoading.value = false
                                            openDialog.value = false*/
                                        }
                                    },
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    androidx.compose.material.Text(
                                        modifier = Modifier.padding(start = 20.dp),
                                        color = androidx.compose.material.MaterialTheme.colors.onSurface,
                                        text = "Index: ${hostList[item]!!.host_record_index}"
                                    )
                                }
                            }

                        }
                    }
                }
                Spacer(modifier = Modifier.height(5.dp))
                androidx.compose.material.Button(
                    shape = RoundedCornerShape(25.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = androidx.compose.material.ButtonDefaults.textButtonColors(
                        backgroundColor = StaticColorText,
                        contentColor = BgColor
                    ),
                    onClick = {
                        openDialog.value = false
                    }) {
                    androidx.compose.material.Text("Cancel")
                }
            }
        }
    }
}
@Composable
fun ShowSuccessMessage(
    description: MutableState<String>,
    openDialog: MutableState<Boolean>
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.success))
    val progress by animateLottieCompositionAsState(composition)

    Dialog(
        onDismissRequest = {
            openDialog.value = true
        },
    ) {
        androidx.compose.material.Card(
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                androidx.compose.material.Text(
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    text = description.value,
                    color = androidx.compose.material.MaterialTheme.colors.onSurface
                )
                Spacer(Modifier.height(15.dp))
                LottieAnimation(
                    composition,
                    progress,
                    modifier = Modifier.size(100.dp)
                )
            }

        }
    }

    @Composable
    fun AskPrintCustomerSlip(
        openDialog: MutableState<Boolean>,
        navController: NavController,
        jsonData: JSONObject?,
        context: Context
    ) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.printing))
        val progress by animateLottieCompositionAsState(
            composition = composition,
            iterations = LottieConstants.IterateForever
        )
        Dialog(
            onDismissRequest = {
                // Dismiss the dialog when the user clicks outside the dialog or on the back
                // button. If you want to disable that functionality, simply use an empty
                // onCloseRequest.
                openDialog.value = true
            },
        ) {
            androidx.compose.material.Card(
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    androidx.compose.material.Text(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        text = "Do you want to print customer copy?",
                        color = androidx.compose.material.MaterialTheme.colors.onSurface
                    )
                    Spacer(Modifier.height(15.dp))

                    LottieAnimation(
                        composition,
                        progress,
                        modifier = Modifier
                            .size(100.dp)
                            .fillMaxWidth()
                    )

                    Spacer(Modifier.height(20.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        androidx.compose.material.Button(
                            shape = RoundedCornerShape(25.dp),
                            modifier = Modifier.width(100.dp),
                            colors = androidx.compose.material.ButtonDefaults.textButtonColors(
                                backgroundColor = StaticColorText,
                                contentColor = BgColor
                            ),
                            onClick = {
                                openDialog.value = false
                                if (jsonData != null) {
                                    Printer().printSaleSlip(
                                        jsonData,
                                        context,
                                        "customer"
                                    )
                                } else {
                                    val lastTransaction = getLastTransaction()
                                    if (lastTransaction != null) {
                                        val lastTransactionJson = transactionToJson(
                                            lastTransaction.txn_type!!,
                                            lastTransaction
                                        )
                                        Printer().printSaleSlip(
                                            lastTransactionJson,
                                            context,
                                            "customer"
                                        )
                                        Log.v("TEST", "Last transaction json: $lastTransactionJson")
                                    }
                                }
                                navController.popBackStack()
                            }) {
                            androidx.compose.material.Text("YES")
                        }
                        androidx.compose.material.Button(
                            shape = RoundedCornerShape(25.dp),
                            modifier = Modifier.width(100.dp),
                            colors = androidx.compose.material.ButtonDefaults.textButtonColors(
                                backgroundColor = MainText,
                                contentColor = BgColor
                            ),
                            onClick = {
                                navController.popBackStack()
                            }) {
                            androidx.compose.material.Text("NO")
                        }
                    }
                }
            }
        }
    }

}
