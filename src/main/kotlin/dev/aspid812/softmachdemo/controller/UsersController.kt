package dev.aspid812.softmachdemo.controller

import dev.aspid812.softmachdemo.controller.exception.InvalidPasswordException
import dev.aspid812.softmachdemo.controller.exception.InvalidUsernameException
import dev.aspid812.softmachdemo.service.model.User
import dev.aspid812.softmachdemo.dto.UpdatePasswordDto
import dev.aspid812.softmachdemo.service.ConfigService
import dev.aspid812.softmachdemo.service.UsersService
import dev.aspid812.softmachdemo.service.exception.RegexSyntaxException
import org.springframework.beans.factory.annotation.Value
import org.springframework.util.ConcurrentLruCache
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap
import java.util.regex.PatternSyntaxException


@RestController
class UsersController(
	private val service: UsersService,
	private val config: ConfigService,

	@Value("\${regexCacheSize}")
	private val regexCacheSize: Int
) {
	private val regexCache = ConcurrentLruCache<String, Regex?>(regexCacheSize) {
		try {
			Regex(it)
		}
		catch (ex: PatternSyntaxException) {
			null
		}
	}

	@GetMapping("/users")
	fun getUsers(
		@RequestParam(required=false) userNameMask: String?
	): Flux<User> {
		var users = service.listUsers()
		if (userNameMask != null) {
			val regex = regexCache.get(userNameMask)
			users = users.filter { user -> regex.matches(user.username) }
		}

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