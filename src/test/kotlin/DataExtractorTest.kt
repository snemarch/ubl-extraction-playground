import com.ctc.wstx.stax.WstxInputFactory
import com.fasterxml.aalto.stax.InputFactoryImpl
import dk.snemarch.xmltest.ApplicationResponseExtractor
import dk.snemarch.xmltest.ApplicationResponseExtractorSM
import dk.snemarch.xmltest.ExtraDataApplicationResponse
import dk.snemarch.xmltest.loadResource
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe
import org.codehaus.staxmate.SMInputFactory
import java.time.LocalDate
import javax.xml.stream.XMLInputFactory

class DataExtractorTest: ExpectSpec({
	val jreFactory = XMLInputFactory.newDefaultFactory()
	val woodstoxFactory = woodstoxFactory()
	val aaltoFactory = aaltoFactory()
	val woodstoxSMFactory = SMInputFactory(woodstoxFactory)
	val aaltoSMFactory = SMInputFactory(aaltoFactory)
	val document = loadResource("document.xml")

	val goodApplicationResponse = ExtraDataApplicationResponse(LocalDate.parse("2020-05-28"),
		"BusinessPaid", "28 May 2020 - Votre facture a été payée 28 May 2020 -  - .")

	context("XML Factories") {
		// These tests are super black-box and could easily break on updated library versions.
		// They're meant as sanity checks
		expect("JRE is good") {
			val reader = jreFactory.createXMLStreamReader(document.inputStream())
			reader.javaClass.canonicalName shouldBe "com.sun.org.apache.xerces.internal.impl.XMLStreamReaderImpl"
		}

		expect("Woodstox is good") {
			val reader = woodstoxFactory.createXMLStreamReader(document.inputStream())
			reader.javaClass.canonicalName shouldBe "com.ctc.wstx.sr.ValidatingStreamReader"
		}

		expect("Aalto is good") {
			val reader = aaltoFactory.createXMLStreamReader(document.inputStream())
			reader.javaClass.canonicalName shouldBe "com.fasterxml.aalto.stax.StreamReaderImpl"
		}
	}

	context("ApplicationResponses") {
		expect("JRE/default works correctly") {
			val data = ApplicationResponseExtractor(jreFactory).extract(document.inputStream())
			data shouldBe goodApplicationResponse
		}

		expect("Woodstox/default works correctly") {
			val data = ApplicationResponseExtractor(woodstoxFactory).extract(document.inputStream())
			data shouldBe goodApplicationResponse
		}

		expect("Aalto/default works correctly") {
			val data = ApplicationResponseExtractor(aaltoFactory).extract(document.inputStream())
			data shouldBe goodApplicationResponse
		}

		expect("Woodstox/StaxMate works correctly") {
			val data = ApplicationResponseExtractorSM(woodstoxSMFactory).extract(document.inputStream())
			data shouldBe goodApplicationResponse
		}

		expect("Aalto/StaxMate works correctly") {
			val data = ApplicationResponseExtractorSM(aaltoSMFactory).extract(document.inputStream())
			data shouldBe goodApplicationResponse
		}
	}
})

private fun woodstoxFactory(): XMLInputFactory = WstxInputFactory.newFactory().apply {
	setProperty(XMLInputFactory.IS_COALESCING, true)
	setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false)
}

private fun aaltoFactory(): XMLInputFactory = InputFactoryImpl().apply {
	//NOTE: calling AaltoInputFactory.newFactory creates a Woodstox factory if Woodstox is on the classpath,
	//we need a direct constructor invocation to get the Aalto factory!
	setProperty(XMLInputFactory.IS_COALESCING, true)
	setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false)
}
