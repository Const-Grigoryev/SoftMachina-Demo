package dev.aspid812.softmachdemo.dto

data class UpdatePasswordDto(
	val username: String,
	val oldpassword: String,
	val password: String
)
