package com.emc.edc.globaldata.data

import com.emc.edc.R
import com.emc.edc.Route

object MenuEntryRoute {
    val routeList = listOf(
        com.emc.edc.globaldata.dataclass.Route(
            title = "Void Sale",
            txnType = "void_sale",
            image = R.drawable.ic_resource_void,
            path = Route.EnterPasswordPin.route,
            popup = false,
            group = "menu_entry"
        )
    )
}