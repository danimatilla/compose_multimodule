package com.dxmxp.seed.navigation.routes

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.dxmxp.seed.screens.profile.ProfileScreen
import com.dxmxp.seed.screens.profile.SettingsScreen
import com.dxmxp.ui.navigation.model.Graph
import com.dxmxp.ui.navigation.model.Route
import com.dxmxp.ui.navigation.model.Screen
import com.dxmxp.ui.navigation.model.Screen.Companion.screenEntry
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import kotlinx.serialization.Serializable

@Serializable
@Module
@InstallIn(SingletonComponent::class)
object ProfileGraph : Graph {

    override val screens: List<Class<out Route>>
        get() = listOf(
            Settings::class.java
        )

    @Serializable
    data object Settings : Screen

    override fun EntryProviderScope<NavKey>.registerEntries() {
        screenEntry<ProfileGraph> { ProfileScreen() }
        screenEntry<Settings> { SettingsScreen() }
    }

    override val route: String
        get() = "/profile"

    @Provides
    @IntoSet
    override fun provideGraph(): Graph = ProfileGraph
}
