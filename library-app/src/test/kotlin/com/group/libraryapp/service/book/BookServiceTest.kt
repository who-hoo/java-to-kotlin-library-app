package com.group.libraryapp.service.book

import com.group.libraryapp.domain.book.Book
import com.group.libraryapp.domain.book.BookRepository
import com.group.libraryapp.domain.book.BookType
import com.group.libraryapp.domain.user.User
import com.group.libraryapp.domain.user.UserRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistoryRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistory
import com.group.libraryapp.domain.user.loanhistory.UserLoanStatus
import com.group.libraryapp.dto.book.request.BookLoanRequest
import com.group.libraryapp.dto.book.request.BookRequest
import com.group.libraryapp.dto.book.request.BookReturnRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class BookServiceTest @Autowired constructor(
    private val bookRepository: BookRepository,
    private val userRepository: UserRepository,
    private val userLoanHistoryRepository: UserLoanHistoryRepository,
    private val bookService: BookService,
) {

    @AfterEach
    fun tearDown() {
        bookRepository.deleteAll()
        userRepository.deleteAll()
        userLoanHistoryRepository.deleteAll()
    }

    @Test
    @DisplayName("책 저장이 정상 동작한다")
    fun saveBookTest() {
        // given
        val request = BookRequest("A", BookType.COMPUTER)

        // when
        bookService.saveBook(request)

        // then
        val results = bookRepository.findAll()
        assertThat(results).hasSize(1)
        assertThat(results[0].name).isEqualTo("A")
        assertThat(results[0].type).isEqualTo(BookType.COMPUTER)
    }

    @Test
    @DisplayName("책 대출이 정상 동작한다")
    fun loanBookTest() {
        // given
        bookRepository.save(Book.fixture("A"))
        val savedUser = userRepository.save(
            User(
                "testUser",
                null
            )
        )
        val request = BookLoanRequest("testUser", "A")

        // when
        bookService.loanBook(request)

        // then
        val results = userLoanHistoryRepository.findAll()
        assertThat(results).hasSize(1)
        assertThat(results[0].bookName).isEqualTo("A")
        assertThat(results[0].user.id).isEqualTo(savedUser.id)
        assertThat(results[0].status).isEqualTo(UserLoanStatus.LOANED)
    }

    @Test
    @DisplayName("책이 대출되어 있다면, 신규 대출이 실패한다")
    fun loanBookFailTest() {
        // given
        bookRepository.save(Book.fixture("A"))
        val savedUser = userRepository.save(
            User(
                "testUser",
                null
            )
        )
        userLoanHistoryRepository.save(UserLoanHistory.fixture(savedUser, "A"))
        val request = BookLoanRequest("testUser", "A")

        // when & then
        assertThrows<IllegalArgumentException> {
            bookService.loanBook(request)
        }.apply {
            assertThat(message).isEqualTo("진작 대출되어 있는 책입니다")
        }
    }

    @Test
    @DisplayName("책 반납이 정상 동작한다")
    fun returnBookTest() {
        // given
        val savedUser = userRepository.save(
            User(
                "testUser",
                null
            )
        )
        userLoanHistoryRepository.save(UserLoanHistory.fixture(savedUser, "A"))
        val request = BookReturnRequest("testUser", "A")

        // when
        bookService.returnBook(request)

        // then
        val results = userLoanHistoryRepository.findAll()
        assertThat(results).hasSize(1)
        assertThat(results[0].bookName).isEqualTo("A")
        assertThat(results[0].user.id).isEqualTo(savedUser.id)
        assertThat(results[0].status).isEqualTo(UserLoanStatus.RETURNED)
    }

}
