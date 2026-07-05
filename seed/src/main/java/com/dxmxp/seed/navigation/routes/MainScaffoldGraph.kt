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

    override val screens: List<Class<out Screen>>
        get() = listOf(
            Home::class.java,
            Search::class.java,
            Menu::class.java,
            Beers::class.java,
            ProfileGraph::class.java
        )

    override val children: List<Class<out Screen>>
        get() = super.children

    @Serializable
    data object Home : Screen {
        override val route: String
            get() = "${this@MainScaffoldGraph.route}/home"
    }

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

    override fun EntryProviderScope<NavKey>.registerEntries() {
        screenEntry<Home> { HomeScreen() }
        screenEntry<Search> { SearchScreen() }
        screenEntry<Menu> { MenuScreen() }
        screenEntry<Beers> { BeersScreen() }
    }

    override val route: String
        get() = "/seed"

    @Provides
    @IntoSet
    override fun provideGraph(): Graph = MainScaffoldGraph
}
