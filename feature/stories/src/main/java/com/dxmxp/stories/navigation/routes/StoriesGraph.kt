package com.dxmxp.stories.navigation.routes

import android.net.Uri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.metadata
import com.dxmxp.domain.model.Story
import com.dxmxp.navigation.core.RouteKey
import com.dxmxp.navigation.core.RouteRegistration
import com.dxmxp.stories.navigation.StoriesScaffold
import com.dxmxp.stories.screens.feed.HomeScreen
import com.dxmxp.navigation.utils.NavigationUtils.modalAnimation
import com.dxmxp.stories.screens.story_detail.StoryDetailScreen
import com.dxmxp.stories.screens.story_detail.StoryDetailViewModel
import com.dxmxp.navigation.model.Graph
import com.dxmxp.navigation.model.Route
import com.dxmxp.navigation.model.Screen
import com.dxmxp.navigation.model.Screen.Companion.screenEntry
import kotlinx.serialization.Serializable

/**
 * StoriesGraph represents a feature module that provides the stories functionality.
 *
 * Route: /stories
 * Screens (static):
 * - Home: /stories/home (feed of stories)
 * - Profile: /stories/profile (profile info - placeholder)
 *
 * Screens (dynamic, with parameters):
 * - StoryDetail: /stories/detail?id=<id> (detail view for a specific story)
 *
 * This graph is a feature module that is registered centrally in AppGraphsModule.
 * It demonstrates both static and dynamic route patterns.
 *
 * Features:
 * - Modal presentation: isModal = true (presented as overlay/dialog)
 * - Dynamic deep linking: Resolves story IDs from query parameters
 * - ViewModel injection: StoryDetail screen receives a typed ViewModel
 *
 * Type-safe navigation (static):
 * ```
 * navigator.push(StoriesGraph)
 * navigator.push(StoriesGraph.Home)
 * navigator.push(StoriesGraph.Profile)
 * ```
 *
 * Type-safe navigation (dynamic):
 * ```
 * navigator.push(StoriesGraph.StoryDetail(id = "story_123"))
 * ```
 *
 * Deep link resolution:
 * - app://stories → StoriesGraph
 * - app://stories/home → StoriesGraph.Home
 * - app://stories/profile → StoriesGraph.Profile
 * - app://stories/detail?id=story_123 → StoriesGraph.StoryDetail(id="story_123")
 */
@Serializable
data object StoriesGraph : Graph {

    override val isModal: Boolean get() = true
    override val route: String get() = "/stories"

    @Serializable
    data object Home : Screen {
        override val route: String get() = "${StoriesGraph.route}/home"
    }

    @Serializable
    data object Profile : Screen {
        override val route: String get() = "${StoriesGraph.route}/profile"
    }

    /**
     * Parameterized route for viewing story details.
     * Contains the story ID and optional story data loaded from cache/navigation store.
     *
     * @param id The unique story identifier (required, from deep link or navigation)
     * @param story Optional pre-loaded story data (from NavigationStore to avoid TransactionTooLarge)
     */
    @Serializable
    data class StoryDetail(val id: String, val story: Story? = null) : Screen {
        override val route: String get() = "${StoriesGraph.route}/detail"
    }

    /**
     * Declare static routes (parameter-less screens) in the route registry.
     * StoryDetail is NOT included because it's a parameterized route.
     * See [dynamicRoutePatterns] for how StoryDetail is resolved from deep links.
     */
    override fun staticRoutes(): Set<RouteRegistration> = setOf(
        RouteRegistration(RouteKey.of(route), this),
        RouteRegistration(RouteKey.of(Home.route), Home),
        RouteRegistration(RouteKey.of(Profile.route), Profile),
    )

    /**
     * Declare dynamic route patterns for deep linking.
     *
     * When a deep link like `app://stories/detail?id=123` arrives:
     * 1. RouteRegistry normalizes the path: `/stories/detail`
     * 2. Matches against patterns in this map
     * 3. Invokes the converter function with the URI
     * 4. Extracts the `id` query parameter and creates StoryDetail(id="123")
     *
     * The story data is typically NOT passed through navigation because:
     * - It can be large and cause TransactionTooLargeException
     * - Use NavigationStore.pushData() instead to store large objects
     * - The ViewModel will fetch data from the repository using the id
     */
    override fun dynamicRoutePatterns(): Map<String, (Uri) -> Route?> = mapOf(
        "/stories/detail" to { uri ->
            val id = uri.getQueryParameter("id")
            if (id != null) StoryDetail(id = id) else null
        }
    )

    /**
     * Register all composable screens for this graph.
     * The ViewModel injection is handled by Screen.screenEntry() helper.
     */
    override fun EntryProviderScope<NavKey>.registerScreens() {
        screenEntry<StoriesGraph>(
            metadata = metadata { modalAnimation() }
        ) { StoriesScaffold() }
        screenEntry<Home> { HomeScreen() }
        screenEntry<Profile> { /* ProfileScreen() */ }
        screenEntry<StoryDetail, StoryDetailViewModel>(
            viewModelProvide = { hiltViewModel<StoryDetailViewModel>() },
        ) { viewModel -> StoryDetailScreen(viewModel) }
    }
}
