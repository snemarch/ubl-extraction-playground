package dk.snemarch.xmltest;

import java.math.BigDecimal
import java.time.LocalDate

data class ExtraDataApplicationResponse(
	val issueDate:LocalDate,
	val responseCode:String,
	val description: String
)

data class ExtraDataInvoice(
	val invoiceNumber: String,
	val issueDate: LocalDate,
	val documentCurrencyCode: String,
	val lineExtensionAmount: BigDecimal,
	val taxExclusiveAmount: BigDecimal,
	val taxInclusiveAmount: BigDecimal,
	val payableAmount: BigDecimal,
	val paymentDueDate: LocalDate
)
