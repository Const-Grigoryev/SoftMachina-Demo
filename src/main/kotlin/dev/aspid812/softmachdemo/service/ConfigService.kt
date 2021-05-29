package dev.aspid812.softmachdemo.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ConfigService(
	@Value("\${delay.getUsers}")
	private val DELAY_GET_USERS: Long,

	@Value("\${delay.postUser}")
	private val DELAY_POST_USER: Long,

	@Value("\${delay.postUpdatePassword}")
	private val DELAY_POST_UPDATE_PASSWORD: Long,

	@Value("\${regex.username}")
	private val REGEX_USERNAME: Regex?,

	@Value("\${regex.password}")
	private val REGEX_PASSWORD: Regex?
) {
	var delayGetUsers = DELAY_GET_USERS
	var delayPostUser = DELAY_POST_USER
	var delayPostUpdatePassword = DELAY_POST_UPDATE_PASSWORD

	var regexUsername = REGEX_USERNAME
	var regexPassword = REGEX_PASSWORD

	fun reset() {
		delayGetUsers = DELAY_GET_USERS
		delayPostUser = DELAY_POST_USER
		delayPostUpdatePassword = DELAY_POST_UPDATE_PASSWORD
		regexUsername = REGEX_USERNAME
		regexPassword = REGEX_PASSWORD
	}
}