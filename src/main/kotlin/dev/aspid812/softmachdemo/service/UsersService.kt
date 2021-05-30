package dev.aspid812.softmachdemo.service

import dev.aspid812.softmachdemo.service.exception.UserAlreadyExistsException
import dev.aspid812.softmachdemo.service.model.User
import dev.aspid812.softmachdemo.service.exception.UserNotFoundException
import dev.aspid812.softmachdemo.service.exception.WrongPasswordException
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

@Service
class UsersService {
	private val userByName: ConcurrentMap<String, User> = ConcurrentHashMap()

	fun clear() {
		userByName.clear()
	}

	fun listUsers(): Sequence<User> {
		return userByName.asSequence()
			.map { entry -> entry.value }
	}

	@Throws(UserNotFoundException::class)
	fun findUser(username: String): User {
		return userByName[username] ?: throw UserNotFoundException(username)
	}

	@Throws(UserAlreadyExistsException::class)
	fun addUser(username: String, password: String): User {
		val newUser = User(username, password)
		val oldUser = userByName.putIfAbsent(username, newUser)
		if (oldUser != null) {
			throw UserAlreadyExistsException(username)
		}
		return newUser
	}

	@Throws(UserNotFoundException::class, WrongPasswordException::class)
	fun updateUserPassword(username: String, oldPassword: String, newPassword: String) {
		val newUser = User(username, newPassword)
		val oldUser = userByName[username] ?: throw UserNotFoundException(username)
		if (oldUser.password != oldPassword) {
			throw WrongPasswordException(username)
		}

		// A perfect place to hide a nasty concurrent bug here...
		userByName[username] = newUser
	}
}
