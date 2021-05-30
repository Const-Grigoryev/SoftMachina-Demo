package dev.aspid812.softmachdemo.controller

import dev.aspid812.softmachdemo.exception.SoftMachinaDemoException
import dev.aspid812.softmachdemo.dto.InternalServerExceptionDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

abstract class SoftMachinaDemoController {
	companion object {
		private fun <T> success(body: T): ResponseEntity<T> {
			return ResponseEntity.ok(body)
		}


		private fun exception(ex: SoftMachinaDemoException): ResponseEntity<InternalServerExceptionDto> {
			val body = InternalServerExceptionDto(ex.description)
			return ResponseEntity(body, HttpStatus.INTERNAL_SERVER_ERROR)
		}
	}

	protected fun <T> handleMono(responseBodyProvider: () -> Mono<T>): Mono<ResponseEntity<*>> {
		return try {
			responseBodyProvider().map(::success)
		}
		catch (ex: SoftMachinaDemoException) {
			Mono.just(exception(ex))
		}
	}

	protected fun <T> handleFlux(responseBodyProvider: () -> Flux<T>): Mono<ResponseEntity<*>> {
		return try {
			responseBodyProvider().collectList().map(::success)
		}
		catch (ex: SoftMachinaDemoException) {
			Mono.just(exception(ex))
		}
	}
}