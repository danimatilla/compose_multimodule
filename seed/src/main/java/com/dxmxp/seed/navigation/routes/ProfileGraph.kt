package com.dxmxp.seed.navigation.routes

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.dxmxp.seed.screens.profile.ProfileScreen
import com.dxmxp.seed.screens.profile.SettingsScreen
import com.dxmxp.ui.navigation.Graph
import com.dxmxp.ui.navigation.Screen
import com.dxmxp.ui.navigation.Screen.Companion.screenEntry
import com.dxmxp.ui.navigation.helpers.NavigationHandler
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

    @Serializable
    data object Settings : Screen {
        override val route: String
            get() = "${this@ProfileGraph.route}/settings"
    }

    override fun EntryProviderScope<NavKey>.registerEntries() {
        screenEntry<ProfileGraph> { ProfileScreen() }
        screenEntry<Settings> { SettingsScreen() }
    }

    override val route: String
        get() = "${MainScaffoldGraph.route}/profile"

    @Provides
    @IntoSet
    override fun provideGraph(): Graph = ProfileGraph
}
