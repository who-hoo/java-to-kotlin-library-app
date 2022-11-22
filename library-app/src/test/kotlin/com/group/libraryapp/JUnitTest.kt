package com.group.libraryapp

import org.junit.jupiter.api.*

class JUnitTest {

    companion object {
        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            println("before all test")
        }

        @JvmStatic
        @AfterAll
        fun afterAll() {
            println("after all test")
        }
    }

    @BeforeEach
    fun beforeEach() {
        println("before each test")
    }

    @AfterEach
    fun afterEach() {
        println("after each test")
    }

    @Test
    fun test1() {
        println("test1")
    }

    @Test
    fun test2() {
        println("test2")
    }

}
