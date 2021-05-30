package dev.aspid812.softmachdemo.controller

import dev.aspid812.softmachdemo.dto.UpdatePasswordDto
import dev.aspid812.softmachdemo.service.ConfigService
import dev.aspid812.softmachdemo.service.UsersService
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
import org.hamcrest.Matchers.*


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PostUpdatePasswordTest {
	@Autowired
	lateinit var usersService: UsersService

	@Autowired
	lateinit var configService: ConfigService

	@LocalServerPort
	var serverPort: Int = 0

	val methodPath = "/updatePassword"

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
	fun `Update password successfully`() {
		val user = with(usersService) {
			addUser("Alice", "12345")
		}

		val expectedPassword = "password"
		val actualPassword = Given {
			port(serverPort)
			contentType(ContentType.JSON)
			body(UpdatePasswordDto(
				username = user.username,
				oldpassword = user.password,
				password = expectedPassword
			))
		} When {
			post(methodPath)
		} Then {
			statusCode(200)
		} Extract {
			usersService.findUser("Alice").password
		}

		assertEquals(expectedPassword, actualPassword)
	}

	@Test
	fun `User does not exist`() {
		val user = User("Alice", "12345")

		Given {
			port(serverPort)
			contentType(ContentType.JSON)
			body(UpdatePasswordDto(
				username = user.username,
				oldpassword = user.password,
				password = "password"
			))
		} When {
			post(methodPath)
		} Then {
			statusCode(500)
			body("message", not(blankOrNullString()))
		}
	}

	@Test
	fun `Password check failed`() {
		val user = with(usersService) {
			addUser("Alice", "12345")
		}

		Given {
			port(serverPort)
			contentType(ContentType.JSON)
			body(UpdatePasswordDto(
				username = user.username,
				oldpassword = "",
				password = "password"
			))
		} When {
			post(methodPath)
		} Then {
			statusCode(500)
			body("message", not(blankOrNullString()))
		}
	}

	@Test
	fun `New password is illegal`() {
		with (configService) {
			regexPassword = Regex(".*\\d.*")
		}

		val user = with(usersService) {
			addUser("Alice", "12345")
		}

		Given {
			port(serverPort)
			contentType(ContentType.JSON)
			body(UpdatePasswordDto(
				username = user.username,
				oldpassword = user.password,
				password = "password"
			))
		} When {
			post(methodPath)
		} Then {
			statusCode(500)
			body("message", not(blankOrNullString()))
		}
	}
}