package dev.aspid812.softmachdemo.controller

import dev.aspid812.softmachdemo.service.UsersService
import dev.aspid812.softmachdemo.service.model.User

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.beans.factory.annotation.Autowired

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import io.restassured.http.*
import io.restassured.module.kotlin.extensions.*
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.AfterEach


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetUsersTest {
	@Autowired
	lateinit var usersService: UsersService

	@LocalServerPort
	var serverPort: Int = 0

	val methodPath = "/users"

	@BeforeEach
	fun setUp() {
		usersService.clear()
	}

	@AfterEach
	fun tearDown() {
		usersService.clear()
	}

	@Test
	fun `Empty user list`() {
		Given {
			port(serverPort)
		} When {
			get(methodPath)
		} Then {
			statusCode(200)
			contentType(ContentType.JSON)
			body("", empty<User>())
		}
	}

	@Test
	fun `Non-empty user list`() {
		with(usersService) {
			addUser("Alice", "12345")
		}

		Given {
			port(serverPort)
		} When {
			get(methodPath)
		} Then {
			statusCode(200)
			contentType(ContentType.JSON)
			body("", hasSize<User>(1))
			body("username", contains("Alice"))
			body("password", contains("12345"))
		}
	}

	@Test
	fun `Username filtering - no match`() {
		with(usersService) {
			addUser("Alice", "12345")
			addUser("Bob", "")
			addUser("Charlie", "BtXBMaaZ")
		}

		Given {
			port(serverPort)
			queryParam("userNameMask", "")
		} When {
			get(methodPath)
		} Then {
			statusCode(200)
			contentType(ContentType.JSON)
			body("", empty<User>())
		}
	}

	@Test
	fun `Username filtering - one match`() {
		with(usersService) {
			addUser("Alice", "12345")
			addUser("Bob", "")
			addUser("Charlie", "BtXBMaaZ")
		}

		Given {
			port(serverPort)
			queryParam("userNameMask", "B.*")
		} When {
			get(methodPath)
		} Then {
			statusCode(200)
			contentType(ContentType.JSON)
			body("", hasSize<User>(1))
			body("username", contains("Bob"))
			body("password", contains(""))
		}
	}

	@Test
	fun `Username filtering - multiple matches`() {
		with(usersService) {
			addUser("Alice", "12345")
			addUser("Bob", "")
			addUser("Charlie", "BtXBMaaZ")
		}

		Given {
			port(serverPort)
			queryParam("userNameMask", ".*e")
		} When {
			get(methodPath)
		} Then {
			statusCode(200)
			contentType(ContentType.JSON)
			body("", hasSize<User>(2))
			body("username", contains("Alice", "Charlie"))
		}
	}

	@Test
	fun `Invalid userNameMask parameter`() {
		Given {
			port(serverPort)
			queryParam("userNameMask", ")))")
		} When {
			get(methodPath)
		} Then {
			statusCode(500)
		}
	}


}
