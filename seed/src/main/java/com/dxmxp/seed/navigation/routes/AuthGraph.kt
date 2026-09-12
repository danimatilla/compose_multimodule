package com.dxmxp.seed.navigation.routes

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.dxmxp.seed.screens.login.LoginScreen
import com.dxmxp.navigation.model.Graph
import com.dxmxp.navigation.model.Screen
import com.dxmxp.navigation.model.Screen.Companion.screenEntry
import kotlinx.serialization.Serializable

@Serializable
data object AuthGraph : Graph {

    override val route: String get() = "/auth"
    override val requiresAuth: Boolean get() = false

    @Serializable
    data object Login : Screen {
        override val route: String get() = "/auth/login"
        override val requiresAuth: Boolean = false
    }

    override fun EntryProviderScope<NavKey>.registerScreens() {
        screenEntry<AuthGraph> { LoginScreen() }
        screenEntry<Login> { LoginScreen() }
    }
}

fun EntryProviderScope<NavKey>.authGraph() {
    with(AuthGraph) { registerScreens() }
}
