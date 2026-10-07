package com.dxmxp.seed.navigation.di

import com.dxmxp.navigation.core.RouteRegistry
import com.dxmxp.navigation.di.NavigationModule
import com.dxmxp.navigation.model.Graph
import com.dxmxp.navigation.utils.DeepLinkRouter
import com.dxmxp.seed.navigation.routes.AuthGraph
import com.dxmxp.seed.navigation.routes.MainGraph
import com.dxmxp.seed.navigation.routes.ProfileGraph
import com.dxmxp.stories.navigation.routes.StoriesGraph
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Application layer wiring for navigation.
 *
 * The core library depends only on [NavigationModule] / [Set<Graph>].
 * This app module chooses Hilt as the concrete injector.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppNavigationModule : NavigationModule {

    override fun graphs(): Set<Graph> = setOf(
        MainGraph,
        AuthGraph,
        ProfileGraph,
        StoriesGraph.Default,
    )

    @Provides
    @Singleton
    fun provideRouteRegistry(): RouteRegistry = RouteRegistry(graphs())

    @Provides
    @Singleton
    fun provideDeepLinkRouter(routeRegistry: RouteRegistry): DeepLinkRouter =
        DeepLinkRouter(routeRegistry)
}
