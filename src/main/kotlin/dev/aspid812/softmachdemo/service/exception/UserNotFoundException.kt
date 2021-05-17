package dev.aspid812.softmachdemo.service.exception

class UserNotFoundException(
	username: String
) : Exception("User '$username' not found")
