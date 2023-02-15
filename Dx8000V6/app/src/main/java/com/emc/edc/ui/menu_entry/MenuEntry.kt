package com.emc.edc.ui.menu_entry


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.emc.edc.globaldata.data.MenuEntryRoute
import com.emc.edc.ui.utils.TheButton
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalFoundationApi
@Composable
fun MenuEntry(navController: NavController) {
    val data = MenuEntryRoute.routeList
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            modifier = Modifier.padding(start = 15.dp, top = 15.dp),
            text = "Menu Entry",
            fontSize = 25.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colors.onSurface
        )
        Spacer(modifier = Modifier.height(5.dp))
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(5.dp)
            ) {
                items(data.size) { item ->
                    TheButton(data[item], navController)
                }
            }
        }
    }
}

@DelicateCoroutinesApi
@ExperimentalFoundationApi
@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalAnimationApi
@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    val navController = rememberAnimatedNavController()
    MenuEntry(navController)
}