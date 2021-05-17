package dev.aspid812.softmachdemo.service.exception

class UserAlreadyExistsException(
	username: String
) : Exception("User '$username' already exists")
