package com.emc.edc.globaldata.dataclass

data class MockTransaction(
    val type: String,
    val topic: String,
    val detail: String,
    val amount: String
)
