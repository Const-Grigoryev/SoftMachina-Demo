package dev.aspid812.softmachdemo.service.exception

class WrongPasswordException(
	username: String
) : Exception("Wrong password for user '$username'")
