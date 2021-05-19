package dev.aspid812.softmachdemo.controller

import dev.aspid812.softmachdemo.service.model.User

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort

import io.restassured.http.*
import org.hamcrest.Matchers.*
import io.restassured.RestAssured.*
import io.restassured.matcher.RestAssuredMatchers.*
import io.restassured.module.kotlin.extensions.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetUsersTest {

	@LocalServerPort
	var serverPort: Int = 0

	@Test
	fun `Empty user list`() {
		Given {
			port(serverPort)
		} When {
			get("/users")
		} Then {
			statusCode(200)
			contentType(ContentType.JSON)
			body("", empty<User>())
		}
	}
}
