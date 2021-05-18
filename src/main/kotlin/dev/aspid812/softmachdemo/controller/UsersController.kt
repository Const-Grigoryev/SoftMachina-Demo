package dev.aspid812.softmachdemo.controller

import dev.aspid812.softmachdemo.controller.exception.InvalidPasswordException
import dev.aspid812.softmachdemo.controller.exception.InvalidUsernameException
import dev.aspid812.softmachdemo.service.model.User
import dev.aspid812.softmachdemo.dto.UpdatePasswordDto
import dev.aspid812.softmachdemo.service.ConfigService
import dev.aspid812.softmachdemo.service.UsersService
import dev.aspid812.softmachdemo.service.exception.RegexSyntaxException
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import java.time.Duration
import java.util.regex.PatternSyntaxException

@RestController
class UsersController(
	private val service: UsersService,
	private val config: ConfigService
) {
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

		val users = service.listUsers()
			.filter { user -> usernameRegex?.matches(user.username) ?: true }

		val delay = Duration.ofMillis(config.delayGetUsers)
		return users.toFlux().delaySubscription(delay)
	}

	@PostMapping("/user")
	fun postUser(
		@RequestBody body: User
	): Mono<Nothing> {
		if (config.regexUsername?.matches(body.username) == false) {
			throw InvalidUsernameException(body.username)
		}

		if (config.regexPassword?.matches(body.password) == false) {
			throw InvalidPasswordException(body.password)
		}

		service.addUser(body.username, body.password)

		val delay = Duration.ofMillis(config.delayPostUser)
		return Mono.empty<Nothing>().delaySubscription(delay)
	}

	@PostMapping("/updatePassword")
	fun postUpdatePassword(
		@RequestBody body: UpdatePasswordDto
	): Mono<Nothing> {
		if (config.regexPassword?.matches(body.password) == false) {
			throw InvalidPasswordException(body.password)
		}

		service.updateUserPassword(body.username, body.oldpassword, body.password)

		val delay = Duration.ofMillis(config.delayPostUpdatePassword)
		return Mono.empty<Nothing>().delaySubscription(delay)
	}
}