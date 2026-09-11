package com.dxmxp.seed.navigation.routes

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.dxmxp.seed.navigation.MainScaffold
import com.dxmxp.seed.screens.HomeScreen
import com.dxmxp.seed.screens.MenuScreen
import com.dxmxp.seed.screens.SearchScreen
import com.dxmxp.seed.screens.profile.ProfileScreen
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
object MainScaffoldGraph : Graph {

    override val route: String
        get() = "/seed"

    override val screens: List<Class<out Route>>
        get() = listOf(
            Home::class.java,
            Search::class.java,
            Menu::class.java,
            ProfileGraph::class.java
        )

    @Serializable
    data object Home : Screen

    @Serializable
    data object Search : Screen

    @Serializable
    data object Menu : Screen

    override fun EntryProviderScope<NavKey>.registerEntries() {
        screenEntry<MainScaffoldGraph> { MainScaffold() }
        screenEntry<Home> { HomeScreen() }
        screenEntry<Search> { SearchScreen() }
        screenEntry<Menu> { MenuScreen() }
        screenEntry<ProfileGraph> { ProfileScreen() }
    }

    @Provides
    @IntoSet
    override fun provideGraph(): Graph = MainScaffoldGraph
}
