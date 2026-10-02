package com.dxmxp.seed.navigation.routes

import android.net.Uri
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.dxmxp.navigation.core.RouteKey
import com.dxmxp.navigation.core.RouteRegistration
import com.dxmxp.seed.screens.login.LoginScreen
import com.dxmxp.navigation.model.Graph
import com.dxmxp.navigation.model.Route
import com.dxmxp.navigation.model.Screen
import com.dxmxp.navigation.model.Screen.Companion.screenEntry
import kotlinx.serialization.Serializable


@Serializable
data object AuthGraph : Graph {

    override val route: String get() = AUTH_GRAPH_PATH
    override val requiresAuth: Boolean get() = false
    override val showMainBottomBar: Boolean get() = false

    @Serializable
    data object Login : Screen {
        override val route: String get() = AUTH_LOGIN_PATH
    }

    override fun staticRoutes(): Set<RouteRegistration> = setOf(
        RouteRegistration(RouteKey.of(route), this),
        RouteRegistration(RouteKey.of(Login.route), Login),
    )

    override fun dynamicRoutePatterns(): Map<String, (Uri) -> Route?> = emptyMap()

    override fun EntryProviderScope<NavKey>.registerScreens() {
        screenEntry<AuthGraph> { LoginScreen() }
        screenEntry<Login> { LoginScreen() }
    }

    private const val AUTH_GRAPH_PATH = "/auth"
    private const val AUTH_LOGIN_PATH = "$AUTH_GRAPH_PATH/login"
}
