package th.emerchant.terminal.edc_pos.screen.transaction.enter_password_pin


import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import com.emc.edc.Route
import com.emc.edc.checkPasswordApp
import com.emc.edc.utils.*
import org.json.JSONObject


@RequiresApi(Build.VERSION_CODES.O)
suspend fun enterPasswordPinHandleButtonClick(
    context: Context?,
    txt: String,
    numberPassword: MutableState<Int>,
    password: MutableState<String>,
    navController: NavHostController,
    jsonData: JSONObject,
    errMessage: MutableState<String>,
    loading: MutableState<Boolean>,
    textLoading: MutableState<String>,
    popupPrintSlipCustomer: MutableState<Boolean>,
    popUpContinue: MutableState<Boolean>,
    route: String?
) {

//    Log.v("TEST","numpad: $txt password: ${password.value}")
    if (txt=="⌫" && password.value.isNotEmpty()) {
        password.value = password.value.dropLast(1)
    }
    else if (txt!="⌫" && txt!="" && password.value.length < numberPassword.value ) {
        password.value += txt
        if (password.value.length == numberPassword.value){
            textLoading.value = "Checking Password..."
            loading.value = true

            if (checkPasswordApp(password.value)) {
                Log.v("test", "correct password")
                if (route != null) {
                    navController.navigate("$route/$jsonData") {
                        popUpTo(Route.Home.route)
                    }
                } else {
/*
                    var responseTransaction =
                        TransportData().sendOnlineTransaction(
                            jsonData, Context, errMessage, loading,
                            textLoading, popupPrintSlipCustomer, popUpContinue
                        )
                    Log.v(
                        "test",
                        "response ${jsonData.getString("transaction_type")}: $responseTransaction")*/

                }
            }
            else{
                loading.value = false
                Toast.makeText(context, "Password incorrect", Toast.LENGTH_SHORT).show()
                password.value = ""
            }
        }
    }
}
   // Log.v("TEST","amount ${formatAmount(raw_amount.value)}")




