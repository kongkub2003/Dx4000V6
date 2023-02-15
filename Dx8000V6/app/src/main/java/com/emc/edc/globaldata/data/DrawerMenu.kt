package com.emc.edc.globaldata.data
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import com.emc.edc.Route
import com.emc.edc.globaldata.dataclass.DrawerMenu


object DrawerMenu {
    val list = listOf(
        DrawerMenu(
            id = "log_on",
            title = "Log on",
            popup = true,
            icon = Icons.Filled.LocationOn,
            path = ""
        ),
        DrawerMenu(
            id = "log_out",
            title = "Log Out",
            popup = true,
            icon = Icons.Filled.ExitToApp,
            path = ""
        ),
        DrawerMenu(
            id = "settings",
            title = "Settings",
            popup = false,
            icon = Icons.Filled.Settings,
            path = Route.Setting.route
        ),
        DrawerMenu(
            id = "version",
            title = "Version",
            popup = false,
            icon = Icons.Filled.Info,
            path = "version"
        ),
        DrawerMenu(
            id = "administrator",
            title = "Administrator",
            popup = false,
            icon = Icons.Filled.Info,
            path = Route.Setting.route
        ),
    )
}