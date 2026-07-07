package com.dxmxp.seed.navigation.routes

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.dxmxp.seed.screens.login.LoginScreen
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
object AuthGraph : Graph {

    override val screens: List<Class<out Screen>>
        get() = listOf(
            Login::class.java
        )

    @Serializable
    data object Login: Screen {
        override val route: String
            get() = "${this@AuthGraph.route}/login"
    }

    override fun EntryProviderScope<NavKey>.registerEntries() {
        screenEntry<AuthGraph> { LoginScreen() }
    }

    @Provides
    @IntoSet
    override fun provideGraph(): Graph = AuthGraph

    override val route: String
        get() = "/auth"
}
