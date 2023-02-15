package com.emc.edc.utils
import android.util.Log
import com.emc.edc.globaldata.data.Bit39
import com.emc.edc.globaldata.data.ISO8583Data
import com.emc.edc.globaldata.dataclass.ISO8583
import com.emc.edc.globaldata.dataclass.ISO8583BitMap
import com.emc.edc.globaldata.dataclass.ISO8583Map
import com.google.gson.Gson
import org.json.JSONObject

class ISO8583Extracting {


    private val isoBitmapEleData = ISO8583Data
    private val utils = Utils()

    private fun hexBitmapToBinary(message:  List<Any> ): String {
        val newmessage = StringBuilder()
        if (message.isEmpty()) {
            return ""
        }
        for (i in message) {
            newmessage.append(utils.hexToBinary(i))
        }
        return newmessage.toString()
    }

    private fun bitmapEnable(bitmap: String): ArrayList<Int> {
        val bitmapList: ArrayList<Int> = ArrayList()
        for ((i, v) in bitmap.withIndex()) {
            if (v.toString() == "1") {
                bitmapList.add(i + 1)
            }
        }
        return bitmapList
    }

    fun getISO8583Extract(message: ArrayList<String>): String {
        try {
            val verifyMessageLength = (message[0] + message[1]).toInt()  == message.size - 2
            Log.d("test", "Verify Length $verifyMessageLength")
            if (verifyMessageLength) {
                if ( message[2] == "60") {
                    var currentIndex = 17;
                    val bitmap = getBitMap(message)
                    val listOfData: ArrayList<ISO8583BitMap> = ArrayList()
                    for (i in bitmap) {
                        val (data, index, respmessageTransform) = iso8583Extract(message, i, currentIndex)
                        val (getKey) = isoBitmapEleData.isoBitElement.filter { it.bit == i }
                        if (index != "") {
                            currentIndex = index as Int
                        }
                        val newData = ISO8583BitMap(i, data.toString(), respmessageTransform.toString(), getKey.key)
                        listOfData.add(newData)
                    }
                    val data = ISO8583Map( getTPDU(message), getMessageType(message), getBitMapString(message), listOfData)
                    return  Gson().toJson(data)
                } else {
                    throw Exception("Start message not 60")
                }
            } else {
                throw Exception("Length Invalid")
            }
        } catch (e: Exception) {
            Log.e("test", "Extract exeption $e")
            throw Exception(e.message)
        }
    }

    fun extractISO8583TOJSON(
        data: ArrayList<String>,
    ): JSONObject {
        val newData = JSONObject()
        ISO8583Extracting().getISO8583Extract(data).let {
            val json = JSONObject(it)
            val jsonArray = json.getJSONArray("data")
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                newData.put(jsonObject.getString("key"), jsonObject.getString("transformData"))
            }
        }

        return newData
    }

    private fun getLength(message: ArrayList<String>): String {
        val lengthFilter = message.filterIndexed { index, _ -> index in 0..1 }
        return utils.hexToString(lengthFilter)
    }


    private fun getTPDU(message: ArrayList<String>): String {
        val tpduFilter = message.filterIndexed { index, _ -> index in 2..6 }
        return utils.hexToString(tpduFilter)
    }

    private fun getMessageType(message: ArrayList<String>): String {
        val msgTypeFilter = message.filterIndexed { index, _ -> index in 7..8 }
        return utils.hexToString(msgTypeFilter)
    }

    private fun getBitMap(message: ArrayList<String>): ArrayList<Int> {
        val bitMapFilter = message.filterIndexed { index, _ -> index in 9..16 }
        return bitmapEnable(hexBitmapToBinary(bitMapFilter))
    }

    private fun getBitMapString(message: ArrayList<String>): String {
        val bitMapFilter = message.filterIndexed { index, _ -> index in 9..16 }
        return utils.hexToString(bitMapFilter)
    }

    private fun iso8583Extract
                (message: ArrayList<String>, bit: Int, currentIndex: Int): Array<Any> {
        var respmessage = ""
        var curIndex = 0
        var errHandle = ""
        var respmessageTransform = ""
        val getBit = isoBitmapEleData.isoBitElement.single { it.bit == bit }
        try {
            when (bit) {
                1 -> {
                    val resp = message.filterIndexed { index, _ -> index in 9..16 }
                    respmessage = utils.hexToString(resp)
                    respmessageTransform = utils.hexToString(resp)
                    curIndex = 17
                }
                2 -> {
                    if (message[currentIndex].toInt() > 19) {
                        errHandle = "Invalid [DE2] Length -> " + message[currentIndex].toInt()
                        throw Exception(errHandle)
                    }
                    val checkRealLength = when(message[currentIndex].toInt().mod(2) != 0){
                        true -> message[currentIndex].toInt() + 1
                        false -> message[currentIndex].toInt()
                    }
                    val resp =
                        message.filterIndexed { index, _ -> index in currentIndex..currentIndex + checkRealLength / 2 }
                    respmessage = utils.hexToString(resp)
                    respmessageTransform = checkType(utils.hexToString(resp), getBit)
                    curIndex = currentIndex + checkRealLength / 2 + 1
                }
                3,11,12 -> {
                    val resp =
                        message.filterIndexed { index, _ -> index in currentIndex..2 + currentIndex }
                    respmessage = utils.hexToString(resp)
                    respmessageTransform = checkType(utils.hexToString(resp), getBit)
                    curIndex = 3 + currentIndex
                }
                4,5,6,38 -> {
                    val resp =
                        message.filterIndexed { index, _ -> index in currentIndex..5 + currentIndex }
                    respmessage = utils.hexToString(resp)
                    respmessageTransform = checkType(utils.hexToString(resp), getBit)
                    curIndex = 6 + currentIndex
                }
                7 -> {
                    val resp =
                        message.filterIndexed { index, _ -> index in currentIndex..4 + currentIndex }
                    respmessage = utils.hexToString(resp)
                    respmessageTransform = checkType(utils.hexToString(resp), getBit)
                    curIndex = 5 + currentIndex
                }
                13,14,15,16,17,18,22,24,39 -> {
                    val resp =
                        message.filterIndexed { index, _ -> index in currentIndex..1 + currentIndex }
                    respmessage = utils.hexToString(resp)
                    respmessageTransform = checkType(utils.hexToString(resp), getBit)
                    curIndex = 2 + currentIndex
                }
                25,26 -> {
                    val resp =
                        message.filterIndexed { index, _ -> index in currentIndex..currentIndex }
                    respmessage = utils.hexToString(resp)
                    respmessageTransform = checkType(utils.hexToString(resp), getBit)
                    curIndex = 1 + currentIndex
                }
                35 -> {
                    if (message[currentIndex].toInt() > 37) {
                        errHandle = "Invalid [DE35] Length -> " + message[currentIndex].toInt()
                        throw Exception(errHandle)
                    }

                    //val checkRealLength = when(message[currentIndex].toInt() == 37) {
                    val checkRealLength = when(message[currentIndex].toInt().mod(2) != 0){
                        true -> message[currentIndex].toInt() + 1
                        false -> message[currentIndex].toInt()
                    }
                    val resp =
                        message.filterIndexed { index, _ -> index in currentIndex..currentIndex + checkRealLength / 2 }
                    respmessage = utils.hexToString(resp)
                    //TODO("Please check may be wrong")
                    respmessageTransform = checkType(utils.hexToString(resp), getBit)
                    curIndex = currentIndex + checkRealLength / 2 + 1
                }
                37, -> {
                    val resp =
                        message.filterIndexed { index, _ -> index in currentIndex..11 + currentIndex }
                    respmessage = utils.hexToString(resp)
                    respmessageTransform = checkType(utils.hexToString(resp), getBit)
                    curIndex = 12 + currentIndex
                }
                41 -> {
                    val resp =
                        message.filterIndexed { index, _ -> index in currentIndex..7 + currentIndex }
                    respmessage = utils.hexToString(resp)
                    respmessageTransform = checkType(utils.hexToString(resp), getBit)
                    curIndex = 8 + currentIndex
                }
                42 -> {
                    val resp =
                        message.filterIndexed { index, _ -> index in currentIndex..14 + currentIndex }
                    respmessage = utils.hexToString(resp)
                    respmessageTransform = checkType(utils.hexToString(resp), getBit)
                    curIndex = 15 + currentIndex
                }
                43 -> {
                    val resp =
                        message.filterIndexed { index, _ -> index in currentIndex..39 + currentIndex }
                    respmessage = utils.hexToString(resp)
                    respmessageTransform = checkType(utils.hexToString(resp), getBit)
                    curIndex = 40 + currentIndex
                }
                45 -> {
                    if (message[currentIndex].toInt() > 76) {
                        errHandle = "Invalid [DE45] Length -> " + message[currentIndex].toInt()
                        throw Exception(errHandle)
                    }

                    val checkRealLength = message[currentIndex].toInt()
                    val resp =
                        message.filterIndexed { index, _ -> index in currentIndex..currentIndex + checkRealLength  }
                    respmessage = utils.hexToString(resp)
                    respmessageTransform = checkType(utils.hexToString(resp), getBit)
                    curIndex = currentIndex + checkRealLength  + 1
                }
                52, 64 -> {
                    val resp =
                        message.filterIndexed { index, _ -> index in currentIndex..7 + currentIndex  }
                    respmessage = utils.hexToString(resp)
                    respmessageTransform = checkType(utils.hexToString(resp), getBit)
                    curIndex = currentIndex + 8

                }
                53 -> {
                    val resp =
                        message.filterIndexed { index, _ -> index in currentIndex..7 + currentIndex  }
                    respmessage = utils.hexToString(resp)
                    respmessageTransform = respmessage
                    curIndex = currentIndex + 8

                }
                54 -> {
                    //TODO("Please check")
                    if ((message[currentIndex] + message[currentIndex + 1]).toInt() > 120) {
                        errHandle = "Invalid [DE54] Length -> " + (message[currentIndex] + message[currentIndex + 1]).toInt()
                        throw Exception(errHandle)
                    }

                    val checkRealLength = (message[currentIndex] + message[currentIndex + 1]).toInt()
                    val resp =
                        message.filterIndexed { index, _ -> index in currentIndex..currentIndex + checkRealLength + 1  }
                    respmessage = utils.hexToString(resp)
                    respmessageTransform = checkType(utils.hexToString(resp), getBit)
                    curIndex = currentIndex + checkRealLength  + 2
                }
                55 -> {
                    if ((message[currentIndex] + message[currentIndex + 1]).toInt() > 255) {
                        errHandle = "Invalid [DE$bit] Length -> " + (message[currentIndex] + message[currentIndex + 1]).toInt()
                        throw Exception(errHandle)
                    }

                    val checkRealLength =  (message[currentIndex] + message[currentIndex + 1]).toInt()
                    val resp =
                        message.filterIndexed { index, _ -> index in currentIndex..currentIndex + checkRealLength + 1  }
                    respmessage = utils.hexToString(resp)
                    Log.d("test", "b $respmessage")
                    respmessageTransform = checkType(utils.hexToString(resp), getBit)
                    curIndex = currentIndex + checkRealLength  + 2

                }
                48,60,61,62,63 -> {
                    //TODO("Please check")
                    if ((message[currentIndex] + message[currentIndex + 1]).toInt() > 999) {
                        errHandle = "Invalid [DE$bit] Length -> " + (message[currentIndex] + message[currentIndex + 1]).toInt()
                        throw Exception(errHandle)
                    }

                    val checkRealLength =  (message[currentIndex] + message[currentIndex + 1]).toInt()
                    val resp =
                        message.filterIndexed { index, _ -> index in currentIndex..currentIndex + checkRealLength + 1  }
                    respmessage = utils.hexToString(resp)
                    Log.d("test", "ANS $respmessage")
                    respmessageTransform = checkType(utils.hexToString(resp), getBit)
                    curIndex = currentIndex + checkRealLength  + 2
                }
                else -> {
                    respmessage = ""
                    curIndex = 0
                }
            }
            return arrayOf(respmessage, curIndex, respmessageTransform)
        } catch (e: Exception) {
            Log.e("test", "Extract exeption $e")
            throw  Exception(e.message)
        }
    }

    private fun checkType(data: String?, element: ISO8583): String {
        try {
            return if (data!!.isNotEmpty()) {
                when (element.type) {
                    "n" -> {
                        when (element.format) {
                            "llvar" -> llvarCodecNType(data)
                            else -> {
                                data
                            }
                        }
                    }
                    "an" -> {
                        when (element.format) {
                            "llvar" -> {
                                val removeLength = llvarCodecAnsType(data)
                                utils.hexToAscii(removeLength)
                            }
                            "lllvar" -> {
                                val removeLength = lllvarCodecAnsType(data)
                                utils.hexToAscii(removeLength)
                            }
                            else -> {
                                utils.hexToAscii(data)
                            }
                        }
                    }
                    "ans" -> {
                        when (element.format) {
                            "llvar" -> {
                                val removeLength = llvarCodecAnsType(data)
                                utils.hexToAscii(removeLength)
                            }
                            "lllvar" -> {
                                val removeLength = lllvarCodecAnsType(data)
                                utils.hexToAscii(removeLength)
                            }
                            else -> {
                                utils.hexToAscii(data)
                            }
                        }
                    }
                    "z" -> {
                        when (element.format) {
                            "llvar" -> llvarCodecZType(data)
                            else -> {
                                data
                            }
                        }
                    }
                    "b" -> {
                        when (element.format) {
                            "lllvar" -> llvarCodecBType(data)
                            else -> {
                                data
                            }
                        }
                    }
                    else -> {
                        Log.e("test", "Data invalid ${element.bit}")
                        throw Exception("Data invalid")
                    }
                }
            } else {
                throw Exception("Data invalid")
            }
        } catch (e: Exception) {
            Log.e("test", "Extract check type ${element.bit}")
            throw  Exception(e.message)
        }
    }

    private fun llvarCodecNType(data: String?): String {
        return data!!.removeRange(0, 2)
    }

    private fun llvarCodecAnsType(data: String?): String {
        return data!!.removeRange(0, 2)
    }

    private fun lllvarCodecAnsType(data: String?): String {
        Log.d("test", "lllvar $data")
        return data!!.removeRange(0, 4)
    }

    private fun llvarCodecZType(data: String?): String {
        return data!!.removeRange(0, 2)
    }

    private fun llvarCodecBType(data: String?): String {
        return data!!.removeRange(0, 4)
    }

    fun checkBit39Payload(data: String?): Array<Any> {
        try {
            var isApprove = false
            var meaning = ""
            val listOfApproves = listOf("00", "10", "11", "16")
            if (Bit39.list.any { it.resp_code == data }) {
                val filterBit39Data = Bit39.list.single { it.resp_code == data }
                if (listOfApproves.any { it == data }) {
                    isApprove = true
                    meaning = filterBit39Data.meaning
                } else {
                    isApprove = false
                    meaning = filterBit39Data.meaning
                }
            }else {
                isApprove = false
                meaning = "Cannot find meaning by this code"
            }

            return arrayOf(isApprove, meaning)

        } catch (e: Exception) {
            throw Exception("Cannot check Bit 39 message")
        }
    }
}