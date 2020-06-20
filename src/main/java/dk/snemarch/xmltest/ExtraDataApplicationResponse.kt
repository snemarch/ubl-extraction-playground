package dk.snemarch.xmltest;

import java.time.LocalDate

data class ExtraDataApplicationResponse(
	val issueDate:LocalDate,
	val responseCode:String,
	val description: String
)
