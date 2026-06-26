package com.dxmxp.seed.navigation.routes

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.dxmxp.seed.screens.login.LoginScreen
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
object AuthGraph : Graph {

    @Serializable
    data object Login : Screen {
        override val route: String
            get() = "${this@AuthGraph.route}/login"
        
        override val showMainBottomBar: Boolean
            get() = false
    }

    override fun EntryProviderScope<NavKey>.registerEntries(
        onEvent: (NavigationHandler.NavigationEvent) -> Unit
    ) {
        screenEntry<AuthGraph> { LoginScreen(onEvent) }
        screenEntry<Login> { LoginScreen(onEvent) }
    }

    override val route: String
        get() = "/auth"

    override val showMainBottomBar: Boolean
        get() = false

    @Provides
    @IntoSet
    override fun provideGraph(): Graph = AuthGraph
}
