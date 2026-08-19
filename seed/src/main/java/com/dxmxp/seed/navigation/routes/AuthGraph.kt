package com.dxmxp.seed.navigation.routes

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.dxmxp.seed.screens.login.LoginScreen
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
object AuthGraph : Graph {

    override val requiresAuth: Boolean
        get() = false

    override val screens: List<Class<out Route>>
        get() = listOf(
            Login::class.java
        )

    @Serializable
    data object Login : Screen {
        override val requiresAuth: Boolean = false
    }

    override fun EntryProviderScope<NavKey>.registerEntries() {
        screenEntry<AuthGraph> { LoginScreen() }
    }

    override val route: String
        get() = "/auth"

    @Provides
    @IntoSet
    override fun provideGraph(): Graph = AuthGraph
}
