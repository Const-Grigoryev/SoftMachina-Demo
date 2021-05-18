package dev.aspid812.softmachdemo.service.exception

import java.util.regex.PatternSyntaxException

class RegexSyntaxException(
	cause: PatternSyntaxException
) : Exception("Syntax error in the regular expression '${cause.pattern}'", cause)
