package com.dxmxp.seed.navigation.routes

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.dxmxp.seed.screens.HomeScreen
import com.dxmxp.seed.screens.MenuScreen
import com.dxmxp.seed.screens.SearchScreen
import com.dxmxp.seed.screens.beers.BeersScreen
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
object MainScaffoldGraph : Graph {

    @Serializable
    data object Search : Screen {
        override val route: String
            get() = "${this@MainScaffoldGraph.route}/search"
    }

    @Serializable
    data object Menu : Screen {
        override val route: String
            get() = "${this@MainScaffoldGraph.route}/menu"
    }

    @Serializable
    data object Beers : Screen {
        override val route: String
            get() = "${this@MainScaffoldGraph.route}/beers"
    }

    override fun EntryProviderScope<NavKey>.registerEntries(
        onEvent: (NavigationHandler.NavigationEvent) -> Unit
    ) {
        screenEntry<MainScaffoldGraph> { HomeScreen(onEvent) }
        screenEntry<Search> { SearchScreen(onEvent) }
        screenEntry<Menu> { MenuScreen(onEvent) }
        screenEntry<Beers> { BeersScreen(onEvent) }

        // Integrate the ProfileGraph entries into this graph.
        ProfileGraph.run { registerEntries(onEvent) }
    }

    override val route: String
        get() = "/seed"

    @Provides
    @IntoSet
    override fun provideGraph(): Graph = MainScaffoldGraph
}
