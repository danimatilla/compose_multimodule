package com.example.seed.navigation.screens

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.example.seed.navigation.NavigationHandler
import com.example.seed.screens.profile.AccountScreen
import com.example.seed.screens.profile.ProfileScreen
import com.example.seed.screens.profile.SettingsScreen
import kotlinx.serialization.Serializable

@Serializable
object ProfileGraph : Graph {

    @Serializable
    data object Home : Screen

    @Serializable
    data object Settings : Screen

    @Serializable
    data object Account : Screen

    override fun EntryProviderScope<NavKey>.registerEntries(
        onEvent: (NavigationHandler.NavigationEvent) -> Unit
    ) {
        entry<Home> { ProfileScreen(onEvent) }
        entry<Settings> { SettingsScreen(onEvent) }
        entry<Account> { AccountScreen(onEvent) }
    }
}
