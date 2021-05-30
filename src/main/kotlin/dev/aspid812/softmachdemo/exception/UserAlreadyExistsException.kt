package dev.aspid812.softmachdemo.exception

class UserAlreadyExistsException(
	username: String
) : SoftMachinaDemoException("User '$username' already exists")
