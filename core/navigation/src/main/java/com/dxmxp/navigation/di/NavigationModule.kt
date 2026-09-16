package com.dxmxp.navigation.di

import com.dxmxp.navigation.model.Graph
import dagger.Module
import dagger.multibindings.Multibinds
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Base Hilt module that defines the multibinding set for all graphs.
 *
 * This module provides the injection point for [Graph] instances across the app.
 * Individual feature modules or app-level modules contribute to this set via @IntoSet.
 *
 * The set is consumed by [RouteRegistry][com.dxmxp.navigation.core.RouteRegistry]
 * to initialize route mappings and deep link patterns.
 *
 * Usage:
 * ```
 * @Module
 * @InstallIn(SingletonComponent::class)
 * object AppGraphsModule {
 *     @Provides
 *     @IntoSet
 *     fun provideMainGraph(): Graph = MainGraph
 *
 *     @Provides
 *     @IntoSet
 *     fun provideStoriesGraph(): Graph = StoriesGraph
 * }
 * ```
 */
@Module
@InstallIn(SingletonComponent::class)
fun interface NavigationModule {

    /**
     * Declares the multibinding set for graphs.
     * Empty by default; populated by feature modules via @IntoSet contributions.
     */
    @Multibinds
    fun graphs(): Set<Graph>
}
