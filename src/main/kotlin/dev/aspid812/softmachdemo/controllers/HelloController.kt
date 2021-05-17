package dev.aspid812.softmachdemo.controllers

import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.util.*

@RestController
class HelloController {
    @GetMapping("/hello")
    fun hello(
    	@RequestParam name: Optional<String>
	): Mono<String> {
    	return Mono.
	        just("Hello, ${name.orElse("World")}!")
	}
}
