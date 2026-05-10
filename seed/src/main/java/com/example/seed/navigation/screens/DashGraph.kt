package com.example.seed.navigation.screens

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.example.seed.navigation.NavigationHandler
import com.example.seed.screens.HomeScreen
import com.example.seed.screens.profile.ProfileScreen
import kotlinx.serialization.Serializable

@Serializable
object DashGraph : Graph {

    @Serializable
    data object Home : Screen

    @Serializable
    data object Search : Screen

    override fun EntryProviderScope<NavKey>.registerEntries(
        onEvent: (NavigationHandler.NavigationEvent) -> Unit
    ) {
        entry<Home> { HomeScreen(onEvent) }
        entry<Search> { /* TODO: SearchScreen(onEvent) */ }

        // Nested graph
        ProfileGraph.run { registerEntries(onEvent) }
        entry<ProfileGraph> { ProfileScreen(onEvent) }
    }
}