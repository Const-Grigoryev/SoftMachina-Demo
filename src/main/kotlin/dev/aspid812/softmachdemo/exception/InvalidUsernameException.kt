package dev.aspid812.softmachdemo.exception

class InvalidUsernameException(
	username: String
) : SoftMachinaDemoException("Username '$username' does not meet the pattern")
