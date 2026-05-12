package com.dxmxp.seed.navigation.routes

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.dxmxp.ui.navigation.NavigationHandler
import com.dxmxp.seed.screens.dash.profile.ProfileScreen
import com.dxmxp.seed.screens.dash.profile.SettingsScreen
import com.dxmxp.ui.navigation.Graph
import com.dxmxp.ui.navigation.Screen
import kotlinx.serialization.Serializable

@Serializable
object ProfileGraph : Graph {

    @Serializable
    data object Settings : Screen {
        override val route: String
            get() = "${this@ProfileGraph.route}/settings"
    }

    override fun EntryProviderScope<NavKey>.registerEntries(
        onEvent: (NavigationHandler.NavigationEvent) -> Unit
    ) {
        entry<ProfileGraph> { ProfileScreen(onEvent) }
        entry<Settings> { SettingsScreen(onEvent) }
    }

    override val route: String
        get() = "${SeedGraph.route}/profile"
}
