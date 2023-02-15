package com.emc.edc.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DrawerValue
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.DelicateCoroutinesApi
import org.json.JSONObject
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.emc.edc.R
import com.emc.edc.Route
import com.emc.edc.getTodaySales
import com.emc.edc.globaldata.dataclass.DrawerMenu
import com.emc.edc.ui.menu_entry.MenuEntry
import com.emc.edc.ui.theme.MenuListTextDark
import com.emc.edc.ui.theme.green20
import com.google.accompanist.navigation.animation.composable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import th.emerchant.terminal.edc_pos.screen.transaction.enter_password_pin.EnterPasswordPinScreen
import java.util.*


@SuppressLint("CoroutineCreationDuringComposition")
@DelicateCoroutinesApi
@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalFoundationApi
@Composable
fun MenuView(navController: NavHostController, context: Context,    toggleTheme: () -> Unit,
             currentTheme: MutableState<Boolean>,) {
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState(
        androidx.compose.material.rememberDrawerState(
            DrawerValue.Closed
        )
    )
    val greetingMSG = remember { mutableStateOf(greeting()) }
    val tabIndex = remember { mutableStateOf(0) }


    val total_sale = getTodaySales()
    Column(
        modifier = Modifier
            .fillMaxSize(),

        ) {
        Image(
            painterResource(R.drawable.logo), "coin",
            modifier = Modifier
                .fillMaxWidth()
                .padding(50.dp)
        )
        //SaleCardButton(navController, total_sale)
    }
    androidx.compose.material.Scaffold(
        Modifier.fillMaxWidth(),
        backgroundColor = MaterialTheme.colors.background,
        scaffoldState = scaffoldState,
        topBar = {
            TopBar(
                scope = scope,
                scaffoldState = scaffoldState,
                tabIndex = tabIndex,
                greetingMSG.value
            )
        },
        drawerContent = {
            Drawer(
                scope = scope,
                scaffoldState = scaffoldState,
                navController = navController,
                toggleTheme,
                currentTheme
            )
        },

    ) {
        when (tabIndex.value) {
            0 -> {
                HomeScreen(navController, context)
            }
            1 -> {
                //  CardEntry(navController = navController)
            }
            2 -> {
                MenuEntry(navController = navController)
            }
            3 -> {
                // UserMenu(navController = navController)
            }
            else -> {
                //TransactionView(context,navController)
            }
        }

    }

}



@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun TopBar(scope: CoroutineScope, scaffoldState: ScaffoldState, tabIndex: MutableState<Int>, greeting: String) {
    Column(
        Modifier

            .fillMaxWidth()
            .background(MenuListTextDark)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = greeting,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    color = Color.White
                )
            },

            navigationIcon = {
                IconButton(onClick = {
                    scope.launch {
                        scaffoldState.drawerState.open()
                    }
                }) {
                    Icon(Icons.Filled.Menu, "", tint = Color.Black)
                }
            },
//            actions = {
//                IconButton(onClick = {
//                    scope.launch {
//                        scaffoldState.drawerState.open()
//                    }
//                }) {
//                    Icon(Icons.Filled.Search, "", tint = MaterialTheme.colors.onSurface)
//                }
//            },
            modifier = Modifier
                .padding(start = 20.dp, end = 16.dp, top = 10.dp, bottom = 10.dp)
                .shadow(elevation = 3.dp,)
        )
        Tab(tabIndex)
    }

}

private fun greeting(): String {
    val date = Date().hours
    var message:String ?=null
    when (date) {
        in 0..11 -> {
            message =  "Good Morning"
        }
        in 12..15 -> {
            message =  "Good Afternoon"
        }
        in 16..20 -> {
            message =  "Good Evening"
        }
        in 21..23 -> {
            message =  "Good Night"
        }
    }
    return  message!!
}

@Composable
fun Tab(tabIndex: MutableState<Int>) {
    val tabData = listOf(
        Icons.Filled.Home,
        Icons.Filled.CreditCard,
        Icons.Filled.Menu,
        //Icons.Filled.SupervisedUserCircle,
       // Icons.Filled.History,
    )
    androidx.compose.material.TabRow(
        selectedTabIndex = tabIndex.value,
        backgroundColor = Color.Black,
        contentColor = Color.White
    ) {
        tabData.forEachIndexed { index, pair ->
            androidx.compose.material.Tab(selected = tabIndex.value == index, onClick = {
                tabIndex.value = index
            }, icon = {
                androidx.compose.material.Icon(imageVector = pair, contentDescription = null)
            })
        }
    }
}
@Composable
fun Drawer(
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    navController: NavHostController,
    toggleTheme: () -> Unit,
    currentTheme: MutableState<Boolean>
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        com.emc.edc.globaldata.data.DrawerMenu.list.forEach { items ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .clickable(onClick = {
                        if (!items.popup && items.path != "") {
                          //  DeviceHelper.me().emv.stopSearch()
                            scope.launch { scaffoldState.drawerState.close() }
                            navController.navigate(items.path) {
                                popUpTo(Route.Home.route)
                            }
                        }
                    })
            ) {
                Row(
                    modifier = Modifier.padding(start = 20.dp)
                ) {
                    Icon(items.icon, "", tint = MaterialTheme.colors.onSurface)
                    Spacer(
                        modifier = Modifier
                            .width(15.dp)
                    )
                    Text(
                        text = items.title,
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp,
                        color = MaterialTheme.colors.onSurface
                    )
                }
            }
        }
        Column(
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 30.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.material.Text(
                    text = "Color Mode",
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp,
                    color = MaterialTheme.colors.onSurface
                )
                Spacer(
                    modifier = Modifier
                        .width(15.dp)
                )

                androidx.compose.material.Switch(
                    checked = currentTheme.value,
                    onCheckedChange = { toggleTheme() }
                )
            }
        }
    }
}