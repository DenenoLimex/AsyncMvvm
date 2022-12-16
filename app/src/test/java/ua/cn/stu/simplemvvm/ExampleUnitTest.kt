package ua.cn.stu.simplemvvm

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testFlows() = runBlocking {
        val numbers = 1..10
        val flow: Flow<Int> = numbers.asFlow()

        println("Printing only even numbers by 10:")
        flow.filter { it % 2 == 0 }
            .map { it * 10 }
            .collect {
                delay(1000)
                println(it)
            }

        println("Printing only odd:")
        flow.filter { it % 2 == 1 }
            .collect {
                delay(1000)
                println(it)
            }
    }
}