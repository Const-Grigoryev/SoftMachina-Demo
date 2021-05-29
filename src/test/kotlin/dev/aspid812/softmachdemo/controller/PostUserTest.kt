package dev.aspid812.softmachdemo.controller

import dev.aspid812.softmachdemo.service.UsersService
import dev.aspid812.softmachdemo.service.ConfigService
import dev.aspid812.softmachdemo.service.model.User

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.beans.factory.annotation.Autowired

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import io.restassured.http.*
import io.restassured.module.kotlin.extensions.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PostUserTest {
	@Autowired
	lateinit var usersService: UsersService

	@Autowired
	lateinit var configService: ConfigService

	@LocalServerPort
	var serverPort: Int = 0

	val methodPath = "/user"

	@BeforeEach
	fun setUp() {
		configService.reset()
		usersService.clear()
	}

	@AfterEach
	fun tearDown() {
		configService.reset()
		usersService.clear()
	}

	@Test
	fun `Post new user`() {
		val expectedUser = User("Alice", "12345")
		val actualUser = Given {
			port(serverPort)
			contentType(ContentType.JSON)
			body(expectedUser)
		} When {
			post(methodPath)
		} Then {
			statusCode(200)
		} Extract {
			usersService.findUser("Alice")
		}

		assertEquals(expectedUser, actualUser)
	}

	@Test
	fun `Post existent user`() {
		val existentUser = with(usersService) {
			addUser("Alice", "12345")
			findUser("Alice")
		}

		Given {
			port(serverPort)
			contentType(ContentType.JSON)
			body(existentUser)
		} When {
			post(methodPath)
		} Then {
			statusCode(500)
		}
	}

	@Test
	fun `Post user with illegal username`() {
		val illegalUser = User("???", "BtXBMaaZ")

		with(configService) {
			regexUsername = Regex("[A-Za-z]+")
		}

		Given {
			port(serverPort)
			contentType(ContentType.JSON)
			body(illegalUser)
		} When {
			post(methodPath)
		} Then {
			statusCode(500)
		}
	}

	@Test
	fun `Post user with illegal password`() {
		val illegalUser = User("Bob", "")

		with(configService) {
			regexPassword = Regex(".{6,}")
		}

		Given {
			port(serverPort)
			contentType(ContentType.JSON)
			body(illegalUser)
		} When {
			post(methodPath)
		} Then {
			statusCode(500)
		}
	}
}