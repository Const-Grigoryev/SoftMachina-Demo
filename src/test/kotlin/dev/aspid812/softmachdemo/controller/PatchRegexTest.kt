package dev.aspid812.softmachdemo.controller

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
class PatchRegexTest {
	@Autowired
	lateinit var usersService: UsersService

	@Autowired
	lateinit var configService: ConfigService

	@LocalServerPort
	var serverPort: Int = 0

	val methodPath = "/api/config/regex"

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
	fun `Set username regex`() {
		Given {
			port(serverPort)
			queryParam("subject", "username")
			queryParam("pattern", "[A-Za-z]+")
		} When {
			patch(methodPath)
		} Then {
			statusCode(200)
		}

		Given {
			port(serverPort)
			contentType(ContentType.JSON)
			body(User("???", "BtXBMaaZ"))
		} When {
			post("/user")
		} Then {
			statusCode(500)
		}
	}

	@Test
	fun `Set password regex`() {
		Given {
			port(serverPort)
			queryParam("subject", "password")
			queryParam("pattern", ".{6,}")
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
			statusCode(500)
		}
	}
}