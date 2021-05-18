package dev.aspid812.softmachdemo.controller.exception

class InvalidPasswordException(
	password: String
) : Exception("Password '$password' does not meet the pattern")
