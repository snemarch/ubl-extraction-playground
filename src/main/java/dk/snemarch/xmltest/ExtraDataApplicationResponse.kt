package dk.snemarch.xmltest;

import java.time.LocalDate;
import java.util.UUID;

data class ExtraDataApplicationResponse(
	var documentId:UUID,
	var issueDate:LocalDate,
	var responseCode:String,
	var description: String
)
