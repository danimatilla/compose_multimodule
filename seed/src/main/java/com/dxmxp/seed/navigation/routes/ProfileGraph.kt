package com.dxmxp.seed.navigation.routes

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.dxmxp.seed.screens.profile.ProfileScreen
import com.dxmxp.seed.screens.profile.SettingsScreen
import com.dxmxp.navigation.model.Graph
import com.dxmxp.navigation.model.Screen
import com.dxmxp.navigation.model.Screen.Companion.screenEntry
import kotlinx.serialization.Serializable

@Serializable
data object ProfileGraph : Graph {

    override val route: String get() = "/profile"

    @Serializable
    data object Settings : Screen {
        override val route: String get() = "/profile/settings"
    }

    override fun EntryProviderScope<NavKey>.registerScreens() {
        screenEntry<ProfileGraph> { ProfileScreen() }
        screenEntry<Settings> { SettingsScreen() }
    }
}

fun EntryProviderScope<NavKey>.profileGraph() {
    with(ProfileGraph) { registerScreens() }
}
