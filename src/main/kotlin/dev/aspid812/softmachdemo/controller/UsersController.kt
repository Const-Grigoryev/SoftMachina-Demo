package dev.aspid812.softmachdemo.controller

import dev.aspid812.softmachdemo.service.model.User
import dev.aspid812.softmachdemo.dto.UpdatePasswordDto
import dev.aspid812.softmachdemo.service.UsersService
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
class UsersController(
	private val service: UsersService
) {
	@GetMapping("/users")
	fun getUsers(): Flux<User> {
		val users = service.listUsers().asIterable()
		return Flux.fromIterable(users)
	}

	@PostMapping("/user")
	fun postUser(
		@RequestBody body: User
	): Mono<Nothing> {
		service.addUser(body.username, body.password)
		return Mono.empty()
	}

	@PostMapping("/updatePassword")
	fun postUpdatePassword(
		@RequestBody body: UpdatePasswordDto
	): Mono<Nothing> {
		service.updateUserPassword(body.username, body.oldpassword, body.password)
		return Mono.empty()
	}
}