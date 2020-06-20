import dk.snemarch.xmltest.*
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe
import org.codehaus.staxmate.SMInputFactory
import java.math.BigDecimal
import java.time.LocalDate
import javax.xml.stream.XMLInputFactory

class DataExtractorTest: ExpectSpec({
	val jreFactory = XMLInputFactory.newDefaultFactory()
	val woodstoxFactory = Utilities.woodstoxFactory()
	val aaltoFactory = Utilities.aaltoFactory()
	val woodstoxSMFactory = SMInputFactory(woodstoxFactory)
	val aaltoSMFactory = SMInputFactory(aaltoFactory)

	val applicationResponse = Utilities.loadResource("ApplicationResponse.xml")
	val invoice = Utilities.loadResource("Invoice.xml")

	val goodApplicationResponse = ExtraDataApplicationResponse(
		issueDate = LocalDate.parse("2020-05-28"),
		responseCode = "BusinessPaid",
		description = "28 May 2020 - Votre facture a été payée 28 May 2020 -  - ."
	)

	val goodInvoice = ExtraDataInvoice(
		invoiceNumber = "05860835",
		issueDate = LocalDate.parse("2017-09-14"),
		documentCurrencyCode = "CNY",
		lineExtensionAmount = BigDecimal("103642.2500"),
		taxExclusiveAmount = BigDecimal("0"),
		taxInclusiveAmount = BigDecimal("103642.2500"),
		payableAmount = BigDecimal("103642.2500"),
		paymentDueDate = LocalDate.parse("2018-01-12"))

	context("XML Factories") {
		// These tests are super black-box and could easily break on updated library versions.
		// They're meant as sanity checks
		expect("JRE is good") {
			val reader = jreFactory.createXMLStreamReader(applicationResponse.inputStream())
			reader.javaClass.canonicalName shouldBe "com.sun.org.apache.xerces.internal.impl.XMLStreamReaderImpl"
		}

		expect("Woodstox is good") {
			val reader = woodstoxFactory.createXMLStreamReader(applicationResponse.inputStream())
			reader.javaClass.canonicalName shouldBe "com.ctc.wstx.sr.ValidatingStreamReader"
		}

		expect("Aalto is good") {
			val reader = aaltoFactory.createXMLStreamReader(applicationResponse.inputStream())
			reader.javaClass.canonicalName shouldBe "com.fasterxml.aalto.stax.StreamReaderImpl"
		}
	}



	context("ApplicationResponses") {
		expect("JRE/default works correctly") {
			val data = ApplicationResponseExtractor(jreFactory).extract(applicationResponse.inputStream())
			data shouldBe goodApplicationResponse
		}

		expect("Woodstox/default works correctly") {
			val data = ApplicationResponseExtractor(woodstoxFactory).extract(applicationResponse.inputStream())
			data shouldBe goodApplicationResponse
		}

		expect("Aalto/default works correctly") {
			val data = ApplicationResponseExtractor(aaltoFactory).extract(applicationResponse.inputStream())
			data shouldBe goodApplicationResponse
		}

		expect("Woodstox/StaxMate works correctly") {
			val data = ApplicationResponseExtractorSM(woodstoxSMFactory).extract(applicationResponse.inputStream())
			data shouldBe goodApplicationResponse
		}

		expect("Aalto/StaxMate works correctly") {
			val data = ApplicationResponseExtractorSM(aaltoSMFactory).extract(applicationResponse.inputStream())
			data shouldBe goodApplicationResponse
		}
	}



	context("Invoices") {
		expect("JRE/default works correctly") {
			val data = InvoiceExtractor(jreFactory).extract(invoice.inputStream())
			data shouldBe goodInvoice
		}

		expect("Woodstox/default works correctly") {
			val data = InvoiceExtractor(woodstoxFactory).extract(invoice.inputStream())
			data shouldBe goodInvoice
		}

		expect("Aalto/default works correctly") {
			val data = InvoiceExtractor(aaltoFactory).extract(invoice.inputStream())
			data shouldBe goodInvoice
		}

		expect("Woodstox/StaxMate works correctly") {
			val data = InvoiceExtractorSM(woodstoxSMFactory).extract(invoice.inputStream())
			data shouldBe goodInvoice
		}

		expect("Aalto/StaxMate works correctly") {
			val data = InvoiceExtractorSM(aaltoSMFactory).extract(invoice.inputStream())
			data shouldBe goodInvoice
		}
	}
})

