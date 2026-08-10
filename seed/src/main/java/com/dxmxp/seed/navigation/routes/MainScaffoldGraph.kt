package com.dxmxp.seed.navigation.routes

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.dxmxp.seed.navigation.MainScaffold
import com.dxmxp.seed.screens.HomeScreen
import com.dxmxp.seed.screens.MenuScreen
import com.dxmxp.seed.screens.SearchScreen
import com.dxmxp.ui.navigation.model.Graph
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

    override val screens: List<Class<out Screen>>
        get() = listOf(
            Home::class.java,
            CatalogGraph::class.java,
            Search::class.java,
            Menu::class.java,
            ProfileGraph::class.java
        )

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

    override fun EntryProviderScope<NavKey>.registerEntries() {
        screenEntry<MainScaffoldGraph> { MainScaffold() }
        screenEntry<Home> { HomeScreen() }
        screenEntry<Search> { SearchScreen() }
        screenEntry<Menu> { MenuScreen() }
    }

    override val route: String
        get() = "/seed"

    @Provides
    @IntoSet
    override fun provideGraph(): Graph = MainScaffoldGraph
}
