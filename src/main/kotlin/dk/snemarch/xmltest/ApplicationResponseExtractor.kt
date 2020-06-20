package dk.snemarch.xmltest

import java.io.InputStream
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.XMLStreamException

class ApplicationResponseExtractor(factory: XMLInputFactory) : DataExtractorBase(factory), DataExtractor<ExtraDataApplicationResponse> {
	@Throws(XMLStreamException::class)
	override fun extract(document: InputStream): ExtraDataApplicationResponse {
		val handler = ApplicationResponseContentHandler()
		super.extractAndSave(document, handler)
		return handler.data!!
	}
}
