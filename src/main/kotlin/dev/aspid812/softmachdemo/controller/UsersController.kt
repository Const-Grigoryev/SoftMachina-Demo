package dev.aspid812.softmachdemo.controller

import dev.aspid812.softmachdemo.exception.InvalidPasswordException
import dev.aspid812.softmachdemo.exception.InvalidUsernameException
import dev.aspid812.softmachdemo.model.User
import dev.aspid812.softmachdemo.dto.UpdatePasswordDto
import dev.aspid812.softmachdemo.service.ConfigService
import dev.aspid812.softmachdemo.service.UsersService
import org.springframework.beans.factory.annotation.Value
import org.springframework.util.ConcurrentLruCache
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import java.time.Duration
import java.util.regex.PatternSyntaxException


private sealed class CachedPattern {
	abstract fun toRegex(): Regex

	class Valid(
		val regex: Regex
	): CachedPattern() {
		override fun toRegex() = regex
	}

	class Invalid(
		val exception: PatternSyntaxException
	): CachedPattern() {
		override fun toRegex() = throw exception
	}
}


@RestController
class UsersController(
	private val service: UsersService,
	private val config: ConfigService,

	@Value("\${regexCacheSize}")
	private val regexCacheSize: Int
) : SoftMachinaDemoController() {
	private val regexCache = ConcurrentLruCache<String, CachedPattern>(regexCacheSize) {
		try {
			val regex = Regex(it)
			CachedPattern.Valid(regex)
		}
		catch (ex: PatternSyntaxException) {
			CachedPattern.Invalid(ex)
		}
	}

	@GetMapping("/users")
	fun getUsers(
		@RequestParam(required=false) userNameMask: String?
	) = handleFlux {
		var users = service.listUsers()
		if (userNameMask != null) {
			val regex = regexCache.get(userNameMask).toRegex()
			users = users.filter { user -> regex.matches(user.username) }
		}

		val delay = Duration.ofMillis(config.delayGetUsers)
		users.toFlux().delaySubscription(delay)
	}

	@PostMapping("/user")
	fun postUser(
		@RequestBody body: User
	) = handleMono {
		if (config.regexUsername?.matches(body.username) == false) {
			throw InvalidUsernameException(body.username)
		}

		if (config.regexPassword?.matches(body.password) == false) {
			throw InvalidPasswordException(body.password)
		}

		service.addUser(body.username, body.password)

		val delay = Duration.ofMillis(config.delayPostUser)
		Mono.empty<Nothing>().delaySubscription(delay)
	}

	@PostMapping("/updatePassword")
	fun postUpdatePassword(
		@RequestBody body: UpdatePasswordDto
	) = handleMono {
		if (config.regexPassword?.matches(body.password) == false) {
			throw InvalidPasswordException(body.password)
		}

		service.updateUserPassword(body.username, body.oldpassword, body.password)

		val delay = Duration.ofMillis(config.delayPostUpdatePassword)
		Mono.empty<Nothing>().delaySubscription(delay)
	}
}