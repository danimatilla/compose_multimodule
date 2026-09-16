package com.dxmxp.seed.navigation.di

import com.dxmxp.navigation.model.Graph
import com.dxmxp.seed.navigation.routes.AuthGraph
import com.dxmxp.seed.navigation.routes.MainGraph
import com.dxmxp.seed.navigation.routes.ProfileGraph
import com.dxmxp.stories.navigation.routes.StoriesGraph
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

/**
 * Central module for declaring all graphs in the application.
 *
 * This is the single point of registration for all navigation graphs.
 * Instead of having multiple distributed modules (one per feature),
 * this centralizes the declaration for better visibility and control.
 *
 * Architecture:
 * ```
 * AppGraphsModule (this file)
 *     └── Provides: Set<Graph>
 *         ├── MainGraph (/seed)
 *         ├── AuthGraph (/auth)
 *         ├── ProfileGraph (/profile)
 *         └── StoriesGraph (/stories)
 *
 * RouteRegistry (initialization)
 *     ├── Reads staticRoutes() from each Graph
 *     ├── Reads dynamicRoutePatterns() from each Graph
 *     └── Builds internal lookup maps
 *
 * At Runtime:
 *     └── Navigation or deep links use RouteRegistry to resolve routes
 * ```
 *
 * Benefits:
 * - Single source of truth for all graphs
 * - Easy to see all available navigation contexts
 * - No duplicate @IntoSet declarations across modules
 * - Centralized place to manage graph initialization order if needed
 *
 * Note: This could be split per-feature if the app grows significantly,
 * but for typical apps, centralization is clearer.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppGraphsModule {

    /**
     * Provides the main app graph.
     * Root navigation context for the main authenticated flow.
     */
    @Provides
    @IntoSet
    fun provideMainGraph(): Graph = MainGraph

    /**
     * Provides the authentication graph.
     * Navigation context for login/registration flow.
     * Typically shown before auth is acquired.
     */
    @Provides
    @IntoSet
    fun provideAuthGraph(): Graph = AuthGraph

    /**
     * Provides the profile graph.
     * Navigation context for user profile and settings screens.
     */
    @Provides
    @IntoSet
    fun provideProfileGraph(): Graph = ProfileGraph

    /**
     * Provides the stories graph (feature module).
     * Navigation context for story feed, details, and interactions.
     * Demonstrates how feature modules contribute graphs to the central module.
     */
    @Provides
    @IntoSet
    fun provideStoriesGraph(): Graph = StoriesGraph
}
