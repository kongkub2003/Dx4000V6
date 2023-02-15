package com.emc.edc.utils

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.io.DataOutputStream
import java.io.InputStream
import java.net.InetSocketAddress
import java.net.Socket

class SocketCommunication(
    private val address: String,
    private val port: Int,
    private val timeout: Int,
) {
    private val socket = Socket()
    private val utils = Utils()

    fun run(): Boolean  {
        return try {
            socket.connect(InetSocketAddress(address, port), timeout);
            socket.isConnected
        } catch(err : Exception) {
            Log.d("Execption", err.message.toString())
            socket.isConnected
        }
    }

    fun write(message: String): Boolean  {
        return try {
            val output = DataOutputStream(socket.getOutputStream())
            output.write(message.decodeHex())
            true
        } catch (err: Exception) {
            Log.e("ERR", err.message.toString())
            false
        }
    }

    fun read(): ArrayList<String>  {
        socket.soTimeout = timeout
        val input = socket.getInputStream()
        val maxTime = System.currentTimeMillis() + timeout
        val isoRespList: ArrayList<String> = ArrayList()
        try {
            while (System.currentTimeMillis() < maxTime) {
                val content = input.read()

                val dataList = utils.decimalToHexString(content.toByte())
                isoRespList.add(dataList)
                if (input.available() == 0) {
                    break;
                }
            }
            socket.close()
            return isoRespList
        } catch (err: Exception) {
            socket.close()
            return isoRespList
        }
    }

    private fun String.decodeHex(): ByteArray {
        check(length % 2 == 0) { "Must have an even length" }

        return chunked(2)
            .map { it.toInt(16).toByte() }
            .toByteArray()
    }

}