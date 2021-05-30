package dev.aspid812.softmachdemo.exception

class WrongPasswordException(
	username: String
) : SoftMachinaDemoException("Wrong password for user '$username'")
