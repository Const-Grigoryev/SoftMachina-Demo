package dev.aspid812.softmachdemo.controller

import dev.aspid812.softmachdemo.controller.exception.InvalidPasswordException
import dev.aspid812.softmachdemo.controller.exception.InvalidUsernameException
import dev.aspid812.softmachdemo.service.model.User
import dev.aspid812.softmachdemo.dto.UpdatePasswordDto
import dev.aspid812.softmachdemo.service.UsersService
import dev.aspid812.softmachdemo.service.exception.RegexSyntaxException
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import java.util.regex.PatternSyntaxException

@RestController
class UsersController(
	private val service: UsersService
) {
	var usernameRegex: Regex? = null
	var passwordRegex: Regex? = null

	@GetMapping("/users")
	fun getUsers(
		@RequestParam(required=false) userNameMask: String?
	): Flux<User> {
		val usernameRegex: Regex?
		try {
			usernameRegex = userNameMask?.let { Regex(it) }
		}
		catch (ex: PatternSyntaxException) {
			throw RegexSyntaxException(ex)
		}

		return service.listUsers()
			.filter { user -> usernameRegex?.matches(user.username) ?: true }
			.toFlux()
	}

	@PostMapping("/user")
	fun postUser(
		@RequestBody body: User
	): Mono<Nothing> {
		if (usernameRegex?.matches(body.username) == false) {
			throw InvalidUsernameException(body.username)
		}

		if (passwordRegex?.matches(body.username) == false) {
			throw InvalidPasswordException(body.username)
		}

		service.addUser(body.username, body.password)
		return Mono.empty()
	}

	@PostMapping("/updatePassword")
	fun postUpdatePassword(
		@RequestBody body: UpdatePasswordDto
	): Mono<Nothing> {
		if (passwordRegex?.matches(body.username) == false) {
			throw InvalidPasswordException(body.username)
		}

		service.updateUserPassword(body.username, body.oldpassword, body.password)
		return Mono.empty()
	}
}