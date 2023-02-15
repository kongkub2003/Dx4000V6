package com.emc.edc.utils


import org.json.JSONObject
import java.math.BigInteger
import java.util.*
import kotlin.collections.ArrayList
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.animation.splineBasedDecay
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.util.rangeTo
import com.emc.edc.getLastTransaction
import com.emc.edc.getTodaySales
import com.emc.edc.globaldata.data.Month
import com.emc.edc.globaldata.dataclass.ISO8583
import com.emc.edc.transactionToJson

//import th.emerchant.terminal.edc_pos.getCardData
//import th.emerchant.terminal.edc_pos.getLastTransaction
//import th.emerchant.terminal.edc_pos.selectHost
//import th.emerchant.terminal.edc_pos.transactionToJson

class Utils {
    fun cardIsValid(cardNumber: String): Boolean {
        var s1 = 0
        var s2 = 0
        val reverse = StringBuffer(cardNumber).reverse().toString()
        for (i in reverse.indices) {
            val digit = Character.digit(reverse[i], 10)
            when {
                i % 2 == 0 -> s1 += digit
                else -> {
                    s2 += 2 * digit
                    when {
                        digit >= 5 -> s2 -= 9
                    }
                }
            }
        }
        return (s1 + s2) % 10 == 0
    }

    fun decimalToHexString(message: Byte): String {
        return when (message.toUByte().toString(16).length) {
            1 -> "0${message.toUByte().toString(16)}"
            else -> message.toUByte().toString(16)
        }
    }

    fun hexToBinary(hex: Any): String? {
        var bin: String = BigInteger(hex.toString(), 16).toString(2)
        val inb = bin.toInt()
        bin = java.lang.String.format(Locale.getDefault(), "%08d", inb)
        return bin
    }

    fun checkBitmap(bitmap: String): String? {
        return hexToBinaryString(bitmap)
    }

    fun convertDecimalToOctal(decimal: Int): Int {
        var decimal = decimal
        var octalNumber = 0
        var i = 1

        while (decimal != 0) {
            octalNumber += decimal % 8 * i
            decimal /= 8
            i *= 10
        }

        return octalNumber
    }

    fun hexToBinaryString(hex: Any): String? {
        return BigInteger(hex.toString(), 16).toString(2).padStart(64,'0')
    }

    fun binaryToHex(binary: Any): String? {
        val length = binary.toString()
        var dataHex = ""
        for (index in length.indices step 4) {
            dataHex += BigInteger(binary.toString().substring(index, index + 4), 2).toString(16)
        }
        return dataHex
    }

    fun asciiToHex(asciiValue: String): String {
        return asciiValue.map { it.code.toString(16) }.joinToString("")
    }

    fun hexToAscii(hexValue: String): String {
        try {
            if (hexValue.length % 2 != 0) {
                throw Exception("Invalid message")
            }
            val builder = StringBuilder()

            run {
                var i = 0
                while (i < hexValue.length) {

                    // Step-1 Split the hex string into two character group
                    val s: String = hexValue.substring(i, i + 2)
                    // Step-2 Convert the each character group into integer using valueOf method
                    val n = Integer.valueOf(s, 16)
                    // Step-3 Cast the integer value to char
                    builder.append(n.toChar())
                    i += 2
                }
            }

            return builder.toString()
        } catch (e: Exception) {
            Log.e("test", "hex to ascii exeption $e")
            throw  Exception(e.message)
        }
    }

    fun hexToString(message: List<Any>): String {
        val newmessage = StringBuilder()
        if (message.isEmpty()) {
            return ""
        }
        for (i in message) {
            newmessage.append(i)
        }
        return newmessage.toString()
    }

    fun cardMasking(fullCard: String, panMask: String): String {
        var cardMask = ""
        var index = 0
        for (char in panMask) {
            when (char) {
                'N' -> {
                    cardMask += fullCard[index]
                    index++
                }
                'X' -> {
                    cardMask += "X"
                    index++
                }
                else -> {
                    cardMask += " "
                }
            }
        }
        return cardMask
    }

    @SuppressLint("SimpleDateFormat")
    fun getDateTime(s: String): Date {
        return Date(s.toLong())
    }

    fun convertToDate(number: String): String {
        try {
            val date = number.substring(6, 8)
            val month = number.substring(4, 6)
            val year = number.substring(0, 4)

            val getMonthToString = Month.listOfMonth.single { it.numberOfMonth == month.toInt() }
            return "${getMonthToString.name.uppercase()} $date ,$year"

        } catch (e: Exception) {
            throw Exception(e.message)
        }
    }

    fun convertToTime(number: String): String {
        try {
            val hour = number.substring(0,2)
            val minute = number.substring(2,4)
            val second = number.substring(4,6)

            return "$hour:$minute:$second"
        } catch (e: Exception) {
            throw Exception(e.message)
        }
    }


    fun formatMoney(raw_amount: String) : String{
        //val test = raw_amount.dropLast(13)
        return when {
            raw_amount.isEmpty() -> {
                "0.00"
            }
            raw_amount.length == 1 -> {
                "0.0$raw_amount"
            }
            raw_amount.length == 2 -> {
                "0.$raw_amount"
            }


            else -> {
                "${ "%,d".format(raw_amount.dropLast(2).toLong()) }.${ raw_amount.drop(raw_amount.length-2) }"
            }
        }

    }

    fun formatMoneyD2S(amount: Double): String {
        //val test111 = amount.toString()
        val aaaa = String.format("%.2f", amount)
        val split_amount = aaaa.toString().split(".")
       // Log.v("test", "amount split: $split_amount")
        var amountDisplay = split_amount[0] + split_amount[1]
        if (split_amount[1].length == 1) amountDisplay += "0"
        val test = amountDisplay
        return amountDisplay
    }

    fun identifyCardScheme(cardNumber: String): CardScheme {
        val jcbRegex = Regex("^(?:2131|1800|35)[0-9]{0,}$")
        val ameRegex = Regex("^3[47][0-9]{0,}\$")
        val dinersRegex = Regex("^3(?:0[0-59]{1}|[689])[0-9]{0,}\$")
        val visaRegex = Regex("^4[0-9]{0,}\$")
        val masterCardRegex = Regex("^(5[1-5]|222[1-9]|22[3-9]|2[3-6]|27[01]|2720)[0-9]{0,}\$")
        val maestroRegex = Regex("^(5[06789]|6)[0-9]{0,}\$")
        val discoverRegex =
            Regex("^(6011|65|64[4-9]|62212[6-9]|6221[3-9]|622[2-8]|6229[01]|62292[0-5])[0-9]{0,}\$")

        val trimmedCardNumber = cardNumber.replace(" ", "")

        return when {
            trimmedCardNumber.matches(jcbRegex) -> CardScheme.JCB
            trimmedCardNumber.matches(ameRegex) -> CardScheme.AMEX
            trimmedCardNumber.matches(dinersRegex) -> CardScheme.DINERS_CLUB
            trimmedCardNumber.matches(visaRegex) -> CardScheme.VISA
            trimmedCardNumber.matches(masterCardRegex) -> CardScheme.MASTERCARD
            trimmedCardNumber.matches(discoverRegex) -> CardScheme.DISCOVER
            trimmedCardNumber.matches(maestroRegex) -> if (cardNumber[0] == '5') CardScheme.MASTERCARD else CardScheme.MAESTRO
            else -> CardScheme.UNKNOWN
        }
    }

    fun extractISO8583TOJSON(
        data: ArrayList<String>,
    ): JSONObject {
        val newData = JSONObject()
        ISO8583Extracting().extractISO8583TOJSON(data).let {
            val json = JSONObject()
            val jsonArray = json.getJSONArray("data")
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                newData.put(jsonObject.getString("key"), jsonObject.getString("transformData"))
            }
        }

        return newData
    }

    fun reprintLastTransaction(context: Context) {
        val lastTransaction = getLastTransaction()

        Log.v("TEST", "Last transaction: $lastTransaction")
        if (lastTransaction != null) {
            val lastTransactionJson = transactionToJson(lastTransaction.txn_type!!,lastTransaction)
            Printer().printSaleSlip(
                lastTransactionJson,
                context,
                "merchant"
            )
            Log.v("TEST", "Last transaction json: $lastTransactionJson")
        }
        else{
            Log.v("TEST", "Have not last transaction")
        }
    }

}

enum class CardScheme {
    JCB, AMEX, DINERS_CLUB, VISA, MASTERCARD, DISCOVER, MAESTRO, UNKNOWN
}