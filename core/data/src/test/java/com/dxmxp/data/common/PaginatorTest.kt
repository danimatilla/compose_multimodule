package com.dxmxp.data.common

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class PaginatorTest {

    private lateinit var paginator: Paginator<Int, List<String>>

    @Before
    fun setUp() {
        // Set up a paginator that increments the key by 1
        // and ends when the result has fewer items than the pageSize
        paginator = Paginator(
            initialKey = 0,
            pageSize = 2,
            nextKeyProvider = { key, result, pageSize ->
                if (result.size < pageSize) null else key + 1
            }
        )
    }

    @Test
    fun `fetchPaged returns data and updates currentKey`() = runTest {
        // First page
        val result1 = paginator.fetchPaged { key, limit ->
            assertEquals(0, key)
            assertEquals(2, limit)
            listOf("item1", "item2")
        }
        assertEquals(listOf("item1", "item2"), result1)

        // Second page (should use key 1)
        val result2 = paginator.fetchPaged { key, _ ->
            assertEquals(1, key)
            listOf("item3")
        }
        assertEquals(listOf("item3"), result2)

        // Third page should be null because the previous result (size 1) < pageSize (2)
        val result3 = paginator.fetchPaged { _, _ ->
            listOf("item4")
        }
        assertNull(result3)
    }

    @Test
    fun `fetchPaged returns null when fetch returns null`() = runTest {
        val result = paginator.fetchPaged { _, _ -> null }
        assertNull(result)
    }

    @Test
    fun `fetchOneShot returns data and disables further pagination`() = runTest {
        val result = paginator.fetchOneShot { listOf("item1") }
        assertEquals(listOf("item1"), result)

        // Any subsequent call to fetchPaged should return null as currentKey becomes null
        val pagedResult = paginator.fetchPaged { _, _ -> listOf("item2") }
        assertNull(pagedResult)
    }

    @Test
    fun `reset restores initial state`() = runTest {
        // Exhaust the paginator
        paginator.fetchPaged { _, _ -> listOf("item1") } // Returns 1 item, key becomes null
        assertNull(paginator.fetchPaged { _, _ -> listOf("item2") })

        paginator.reset()

        // Should start again from the initial key (0)
        val result = paginator.fetchPaged { key, _ ->
            assertEquals(0, key)
            listOf("item1", "item2")
        }
        assertEquals(listOf("item1", "item2"), result)
    }
}
