package com.dxmxp.seed.navigation.routes

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.dxmxp.seed.screens.profile.ProfileScreen
import com.dxmxp.seed.screens.profile.SettingsScreen
import com.dxmxp.ui.navigation.Graph
import com.dxmxp.ui.navigation.Screen
import com.dxmxp.ui.navigation.Screen.Companion.screenEntry
import com.dxmxp.ui.navigation.helpers.NavigationHandler
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
        screenEntry<ProfileGraph> { ProfileScreen(onEvent) }
        screenEntry<Settings> { SettingsScreen(onEvent) }
    }

    override val route: String
        get() = "${SeedGraph.route}/profile"
}
