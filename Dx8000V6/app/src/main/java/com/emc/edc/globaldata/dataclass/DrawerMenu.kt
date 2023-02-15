package com.emc.edc.globaldata.dataclass

import androidx.compose.ui.graphics.vector.ImageVector

data class DrawerMenu(
    val id : String,
    val title: String,
    val icon: ImageVector,
    val path: String,
    val popup: Boolean
)
