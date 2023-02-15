package com.emc.edc

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import com.emc.edc.ui.amount.AmountScreen
import com.emc.edc.ui.card_display.CardDisplay
import com.emc.edc.ui.card_entry.CardEntry
import com.emc.edc.ui.sale_selection.SelectOperation
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import kotlinx.coroutines.DelicateCoroutinesApi
import org.json.JSONObject
import th.emerchant.terminal.edc_pos.screen.transaction.enter_password_pin.EnterPasswordPinScreen
import com.emc.edc.ui.search_transaction.SearchTransactionScreen
import com.emc.edc.ui.transaction_display.TransactionDisplay
import th.emerchant.terminal.edc_pos.screen.transaction.key_in.KeyIn

@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalComposeUiApi
@DelicateCoroutinesApi
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun Navigation(context: Context) {
    val navController = rememberAnimatedNavController()
    AnimatedNavHost(
        navController, startDestination = Route.Home.route,
    ) {

        composable(
            Route.Home.route,
            exitTransition = { ->
                slideOutHorizontally(
                    targetOffsetX = { -1000 },
                )
            },
            popEnterTransition = { ->
                slideInHorizontally(
                    initialOffsetX = { -1000 },
                )
            },

            ) {
            MainScreen(navController, context)
        }
        composable(
            Route.Amount.route + "/{data}",
            enterTransition = { ->
                slideInHorizontally(
                    initialOffsetX = { 1000 },
                )
            },
            exitTransition = { ->
                slideOutHorizontally(
                    targetOffsetX = { 1000 },
                )
            },
            popEnterTransition = { ->
                slideInHorizontally(
                    initialOffsetX = { -1000 },
                )
            },
        ) { navBackStack ->
            val data = JSONObject(navBackStack.arguments?.getString("data"))

            AmountScreen(
                context,
                navController,
                data.getString("transaction_type"),
                data.getString("title")
            )
        }

        composable(
            Route.Select.route + "/{data}",
            enterTransition = { ->
                slideInHorizontally(
                    initialOffsetX = { 1000 },
                )
            },
            exitTransition = { ->
                slideOutHorizontally(
                    targetOffsetX = { -1000 },
                )
            },
        ) { navBackStack ->
            val data = navBackStack.arguments?.getString("data")

            SelectOperation(navController, data!!)
        }
        composable(
            Route.CardEntry.route + "/{data}",
            enterTransition = { ->
                slideInHorizontally(
                    initialOffsetX = { 1000 },
                )
            },
            exitTransition = { ->
                slideOutHorizontally(
                    targetOffsetX = { -1000 },
                )
            },
        ) { navBackStack ->
            val data = navBackStack.arguments?.getString("data")

            CardEntry(
                context,navController, data!!
            )
        }
        composable(
            Route.CardDisplay.route + "/{data}",
            enterTransition = { ->
                slideInHorizontally(
                    initialOffsetX = { 1000 },
                )
            },
            exitTransition = { ->
                slideOutHorizontally(
                    targetOffsetX = { 1000 },
                )
            },
        ) { navBackStack ->
            val data = navBackStack.arguments?.getString("data")
            val hasError = remember { mutableStateOf(false) }
            val errMessage = remember { mutableStateOf("") }
            val jsonData = remember { mutableStateOf(JSONObject(data)) }
            val responseOnlineStatus = remember { mutableStateOf(false) }
            var dialogStatus = remember { mutableStateOf(false) }
            var connectionStatus = remember { mutableStateOf(false) }
            val textLoading = remember { mutableStateOf("") }

            CardDisplay(
                navController,
                hasError,
                errMessage,
                jsonData,
                responseOnlineStatus,
                dialogStatus,
                connectionStatus,
                textLoading,
                data
            )        }
        composable(
            Route.EnterPasswordPin.route + "/{data}",
            enterTransition = { ->
                slideInHorizontally(
                    initialOffsetX = { 1000 },
                )
            },
            exitTransition = { ->
                slideOutHorizontally(
                    targetOffsetX = { 1000 },
                )
            },
            popEnterTransition = { ->
                slideInHorizontally(
                    initialOffsetX = { -1000 },
                )
            }
        ) { navBackStack ->
            val data = JSONObject(navBackStack.arguments?.getString("data"))
            val route = if (data.has("route")) data.getString("route") else null

            EnterPasswordPinScreen(context, navController, data, route)
        }
        composable(
            Route.SearchTransaction.route + "/{data}",
            enterTransition = { ->
                slideInHorizontally(
                    initialOffsetX = { 1000 },
                )
            },
            exitTransition = { ->
                slideOutHorizontally(
                    targetOffsetX = { 1000 },
                )
            },
            popEnterTransition = { ->
                slideInHorizontally(
                    initialOffsetX = { -1000 },
                )
            },
        ) { navBackStack ->
            val data = JSONObject(navBackStack.arguments?.getString("data"))

            SearchTransactionScreen(
                context, navController,
                data.getString("transaction_type"), data.getString("transaction_title")
            )
        }
        composable(
            Route.TransactionDisplay.route + "/{data}",
            enterTransition = { ->
                slideInHorizontally(
                    initialOffsetX = { 1000 },
                )
            },
            exitTransition = { ->
                slideOutHorizontally(
                    targetOffsetX = { 1000 },
                )
            },
        ) { navBackStack ->
            val data = navBackStack.arguments?.getString("data")

            TransactionDisplay(navController, data!!, context)
        }
        composable(
            Route.KeyIn.route + "/{data}",
            enterTransition = { ->
                slideInHorizontally(
                    initialOffsetX = { 1000 },
                )
            },
            exitTransition = { ->
                slideOutHorizontally(
                    targetOffsetX = { -1000 },
                )
            },
        ) { navBackStack ->
            val data = navBackStack.arguments?.getString("data")

            KeyIn(
                context,navController, data!!
            )
        }
    }

}