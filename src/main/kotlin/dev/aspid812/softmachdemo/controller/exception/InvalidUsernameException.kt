package dev.aspid812.softmachdemo.controller.exception

class InvalidUsernameException(
	username: String
) : Exception("Username '$username' does not meet the pattern")
