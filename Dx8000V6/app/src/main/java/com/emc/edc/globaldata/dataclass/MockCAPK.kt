package com.emc.edc.globaldata.dataclass

data class MockCAPK(
    val rid: String,
    val index: String,
    val mod: String,
    val exponent: String,
    val issuer: String,
    val keyLength: Int,
    val sha1: String,
    val exp: String
)
