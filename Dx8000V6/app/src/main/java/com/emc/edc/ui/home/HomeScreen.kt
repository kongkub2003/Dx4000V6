package com.emc.edc.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideIn
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.emc.edc.R
import com.emc.edc.getTodaySales
import com.emc.edc.ui.theme.green20
import com.emc.edc.ui.utils.TheButton
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import kotlinx.coroutines.DelicateCoroutinesApi
import org.json.JSONObject


@DelicateCoroutinesApi
@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalFoundationApi
@Composable
fun HomeScreen(navController: NavController, context: Context) {
    val total_sale = getTodaySales()
    Column(
        modifier = Modifier
            .fillMaxSize(),

        ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            SaleCardButton(navController as NavHostController, total_sale)
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(5.dp)
                ) {

                    }
                }
            }
        }
    }



@SuppressLint("CoroutineCreationDuringComposition")
@DelicateCoroutinesApi
//@ExperimentalMaterialApi
@ExperimentalFoundationApi
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SaleCardButton(navController: NavHostController, total_sale: String){
    Card(
        colors = CardDefaults.cardColors(containerColor = green20),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        elevation = CardDefaults.cardElevation(8.dp),
    ) {
        Box(modifier = Modifier
            .clickable {
                navController.navigate(
                    "amount/" +
                            "${
                                JSONObject(
                                    "{" +
                                            "transaction_type:\"sale\"," +
                                            "title:\"Sale\"" +
                                            "}"
                                )
                            }"
                )
            }) {
            Row(
                Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(start = 20.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "TODAY",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "SALE",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp
                        )
                    Text(
                        text = "TOTAL: $total_sale BAHT",
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
                    //Image(painterResource(R.drawable.sale1), "coin")

                }
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalAnimationApi
@Preview(showBackground = true)
@Composable
fun CardSalePreview() {
    val navController = rememberAnimatedNavController()
    SaleCardButton(navController, "0.00")
}

