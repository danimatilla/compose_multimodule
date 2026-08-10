package com.dxmxp.ui.navigation.model

import androidx.navigation3.runtime.NavKey

interface Route : NavKey {
    val route: String
    val showMainBottomBar: Boolean
}
