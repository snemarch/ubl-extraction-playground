package dk.snemarch.xmltest

import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

class InvoiceContentHandler : BaseContentHandler {
	var data: ExtraDataInvoice? = null
		private set

	private val paymentDueDates: MutableList<LocalDate?> = ArrayList()
	private var invoiceNumber: String? = null
	private var issueDate: LocalDate? = null
	private var documentCurrencyCode: String? = null
	private var lineExtensionAmount: BigDecimal? = null
	private var taxExclusiveAmount: BigDecimal? = null
	private var taxInclusiveAmount: BigDecimal? = null
	private var payableAmount: BigDecimal? = null

	override fun processElementData(path: String, value: String) {
		when (path) {
			".Invoice.ID" -> invoiceNumber = value
			".Invoice.IssueDate" -> issueDate = LocalDate.parse(value)
			".Invoice.DocumentCurrencyCode" -> documentCurrencyCode = value
			".Invoice.LegalMonetaryTotal.LineExtensionAmount" -> lineExtensionAmount = BigDecimal(value)
			".Invoice.LegalMonetaryTotal.TaxExclusiveAmount" -> taxExclusiveAmount = BigDecimal(value)
			".Invoice.LegalMonetaryTotal.TaxInclusiveAmount" -> taxInclusiveAmount = BigDecimal(value)
			".Invoice.LegalMonetaryTotal.PayableAmount" -> payableAmount = BigDecimal(value)
			".Invoice.PaymentMeans.PaymentDueDate" -> paymentDueDates.add(LocalDate.parse(value))
		}
	}

	override fun processEndDocument() {
		if (paymentDueDates.size > 1) {
			System.err.println("Multiple PaymentMeans.PaymentDueDate present, selecting latest one")
		}
		val dueDate = paymentDueDates.stream().sorted(Comparator.reverseOrder()).findFirst().orElse(null)
		data = ExtraDataInvoice(invoiceNumber!!, issueDate!!, documentCurrencyCode!!,
			lineExtensionAmount!!, taxExclusiveAmount!!, taxInclusiveAmount!!, payableAmount!!, dueDate!!)
	}
}
