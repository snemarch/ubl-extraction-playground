package dk.snemarch.xmltest

import org.codehaus.staxmate.SMInputFactory
import org.codehaus.staxmate.`in`.SMInputCursor
import java.io.InputStream
import java.math.BigDecimal
import java.time.LocalDate
import java.util.ArrayList
import java.util.Comparator

/*
Structure we're interested in:

Invoice
	ID							[1..1]
	IssueDate					[1..1]
	DocumentCurrencyCode		[0..1]
	LegalMonetaryTotal			[1..1]
		LineExtensionAmount		[0..1]
		TaxExclusiveAmount		[0..1]
		TaxInclusiveAmount		[0..1]
		PayableAmount			[1..1]
	PaymentMeans				[0..*]
		PaymentDueDate			[0..1]
 */
class InvoiceExtractorSM(private val factory: SMInputFactory) : DataExtractor<ExtraDataInvoice> {
	companion object {
		private val paymentFilter = NamedElementFilter("PaymentDueDate")
	}

	private val paymentDueDates: MutableList<LocalDate> = ArrayList()
	private var invoiceNumber: String? = null
	private var issueDate: LocalDate? = null
	private var documentCurrencyCode: String? = null
	private var lineExtensionAmount: BigDecimal? = null
	private var taxExclusiveAmount: BigDecimal? = null
	private var taxInclusiveAmount: BigDecimal? = null
	private var payableAmount: BigDecimal? = null

	override fun extract(document: InputStream): ExtraDataInvoice {
		val root = factory.rootElementCursor(document).advance()
		try {
			val child = root.childElementCursor()
			while (child.next != null) {
				when (child.localName) {
					"ID" -> invoiceNumber = child.elemStringValue
					"IssueDate" -> issueDate = LocalDate.parse(child.elemStringValue)
					"DocumentCurrencyCode" -> documentCurrencyCode = child.elemStringValue
					"LegalMonetaryTotal" -> handleMonetary(child.childElementCursor())
					"PaymentMeans" -> handlePaymentMeans(child.childCursor(paymentFilter))
				}
			}
		} finally {
			root.streamReader.closeCompletely()
		}

		if (paymentDueDates.size > 1) {
			System.err.println("Multiple PaymentMeans.PaymentDueDate present, selecting latest one")
		}
		val paymentDueDate = paymentDueDates.stream().sorted(Comparator.reverseOrder()).findFirst().orElse(null)

		return ExtraDataInvoice(
			invoiceNumber = invoiceNumber!!,
			issueDate = issueDate!!,
			documentCurrencyCode = documentCurrencyCode!!,
			lineExtensionAmount = lineExtensionAmount!!,
			taxExclusiveAmount = taxExclusiveAmount!!,
			taxInclusiveAmount = taxInclusiveAmount!!,
			payableAmount = payableAmount!!,
			paymentDueDate = paymentDueDate
		)
	}

	private fun handleMonetary(child: SMInputCursor) {
		while (child.next != null) {
			when (child.localName) {
				"LineExtensionAmount" -> lineExtensionAmount = BigDecimal(child.elemStringValue)
				"TaxExclusiveAmount" -> taxExclusiveAmount = BigDecimal(child.elemStringValue)
				"TaxInclusiveAmount" -> taxInclusiveAmount = BigDecimal(child.elemStringValue)
				"PayableAmount" -> payableAmount = BigDecimal(child.elemStringValue)
			}
		}
	}

	private fun handlePaymentMeans(child: SMInputCursor) {
		while (child.next != null) {
			when (child.localName) {
				"PaymentDueDate" -> paymentDueDates.add(LocalDate.parse(child.elemStringValue))
			}
		}
	}
}
