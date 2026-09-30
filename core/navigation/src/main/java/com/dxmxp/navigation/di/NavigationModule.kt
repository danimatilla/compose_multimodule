package com.dxmxp.navigation.di

import com.dxmxp.navigation.model.Graph

/**
 * Generic contract for supplying the set of graphs to the navigation core.
 *
 * The core library should not know whether the app uses Hilt, Koin, manual wiring,
 * or any other dependency injection mechanism. It only needs a collection of [Graph].
 */
fun interface NavigationModule {
    fun graphs(): Set<Graph>
}
