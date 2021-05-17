package dev.aspid812.softmachdemo.controllers

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.util.*

@Controller
class GreetingController {
    @GetMapping("/hello")
    fun hello(
    	@RequestParam name: Optional<String>
	): Mono<> {
    	val body = "Hello, ${name.orElse("World")}!"
    	return ServerResponse
		    .ok()
		    .contentType(MediaType.TEXT_PLAIN)
		    .bodyValue(body)
//	    return Mono.just(body)
//	    return Mono.just(response)
	}
}
