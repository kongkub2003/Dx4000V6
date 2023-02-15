package com.emc.edc.globaldata.dataclass

import com.google.gson.annotations.SerializedName

data class ISO8583(
    val bit: Int,
    val length: Int,
    val type: String,
    val name: String,
    val key: String,
    val format: String
)

data class ISO8583Map(
    @SerializedName("tpdu")
    val tpdu: String,
    @SerializedName("messageType")
    val messageType: String,
    @SerializedName("bitmap")
    val bitmap: String,
    @SerializedName("data")
    val data: ArrayList<ISO8583BitMap>
)

data class ISO8583BitMap(
    @SerializedName("id")
    val id: Int,
    @SerializedName("originalData")
    val originalData: String,
    @SerializedName("transformData")
    val transformData: String,
    @SerializedName("key")
    val key: String,
)
