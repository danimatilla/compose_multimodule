package com.example.seed.navigation.screens

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.example.seed.navigation.NavigationHandler
import com.example.seed.screens.dash.profile.ProfileScreen
import com.example.seed.screens.dash.profile.SettingsScreen
import kotlinx.serialization.Serializable

@Serializable
object ProfileGraph : Graph {

    @Serializable
    data object Profile : Screen {
        override val route: String
            get() = "${this@ProfileGraph.route}/profile"
    }

    @Serializable
    data object Settings : Screen {
        override val route: String
            get() = "${this@ProfileGraph.route}/settings"
    }

    override fun EntryProviderScope<NavKey>.registerEntries(
        onEvent: (NavigationHandler.NavigationEvent) -> Unit
    ) {
        entry<Profile> { ProfileScreen(onEvent) }
        entry<Settings> { SettingsScreen(onEvent) }
    }

    override val route: String
        get() = "${DashGraph.route}/profile"
}
