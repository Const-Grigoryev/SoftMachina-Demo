package dev.aspid812.softmachdemo.controller

import dev.aspid812.softmachdemo.dto.UpdatePasswordDto
import dev.aspid812.softmachdemo.service.ConfigService
import dev.aspid812.softmachdemo.service.UsersService
import dev.aspid812.softmachdemo.model.User

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.beans.factory.annotation.Autowired

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.AfterEach
import io.restassured.http.*
import io.restassured.module.kotlin.extensions.*
import org.hamcrest.Matchers.*


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PatchDelayTest {
	@Autowired
	lateinit var usersService: UsersService

	@Autowired
	lateinit var configService: ConfigService

	@LocalServerPort
	var serverPort: Int = 0

	val methodPath = "/api/config/delay"

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
	fun `Delay getUsers`() {
		val delay = 500L

		Given {
			port(serverPort)
			queryParam("method", "getUsers")
			queryParam("value", delay)
		} When {
			patch(methodPath)
		} Then {
			statusCode(200)
		}

		Given {
			port(serverPort)
		} When {
			get("/users")
		} Then {
			time(greaterThanOrEqualTo(delay))
		}
	}

	@Test
	fun `Delay postUser`() {
		val delay = 500L

		Given {
			port(serverPort)
			queryParam("method", "postUser")
			queryParam("value", delay)
		} When {
			patch(methodPath)
		} Then {
			statusCode(200)
		}

		Given {
			port(serverPort)
			contentType(ContentType.JSON)
			body(User("Alice", "12345"))
		} When {
			post("/user")
		} Then {
			time(greaterThanOrEqualTo(delay))
		}
	}

	@Test
	fun `Delay postUpdatePassword`() {
		val delay = 500L

		Given {
			port(serverPort)
			queryParam("method", "postUpdatePassword")
			queryParam("value", delay)
		} When {
			patch(methodPath)
		} Then {
			statusCode(200)
		}

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
			post("/updatePassword")
		} Then {
			time(greaterThanOrEqualTo(delay))
		}
	}
}