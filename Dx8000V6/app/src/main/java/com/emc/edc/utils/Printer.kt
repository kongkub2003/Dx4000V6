package com.emc.edc.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.RemoteException
import android.util.Log
import androidx.annotation.RequiresApi
import com.emc.edc.emv.DeviceHelper
import com.emc.edc.getDataSetting
import com.emc.edc.getMerchantData
import com.emc.edc.globaldata.dataclass.ISO8583
import com.usdk.apiservice.aidl.printer.*
import kotlinx.coroutines.delay
import com.usdk.apiservice.aidl.vectorprinter.Alignment
import com.usdk.apiservice.aidl.vectorprinter.TextSize
import com.usdk.apiservice.aidl.vectorprinter.UVectorPrinter
import com.usdk.apiservice.aidl.vectorprinter.VectorPrinterData
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.time.Instant
import java.time.format.DateTimeFormatter

class Printer {
    private val printer: UPrinter = DeviceHelper.me().printer
    private val vectorPrinter: UVectorPrinter? = DeviceHelper.me().vectorPrinter
    private val utils = Utils()

    fun stringToArrayHexString(data: String): ArrayList<String> {
        val isoToArray: ArrayList<String> = ArrayList()
        for (i in 0 until data.length - 1 step 2) {
            isoToArray.add(
                (data[i]).toString() + (data[i + 1]).toString()
            )
        }
        return isoToArray
    }

    fun printSaleSlip(data: JSONObject, context: Context, copyFor: String) {
        try {
            val merchantData = getMerchantData()
            val checkOperationType =
                when {
                    data.getString("operation") == "magnetic" -> {
                        "S"
                    }
                    data.getString("operation") == "key_in" -> {
                        "K"
                    }
                    else -> {
                        "U"
                    }
                }

            val nameSignature = data.getString("name").uppercase().split(" ")
            val logo = readAssetsFile(context, "logo_emerchant.bmp")
            printer.setPrintFormat(PrintFormat.FORMAT_ZEROSPECSET,
                PrintFormat.VALUE_ZEROSPECSET_DEFAULTZERO)
            printer.addBmpImage(0, FactorMode.BMP1X1, logo)
            printer.setPrintFormat(
                PrintFormat.FORMAT_MOREDATAPROC,
                PrintFormat.VALUE_MOREDATAPROC_PRNONELINE
            )

            printer.setPrnGray(10)

            printer.addText(AlignMode.LEFT, "_______________________________________")
            printer.feedLine(1)
            printer.setPrnGray(3)
//        printer.setPrintFormat(
//            PrintFormat.FORMAT_MOREDATAPROC, PrintFormat.VALUE_MOREDATAPROC_PRNTOEND
//        )

//        printer.setAscScale(ASCScale.SC2x1)
//        printer.setAscSize(ASCSize.DOT24x8)
            printer.addText(AlignMode.CENTER, merchantData.getString("merchant_name"))
            printer.addText(AlignMode.CENTER, merchantData.getString("merchant_location"))
            printer.addText(AlignMode.CENTER, merchantData.getString("merchant_convince"))
            printer.feedLine(1)

            printer.addText(AlignMode.LEFT, "TID:${data.getString("tid")}")
            printer.addText(AlignMode.LEFT, "MID:${data.getString("mid")}")
            printer.addText(AlignMode.LEFT, "HOST:${data.getString("host_label")}")

            val dateTime: MutableList<Bundle> = ArrayList()
            val date = Bundle()
            date.putString(PrinterData.TEXT, utils.convertToDate(data.getString("date")))
            date.putInt(PrinterData.ALIGN_MODE, AlignMode.LEFT)
            dateTime.add(date)
            val time = Bundle()
            time.putString(PrinterData.TEXT, "${utils.convertToTime(data.getString("time"))} ")
            time.putInt(PrinterData.ALIGN_MODE, AlignMode.RIGHT)
            dateTime.add(time)
            printer.addMixStyleText(dateTime)

            printer.addText(AlignMode.LEFT, "----------------------------------------------")

            val type: MutableList<Bundle> = ArrayList()
            val typeOfCard = Bundle()
            typeOfCard.putString(PrinterData.TEXT, data.getString("card_label"))
            typeOfCard.putInt(PrinterData.ALIGN_MODE, AlignMode.LEFT)
            type.add(typeOfCard)
            val typeOfOperation = Bundle()
            typeOfOperation.putString(PrinterData.TEXT,
                "${data.getString("transaction_type").uppercase()} ")
            typeOfOperation.putInt(PrinterData.ALIGN_MODE, AlignMode.RIGHT)
            type.add(typeOfOperation)
            printer.addMixStyleText(type)

            printer.addText(AlignMode.LEFT,
                "${data.getString("card_number_mask").uppercase()}  /$checkOperationType")
            printer.addText(AlignMode.LEFT,
                "TRACE# :${data.getString("invoice").padStart(6, '0').uppercase()}")
            printer.addText(AlignMode.LEFT,
                "STAN# :${data.getString("stan").padStart(6, '0').uppercase()}")
            printer.addText(AlignMode.LEFT,
                "BATCH# :${data.getString("batch_number").padStart(6, '0').uppercase()}")
            printer.addText(AlignMode.LEFT,
                "REF NO :${data.getString("ref_num").padStart(6, '0').uppercase()}")
            printer.addText(AlignMode.LEFT,
                "APPROVAL CODE :${data.getString("auth_id").padStart(6, '0').uppercase()}")
            printer.addText(AlignMode.LEFT, "EXP DATE :XX/XX")

            val cardName: MutableList<Bundle> = ArrayList()
            val cardnameKey = Bundle()
            cardnameKey.putString(PrinterData.TEXT, "CARD NAME")
            cardnameKey.putInt(PrinterData.ALIGN_MODE, AlignMode.LEFT)
            cardName.add(cardnameKey)
            val cardNameValue = Bundle()
            cardNameValue.putString(PrinterData.TEXT, data.getString("name").uppercase())
            cardNameValue.putInt(PrinterData.ALIGN_MODE, AlignMode.RIGHT)
            cardName.add(cardNameValue)
            printer.addMixStyleText(cardName)

            val amount: MutableList<Bundle> = ArrayList()
            val amountKey = Bundle()
            amountKey.putString(PrinterData.TEXT, "BASE")
            amountKey.putInt(PrinterData.ALIGN_MODE, AlignMode.LEFT)
            amountKey.putInt(PrinterData.ASC_SCALE, ASCScale.SC1x2)
            amountKey.putInt(PrinterData.ASC_SIZE, ASCSize.DOT24x12)
            amount.add(amountKey)
            val amountTotal = Bundle()
            amountTotal.putString(PrinterData.TEXT, "THB *${data.getString("amount").uppercase()} ")
            amountTotal.putInt(PrinterData.ALIGN_MODE, AlignMode.RIGHT)
            amountTotal.putInt(PrinterData.ASC_SCALE, ASCScale.SC1x2)
            amountTotal.putInt(PrinterData.ASC_SIZE, ASCSize.DOT24x12)
            amount.add(amountTotal)
            printer.addMixStyleText(amount)

            val tipAmount: MutableList<Bundle> = ArrayList()
            val tipAmountKey = Bundle()
            tipAmountKey.putString(PrinterData.TEXT, "TIP")
            tipAmountKey.putInt(PrinterData.ALIGN_MODE, AlignMode.LEFT)
            tipAmountKey.putInt(PrinterData.ASC_SCALE, ASCScale.SC1x2)
            tipAmountKey.putInt(PrinterData.ASC_SIZE, ASCSize.DOT24x12)
            tipAmount.add(tipAmountKey)
            val tipAmountTotal = Bundle()
            tipAmountTotal.putString(PrinterData.TEXT, "____________ ")
            tipAmountTotal.putInt(PrinterData.ALIGN_MODE, AlignMode.RIGHT)
            tipAmountTotal.putInt(PrinterData.ASC_SCALE, ASCScale.SC1x2)
            tipAmountTotal.putInt(PrinterData.ASC_SIZE, ASCSize.DOT24x12)
            tipAmount.add(tipAmountTotal)
            printer.addMixStyleText(tipAmount)

            val totalAmount: MutableList<Bundle> = ArrayList()
            val totalAmountKey = Bundle()
            totalAmountKey.putString(PrinterData.TEXT, "TOTAL")
            totalAmountKey.putInt(PrinterData.ALIGN_MODE, AlignMode.LEFT)
            totalAmountKey.putInt(PrinterData.ASC_SCALE, ASCScale.SC1x2)
            totalAmountKey.putInt(PrinterData.ASC_SIZE, ASCSize.DOT24x12)
            totalAmount.add(totalAmountKey)
            val totalAmountTotal = Bundle()
            totalAmountTotal.putString(PrinterData.TEXT, "____________ ")
            totalAmountTotal.putInt(PrinterData.ALIGN_MODE, AlignMode.RIGHT)
            totalAmountTotal.putInt(PrinterData.ASC_SCALE, ASCScale.SC1x2)
            totalAmountTotal.putInt(PrinterData.ASC_SIZE, ASCSize.DOT24x12)
            totalAmount.add(tipAmountTotal)
            printer.addMixStyleText(totalAmount)

            printer.addText(AlignMode.LEFT, "----------------------------------------------")

            if (copyFor == "merchant") {
                printer.setAscScale(ASCScale.SC1x2)
                printer.setAscSize(ASCSize.DOT7x7)
                printer.addText(AlignMode.LEFT, "SIGN X _ _ _ _ _ _ _ _ _ _ _ _ ")

                printer.setAscScale(ASCScale.SC1x1)
                printer.setAscSize(ASCSize.DOT5x7)
                printer.addText(AlignMode.CENTER, "${nameSignature[1]}/${nameSignature[0]}")

                printer.setAscScale(0)
                printer.setAscSize(0)
                printer.addText(AlignMode.LEFT, "----------------------------------------------")

                printer.setAscScale(ASCScale.SC1x1)
                printer.setAscSize(ASCSize.DOT5x7)
                printer.addText(AlignMode.CENTER, "** MERCHANT COPY **")
            } else {
                printer.setAscScale(ASCScale.SC1x1)
                printer.setAscSize(ASCSize.DOT5x7)
                printer.addText(AlignMode.CENTER, "** CUSTOMER COPY **")
            }


            printer.setAscScale(ASCScale.SC1x1)
            printer.setAscSize(ASCSize.DOT24x8)
            printer.addText(AlignMode.CENTER, "I ACKNOWLEDGE SATISFACTORY RECEIPT")
            printer.addText(AlignMode.CENTER, "OF RELATIVE GOODS/SERVICES")

            printer.setPrintFormat(0, 0)
            printer.setAscScale(0)
            printer.setAscSize(0)

            printer.setAscScale(ASCScale.SC1x1)
            printer.setAscSize(ASCSize.DOT5x7)
            printer.addText(AlignMode.CENTER, "*** NO REFUND ***")

            printer.setPrintFormat(0, 0)
            printer.setAscScale(0)
            printer.setAscSize(0)


            printer.feedLine(5)


            printer.startPrint(object :
                OnPrintListener.Stub() {
                @Throws(RemoteException::class)
                override fun onFinish() {

                }


                @Throws(RemoteException::class)
                override fun onError(p0: Int) {
                    Log.e("err", "printer is ... $p0")
                }
            })
        }
        catch (e:Exception){
            Log.v("TEST", "print error : $e")
        }
    }

    private fun printSlip(curSheetNo: Int) {
        if (curSheetNo > 1) {
            return
        }

        vectorPrinter!!.startPrint(object :
            com.usdk.apiservice.aidl.vectorprinter.OnPrintListener.Stub() {
            @Throws(RemoteException::class)
            override fun onFinish() {

            }

            @Throws(RemoteException::class)
            override fun onStart() {

            }

            @Throws(RemoteException::class)
            override fun onError(error: Int, errorMsg: String) {

            }
        })

    }

    private fun readAssetsFile(ctx: Context, fileName: String): ByteArray? {
        var input: InputStream? = null
        return try {
            input = ctx.assets.open(fileName)
            val buffer = ByteArray(input.available())
            input.read(buffer)
            Log.d("Buffer", buffer.toString())
            buffer
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } finally {
            if (input != null) {
                try {
                    input.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun printBitmap(
        data: ArrayList<String>,
        type: String,
        host: String,
    ) {
        if (getDataSetting()?.enable_print_test == true) {
            ISO8583Extracting().getISO8583Extract(data).let {
                val json = JSONObject(it)
                val jsonArray = json.getJSONArray("data")
                Log.d("Extract", it)
                printer.setPrintFormat(
                    PrintFormat.FORMAT_MOREDATAPROC,
                    PrintFormat.VALUE_MOREDATAPROC_PRNTOEND
                );
                val time = Utils().getDateTime(System.currentTimeMillis().toString())
                printer.addText(AlignMode.LEFT, "[Time] $time");
                printer.addText(AlignMode.LEFT, "[$type]");
                printer.addText(AlignMode.LEFT, "[Host] $host");
                printer.addText(AlignMode.LEFT, "[TPDU] " + json.getString("tpdu"));
                printer.addText(AlignMode.LEFT, "[Message Type] " + json.getString("messageType"));
                printer.addText(AlignMode.LEFT, "[Bitmap] " + json.getString("bitmap"));

                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val bit = if (jsonObject.getString("id").length == 1) {
                        "0" + jsonObject.getString("id")
                    } else {
                        jsonObject.getString("id")
                    }
                    printer.addText(
                        AlignMode.LEFT,
                        "[DE" + bit + "] " + jsonObject.getString("originalData")
                    );
                    Log.d("Bit", "[DE" + bit + "] " + jsonObject.getString("originalData"))
                }
            }

            printer.feedLine(5)

            printer.startPrint(object : OnPrintListener.Stub() {
                @Throws(RemoteException::class)
                override fun onFinish() {
                    Log.d("Printing", "Finish")
                }

                @Throws(RemoteException::class)
                override fun onError(error: Int) {
                }
            })

            delay(1500)
        }
    }
}

//printer.setAscScale(ASCScale.SC1x1)
//printer.setAscSize(ASCSize.DOT5x7)
//printer.addText(AlignMode.CENTER, "SC1x1 DOT5x7")
//
//printer.setAscScale(ASCScale.SC1x2)
//printer.setAscSize(ASCSize.DOT5x7)
//printer.addText(AlignMode.CENTER, "SC1x2 DOT5x7")
//
//printer.setAscScale(ASCScale.SC1x3)
//printer.setAscSize(ASCSize.DOT5x7)
//printer.addText(AlignMode.CENTER, "SC1x3 DOT5x7")
//
//printer.setAscScale(ASCScale.SC2x1)
//printer.setAscSize(ASCSize.DOT5x7)
//printer.addText(AlignMode.CENTER, "SC2x1 DOT5x7")
//
//printer.setAscScale(ASCScale.SC2x2)
//printer.setAscSize(ASCSize.DOT5x7)
//printer.addText(AlignMode.CENTER, "SC2x2 DOT5x7")
//
//printer.setAscScale(ASCScale.SC2x3)
//printer.setAscSize(ASCSize.DOT5x7)
//printer.addText(AlignMode.CENTER, "SC2x3 DOT5x7")
//
//printer.setAscScale(ASCScale.SC3x1)
//printer.setAscSize(ASCSize.DOT5x7)
//printer.addText(AlignMode.CENTER, "SC3x1 DOT5x7")
//
//printer.setAscScale(ASCScale.SC3x2)
//printer.setAscSize(ASCSize.DOT5x7)
//printer.addText(AlignMode.CENTER, "SC3x2 DOT5x7")
//
//printer.setAscScale(ASCScale.SC3x3)
//printer.setAscSize(ASCSize.DOT5x7)
//printer.addText(AlignMode.CENTER, "SC3x3 DOT5x7")
////  --------------------------------
//
//printer.setAscScale(ASCScale.SC1x1)
//printer.setAscSize(ASCSize.DOT7x7)
//printer.addText(AlignMode.CENTER, "SC1x1 DOT7x7")
//
//printer.setAscScale(ASCScale.SC1x2)
//printer.setAscSize(ASCSize.DOT7x7)
//printer.addText(AlignMode.CENTER, "SC1x2 DOT7x7")
//
//printer.setAscScale(ASCScale.SC1x3)
//printer.setAscSize(ASCSize.DOT7x7)
//printer.addText(AlignMode.CENTER, "SC1x3 DOT7x7")
//
//printer.setAscScale(ASCScale.SC2x1)
//printer.setAscSize(ASCSize.DOT7x7)
//printer.addText(AlignMode.CENTER, "SC2x1 DOT7x7")
//
//printer.setAscScale(ASCScale.SC2x2)
//printer.setAscSize(ASCSize.DOT7x7)
//printer.addText(AlignMode.CENTER, "SC2x2 DOT7x7")
//
//printer.setAscScale(ASCScale.SC2x3)
//printer.setAscSize(ASCSize.DOT7x7)
//printer.addText(AlignMode.CENTER, "SC2x3 DOT7x7")
//
//printer.setAscScale(ASCScale.SC3x1)
//printer.setAscSize(ASCSize.DOT7x7)
//printer.addText(AlignMode.CENTER, "SC3x1 DOT7x7")
//
//printer.setAscScale(ASCScale.SC3x2)
//printer.setAscSize(ASCSize.DOT7x7)
//printer.addText(AlignMode.CENTER, "SC3x2 DOT7x7")
//
//printer.setAscScale(ASCScale.SC3x3)
//printer.setAscSize(ASCSize.DOT7x7)
//printer.addText(AlignMode.CENTER, "SC3x3 DOT7x7")
//
////  --------------------------------
//
//printer.setAscScale(ASCScale.SC1x1)
//printer.setAscSize(ASCSize.DOT16x8)
//printer.addText(AlignMode.CENTER, "SC1x1 DOT16x8")
//
//printer.setAscScale(ASCScale.SC1x2)
//printer.setAscSize(ASCSize.DOT16x8)
//printer.addText(AlignMode.CENTER, "SC1x2 DOT16x8")
//
//printer.setAscScale(ASCScale.SC1x3)
//printer.setAscSize(ASCSize.DOT16x8)
//printer.addText(AlignMode.CENTER, "SC1x3 DOT16x8")
//
//printer.setAscScale(ASCScale.SC2x1)
//printer.setAscSize(ASCSize.DOT16x8)
//printer.addText(AlignMode.CENTER, "SC2x1 DOT16x8")
//
//printer.setAscScale(ASCScale.SC2x2)
//printer.setAscSize(ASCSize.DOT16x8)
//printer.addText(AlignMode.CENTER, "SC2x2 DOT16x8")
//
//printer.setAscScale(ASCScale.SC2x3)
//printer.setAscSize(ASCSize.DOT16x8)
//printer.addText(AlignMode.CENTER, "SC2x3 DOT16x8")
//
//printer.setAscScale(ASCScale.SC3x1)
//printer.setAscSize(ASCSize.DOT16x8)
//printer.addText(AlignMode.CENTER, "SC3x1 DOT16x8")
//
//printer.setAscScale(ASCScale.SC3x2)
//printer.setAscSize(ASCSize.DOT16x8)
//printer.addText(AlignMode.CENTER, "SC3x2 DOT16x8")
//
//printer.setAscScale(ASCScale.SC3x3)
//printer.setAscSize(ASCSize.DOT16x8)
//printer.addText(AlignMode.CENTER, "SC3x3 DOT16x8")
//
////  --------------------------------
//
//printer.setAscScale(ASCScale.SC1x1)
//printer.setAscSize(ASCSize.DOT24x8)
//printer.addText(AlignMode.CENTER, "SC1x1 DOT24x8")
//
//printer.setAscScale(ASCScale.SC1x2)
//printer.setAscSize(ASCSize.DOT24x8)
//printer.addText(AlignMode.CENTER, "SC1x2 DOT24x8")
//
//printer.setAscScale(ASCScale.SC1x3)
//printer.setAscSize(ASCSize.DOT24x8)
//printer.addText(AlignMode.CENTER, "SC1x3 DOT24x8")
//
//printer.setAscScale(ASCScale.SC2x1)
//printer.setAscSize(ASCSize.DOT24x8)
//printer.addText(AlignMode.CENTER, "SC2x1 DOT24x8")
//
//printer.setAscScale(ASCScale.SC2x2)
//printer.setAscSize(ASCSize.DOT24x8)
//printer.addText(AlignMode.CENTER, "SC2x2 DOT24x8")
//
//printer.setAscScale(ASCScale.SC2x3)
//printer.setAscSize(ASCSize.DOT24x8)
//printer.addText(AlignMode.CENTER, "SC2x3 DOT24x8")
//
//printer.setAscScale(ASCScale.SC3x1)
//printer.setAscSize(ASCSize.DOT24x8)
//printer.addText(AlignMode.CENTER, "SC3x1 DOT24x8")
//
//printer.setAscScale(ASCScale.SC3x2)
//printer.setAscSize(ASCSize.DOT24x8)
//printer.addText(AlignMode.CENTER, "SC3x2 DOT24x8")
//
//printer.setAscScale(ASCScale.SC3x3)
//printer.setAscSize(ASCSize.DOT24x8)
//printer.addText(AlignMode.CENTER, "SC3x3 DOT24x8")
//
////  --------------------------------
//
//printer.setAscScale(ASCScale.SC1x1)
//printer.setAscSize(ASCSize.DOT24x12)
//printer.addText(AlignMode.CENTER, "SC1x1 DOT24x12")
//
//printer.setAscScale(ASCScale.SC1x2)
//printer.setAscSize(ASCSize.DOT24x12)
//printer.addText(AlignMode.CENTER, "SC1x2 DOT24x12")
//
//printer.setAscScale(ASCScale.SC1x3)
//printer.setAscSize(ASCSize.DOT24x12)
//printer.addText(AlignMode.CENTER, "SC1x3 DOT24x12")
//
//printer.setAscScale(ASCScale.SC2x1)
//printer.setAscSize(ASCSize.DOT24x12)
//printer.addText(AlignMode.CENTER, "SC2x1 DOT24x12")
//
//printer.setAscScale(ASCScale.SC2x2)
//printer.setAscSize(ASCSize.DOT24x12)
//printer.addText(AlignMode.CENTER, "SC2x2 DOT24x12")
//
//printer.setAscScale(ASCScale.SC2x3)
//printer.setAscSize(ASCSize.DOT24x12)
//printer.addText(AlignMode.CENTER, "SC2x3 DOT24x12")
//
//printer.setAscScale(ASCScale.SC3x1)
//printer.setAscSize(ASCSize.DOT24x12)
//printer.addText(AlignMode.CENTER, "SC3x1 DOT24x12")
//
//printer.setAscScale(ASCScale.SC3x2)
//printer.setAscSize(ASCSize.DOT24x12)
//printer.addText(AlignMode.CENTER, "SC3x2 DOT24x12")
//
//printer.setAscScale(ASCScale.SC3x3)
//printer.setAscSize(ASCSize.DOT24x12)
//printer.addText(AlignMode.CENTER, "SC3x3 DOT24x12")
//
////  --------------------------------
//
//printer.setAscScale(ASCScale.SC1x1)
//printer.setAscSize(ASCSize.DOT32x12)
//printer.addText(AlignMode.CENTER, "SC1x1 DOT32x12")
//
//printer.setAscScale(ASCScale.SC1x2)
//printer.setAscSize(ASCSize.DOT32x12)
//printer.addText(AlignMode.CENTER, "SC1x2 DOT32x12")
//
//printer.setAscScale(ASCScale.SC1x3)
//printer.setAscSize(ASCSize.DOT32x12)
//printer.addText(AlignMode.CENTER, "SC1x3 DOT32x12")
//
//printer.setAscScale(ASCScale.SC2x1)
//printer.setAscSize(ASCSize.DOT32x12)
//printer.addText(AlignMode.CENTER, "SC2x1 DOT32x12")
//
//printer.setAscScale(ASCScale.SC2x2)
//printer.setAscSize(ASCSize.DOT32x12)
//printer.addText(AlignMode.CENTER, "SC2x2 DOT32x12")
//
//printer.setAscScale(ASCScale.SC2x3)
//printer.setAscSize(ASCSize.DOT32x12)
//printer.addText(AlignMode.CENTER, "SC2x3 DOT32x12")
//
//printer.setAscScale(ASCScale.SC3x1)
//printer.setAscSize(ASCSize.DOT32x12)
//printer.addText(AlignMode.CENTER, "SC3x1 DOT32x12")
//
//printer.setAscScale(ASCScale.SC3x2)
//printer.setAscSize(ASCSize.DOT32x12)
//printer.addText(AlignMode.CENTER, "SC3x2 DOT32x12")
//
//printer.setAscScale(ASCScale.SC3x3)
//printer.setAscSize(ASCSize.DOT32x12)
//printer.addText(AlignMode.CENTER, "SC3x3 DOT32x12")