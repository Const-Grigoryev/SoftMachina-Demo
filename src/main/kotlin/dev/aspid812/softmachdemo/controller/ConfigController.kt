package dev.aspid812.softmachdemo.controller

import dev.aspid812.softmachdemo.service.ConfigService
import dev.aspid812.softmachdemo.service.exception.RegexSyntaxException
import org.springframework.web.bind.annotation.*
import java.util.regex.PatternSyntaxException

@RestController
@RequestMapping("/api/config")
class ConfigController(
	val service: ConfigService
) {
	@PatchMapping("/delay")
	fun patchDelay(
		@RequestParam method: String,
		@RequestParam value: Long
	) {
		if (value < 0) {
			throw IllegalArgumentException("Delay value must be >= 0")
		}

		when (method) {
			"getUser" -> service.delayGetUsers = value
			"postUser" -> service.delayPostUser = value
			"postUpdate" -> service.delayPostUpdatePassword = value
			else -> throw IllegalArgumentException("Unsupported method name: '$method'")
		}
	}

	@PatchMapping("/regex")
	fun patchRegex(
		@RequestParam subject: String,
		@RequestParam(required=false) pattern: String?
	) {
		val regex: Regex?
		try {
			regex = pattern?.let { Regex(it) }
		}
		catch (ex: PatternSyntaxException) {
			throw RegexSyntaxException(ex)
		}

		when (subject) {
			"username" -> service.regexUsername = regex
			"password" -> service.regexPassword = regex
			else -> throw IllegalArgumentException("Unsupported regex subject: $subject")
		}
	}
}