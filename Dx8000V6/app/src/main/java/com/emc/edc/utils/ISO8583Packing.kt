package com.emc.edc.utils

import android.provider.MediaStore.Images.Media.getBitmap
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import com.emc.edc.database.ConfigTransactionRO
import com.emc.edc.database.DataBitmapRO
import com.emc.edc.database.ProcessingCodeRO
import com.emc.edc.globaldata.data.ISO8583Data
import com.emc.edc.globaldata.dataclass.ISO8583
import io.realm.Realm
import io.realm.RealmList
import io.realm.kotlin.where
import org.json.JSONObject
import java.util.*
import kotlin.text.StringBuilder as StringBuilder1

class ISO8583Packing(
    private val transactionType: String?,
    private val cardNumber: String? = null,
    private val operation: String? = null,
    private val amount: String? = null,
    private val additionalAmount: String? = null,
    private val originalAmount: String? = null,
    private val cardEXP: String? = null,
    private val typeA0: String? = "credit_account",
    private val type0X: String? = "more_message_indicator",
    private val track1: String? = null,
    private val track2: String? = null,
    private val tid: String? = null,
    private val mid: String? = null,
    private val nii: String? = null,
    private val stan: Int?,
    private val invoice: Int?,
    private val refNumber: String?,
    private val approveCode: String?,
    private val responseCode: String?,
    private val date: String?,
    private val time: String?,
    private val realm: Realm,
    private val pos_entry_mode: String?
)
{
    private val isoBitmapEleData = ISO8583Data
    private var processingCodeReversal: String? = null
    private val utils = Utils()
    //private val algorithm: UAlgorithm = DeviceHelper.me().algorithm;

    private fun getTransactionDetail(): ConfigTransactionRO? {
        return realm.where<ConfigTransactionRO>().equalTo("type", transactionType).findFirst()
    }

    private fun getBitmapOperation(): DataBitmapRO {
        return getTransactionDetail()!!.bitmap!!.single { data -> data.type == operation }
    }

    private fun getTransactionMessageType(): String {
        return getTransactionDetail()!!.msgType!!
    }

    private fun getProcessingCodeList(): RealmList<String> {
        return getTransactionDetail()!!.processing_code!!
    }


    private fun getBitmap(): String? {
        val bitmap = MutableList<Int?>(64) { 0 }
        getBitmapOperation().bitmaps.forEach {
            bitmap[it - 1] = 1
        }

        return utils.binaryToHex(bitmap.joinToString(""))
    }
    private fun getBitmap(bitmapData: ArrayList<Int>): String? {
        val bitmap = MutableList<Int?>(64) { 0 }
        bitmapData.forEach {
            bitmap[it - 1] = 1
        }
        return utils.binaryToHex(bitmap.joinToString(""))
    }

    private fun getBitmap(bitmapData: List<Int>): String? {
        val bitmap = MutableList<Int?>(64) { 0 }
        bitmapData.forEach {
            bitmap[it - 1] = 1
        }
        return utils.binaryToHex(bitmap.joinToString(""))
    }

    private fun payloadLength(payload: String): String {
        return (payload.length / 2).toString(16).padStart(4, '0')
    }

    private fun getSrc(): String {
        return "8000"
    }

    private fun getTPDU(): String {
        val header = "60"
        val getBit = isoBitmapEleData.isoBitElement.single { it.bit == 24 }
        val niiVar = checkType(nii, getBit)
        return header + niiVar + getSrc()
    }

    private fun getPEM(): String? {
        return "1"
    }

    private fun getPCD(): String {
        return "00"
    }

    private fun getTrack1(): String {
        Log.d("Test", utils.asciiToHex(track1.toString()))
        return track1.toString()
    }

    private fun getTrack2(): String {
        return track2!!.replace("=", "d")
    }

    private fun getAdditionalAmount(): String {
        val transformAmount =
            additionalAmount?.replace(".", "")?.replace(",", "")?.padStart(12, '0')
                ?: throw Exception("Original amount is invalid")
        Log.d("Test", "Bit54 $transformAmount")
        return transformAmount
    }

    private fun getInvoice(): String {
        var invoice = invoice.toString()
        if (invoice.length < 6) {
            invoice = invoice.padStart(6, '0')
        }
        return invoice
    }

    private fun getOriginalAmount(): String {
        val transformOriginalAmount =
            originalAmount?.replace(".", "")?.replace(",", "")?.padStart(12, '0')
                ?: throw Exception("Original amount is invalid")
        Log.d("Test", "Bit60 $transformOriginalAmount")
        return transformOriginalAmount
    }

    fun getVersion(): String {
        return "04"
    }
    private fun getReversal(): Boolean {
        return getTransactionDetail()!!.reversal!!
    }

    private fun getBitElementDetail(bit: Int): String {
        val getBit = isoBitmapEleData.isoBitElement.single { it.bit == bit }
        val responseData: String
        when (bit) {
            2 -> {
                responseData = checkType(cardNumber, getBit)
            }
            3 -> {
                var processingCode = ""
                if (getProcessingCodeList().size > 0) {
                    getProcessingCodeList().forEach {
                        val currentName = it.lowercase(Locale.getDefault())
                        val getNameOfProcessingCode =
                            if (currentName == "a0" || currentName == "0x") {
                                realm.where<ProcessingCodeRO>().equalTo("name", currentName)
                                    .findFirst()
                            } else null
                        processingCode += when (currentName) {
                            "a0" -> {
                                getNameOfProcessingCode!!.data.single { data -> data.acc == typeA0 }.code.toString() + "0"
                            }
                            "0x" -> {
                                "0" + getNameOfProcessingCode!!.data.single { data -> data.acc == type0X }.code.toString()
                            }
                            else -> {
                                currentName
                            }
                        }
                    }
                } else {
                    processingCode = processingCodeReversal!!
                }
                responseData = processingCode
            }
            4 -> {
                val transformAmount =
                    amount?.replace(".", "") ?: throw Exception("Amount is invalid")
                responseData = checkType(transformAmount, getBit)
            }
            11 -> {
                responseData = checkType(stan.toString(), getBit)
            }
            12 -> {
                responseData = checkType(time, getBit)
            }
            13 -> {
                responseData = checkType(date, getBit)
            }
            14 -> {
                responseData = checkType(cardEXP, getBit)
            }
            22 -> {
                responseData = checkType(pos_entry_mode, getBit)
            }
            24 -> {
                responseData = checkType(nii, getBit)
            }
            25 -> {
                responseData = checkType(getPCD(), getBit)
            }
            35 -> {
                responseData = checkType(getTrack2(), getBit)
            }
            37 -> {
                responseData = checkType(refNumber, getBit)
            }
            38 -> {
                responseData = checkType(approveCode, getBit)
            }
            39 -> {
                responseData = checkType(responseCode, getBit)
            }
            41 -> {
                responseData = checkType(tid, getBit)
            }
            42 -> {
                responseData = checkType(mid, getBit)
            }
            45 -> {
                responseData = checkType(getTrack1(), getBit)
            }
            54 -> {
                responseData = checkType(getAdditionalAmount(), getBit)
            }
            60 -> {
                responseData = checkType(getOriginalAmount(), getBit)
            }
            62 -> {
                responseData = checkType(getInvoice(), getBit)
            }
            else -> {
                responseData = "x"
            }
        }
        return responseData
    }

    private fun checkType(data: String?, element: ISO8583): String {
        return if (data != null) {
            when (element.type) {
                "n" -> {
                    when (element.format) {
                        "llvar" -> llvarCodecNType(data, element.length)
                        else -> {
                            val paddingData = data.padStart(
                                if (element.length.mod(2) != 0) element.length + 1 else element.length,
                                '0'
                            )

                            checkLength(paddingData, element.length)
                        }
                    }
                }
                "an" -> {
                    Log.d("Test", utils.asciiToHex(data))
                    when (element.format) {
                        "llvar" -> llvarCodecAnType(utils.asciiToHex(data), element.length)
                        "lllvar" -> lllvarCodecAnType(utils.asciiToHex(data), element.length)
                        else -> {
                            var paddingData = utils.asciiToHex(data)
                            val lengthData = element.length - (paddingData.length/2)
                            for(i in 1..lengthData){
                                paddingData += "20"
                            }
                            checkLength(paddingData, element.length * 2)
                        }
                    }
                }
                "ans" -> {
                    when (element.format) {
                        "llvar" -> llvarCodecAnsType(utils.asciiToHex(data), element.length)
                        "lllvar" -> lllvarCodecAnsType(utils.asciiToHex(data), element.length)
                        else -> {
                            var paddingData = utils.asciiToHex(data)
                            val lengthData = element.length - (paddingData.length/2)
                            for(i in 1..lengthData){
                                paddingData += "20"
                            }
                            checkLength(paddingData, element.length * 2)
                        }
                    }
                }
                "z" -> {
                    when (element.format) {
                        "llvar" -> llvarCodecZType(data, element.length)
                        else -> {
                            val paddingData = data.padStart(element.length, '0')
                            checkLength(paddingData, element.length)
                        }
                    }
                }
                else -> {
                    Log.e("Test", "Error from checkType is none")
                    throw Exception("Data invalid")
                }
            }
        } else {
            Log.e("Test", "Error from checkType")
            throw Exception("Data invalid")
        }
    }

    private fun checkLength(data: String?, length: Int): String {
        val compareLength = if (length.mod(2) == 0) length else length + 1
        if (data!!.length <= compareLength) {
            return data.toString()
        } else {
            throw Exception("Data invalid format length $data")
        }
    }

    private fun llvarCodecNType(data: String?, length: Int): String {
        if (data!!.length <= length) {
            val dataLength = data.length
            return if (data.length.mod(2) != 0) {
                (dataLength).toString() + data + "F"
            } else {
                (dataLength).toString() + data
            }
        } else {
            throw Exception("Data invalid format")
        }
    }

    private fun llvarCodecZType(data: String?, length: Int): String {
        if (data!!.length <= length) {
            val dataLength = data.length
            return if (data.length.mod(2) != 0) {
                (dataLength).toString() + data + "F"
            } else {
                (dataLength).toString() + data
            }
        } else {
            throw Exception("Data invalid format")
        }
    }

    private fun llvarCodecAnsType(data: String?, length: Int): String {
        val dataLength = data!!.length / 2
        if (dataLength <= length) {
            return if (data.length.mod(2) != 0) {
                (dataLength).toString() + data + "F"
            } else {
                (dataLength).toString() + data
            }
        } else {
            throw Exception("Data invalid format")
        }
    }

    private fun lllvarCodecAnsType(data: String?, length: Int): String {
        val dataLength = data!!.length / 2
        if (dataLength <= length) {
            return if (dataLength.mod(2) != 0) {
                (dataLength).toString().padStart(4, '0') + data + "F"
            } else {
                (dataLength).toString().padStart(4, '0') + data
            }
        } else {
            throw Exception("Data invalid format")
        }
    }

    private fun llvarCodecAnType(data: String?, length: Int): String {
        val dataLength = data!!.length / 2
        if (dataLength <= length) {
            return if (data.length.mod(2) != 0) {
                (dataLength).toString() + data + "F"
            } else {
                (dataLength).toString() + data
            }
        } else {
            throw Exception("Data invalid format")
        }
    }

    private fun lllvarCodecAnType(data: String?, length: Int): String {
        val dataLength = data!!.length / 2
        if (dataLength <= length) {
            return if (data.length.mod(2) != 0) {
                (dataLength).toString().padStart(4, '0') + data + "F"
            } else {
                (dataLength).toString().padStart(4, '0') + data
            }
        } else {
            throw Exception("Data invalid format")
        }
    }

    private fun bitmapElement(): StringBuilder1 {
        val payload = StringBuilder1()
        getBitmapOperation().bitmaps.forEach {
            payload.append(getBitElementDetail(it))
        }
        return payload
    }

    private fun bitmapElement(bitmap: ArrayList<Int>): StringBuilder1 {
        val payload = StringBuilder1()
        bitmap.forEach {
            Log.d("Test bitmap loop", it.toString())
            payload.append(getBitElementDetail(it))
        }
        return payload
    }

    private fun messageLength(message: String): String {
        return (message.length / 2).toString(16).padStart(4, '0')
    }

    fun iso8583Payload(): String {
        val tpdu = getTPDU()
        val messageType = getTransactionMessageType()
        val bitmap = getBitmap()
        val data = bitmapElement().toString()
        val mergeData = tpdu + messageType + bitmap + data
        val headerLength = messageLength(mergeData)
        return headerLength + mergeData
    }
    fun iso8583ReversalPayload(): String {
        return if (getReversal()) {
            val tpdu = getTPDU()
            val messageType = "0400"
            val bitmap: ArrayList<Int> = arrayListOf(2, 3, 4, 11, 14, 22, 24, 25, 41, 42, 62)
            val data = bitmapElement(bitmap).toString()
            val mergeData = tpdu + messageType + getBitmap(bitmap) + data
            val headerLength = payloadLength(mergeData)
            headerLength + mergeData
        } else {
            ""
        }
    }
//private fun

}