package dev.aspid812.softmachdemo.exception

class InvalidPasswordException(
	password: String
) : SoftMachinaDemoException("Password '$password' does not meet the pattern")
