package dev.aspid812.softmachdemo.exception

class UserNotFoundException(
	username: String
) : SoftMachinaDemoException("User '$username' not found")
