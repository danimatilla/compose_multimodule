package com.dxmxp.navigation.core

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.dxmxp.navigation.model.Route
import org.junit.Assert.assertEquals
import org.junit.Test

class NavigatorTest {

    private data class TestRoute(override val route: String) : Route

    @Test
    fun `updateStack DSL updates backstack atomically`() {
        val backStack = NavBackStack<NavKey>(TestRoute("root"))
        val navigator = Navigator(backStack)

        navigator.updateStack {
            root(TestRoute("main"))
            push(TestRoute("stories"))
            push(TestRoute("detail"))
        }

        assertEquals(3, backStack.size)
        assertEquals(TestRoute("main"), backStack[0])
        assertEquals(TestRoute("stories"), backStack[1])
        assertEquals(TestRoute("detail"), backStack[2])
        assertEquals(TestRoute("detail"), navigator.currentDestination)
    }

    @Test
    fun `multiple push and root in updateStack updates backstack correctly`() {
        val backStack = NavBackStack<NavKey>(TestRoute("root"))
        val navigator = Navigator(backStack)

        navigator.updateStack {
            root(TestRoute("home"))
            push(TestRoute("profile"))
        }

        assertEquals(2, backStack.size)
        assertEquals(TestRoute("home"), backStack[0])
        assertEquals(TestRoute("profile"), backStack[1])
    }

    @Test
    fun `pop inside updateStack removes top element`() {
        val backStack = NavBackStack<NavKey>(TestRoute("root"))
        val navigator = Navigator(backStack)

        navigator.push(TestRoute("screen1"))
        navigator.push(TestRoute("screen2"))

        navigator.updateStack {
            pop()
        }

        assertEquals(2, backStack.size)
        assertEquals(TestRoute("screen1"), navigator.currentDestination)
    }
}
