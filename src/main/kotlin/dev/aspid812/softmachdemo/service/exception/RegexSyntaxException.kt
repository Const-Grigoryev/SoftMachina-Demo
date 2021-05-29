package dev.aspid812.softmachdemo.service.exception

import java.util.regex.PatternSyntaxException

class RegexSyntaxException(
	pattern: String
) : Exception("Syntax error in the regular expression '${pattern}'")
