package dk.snemarch.xmltest

import java.io.InputStream
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.XMLStreamException

class InvoiceExtractor(factory: XMLInputFactory) : DataExtractorBase(factory), DataExtractor<ExtraDataInvoice> {
	@Throws(XMLStreamException::class)
	override fun extract(document: InputStream): ExtraDataInvoice {
		val handler = InvoiceContentHandler()
		super.extractAndSave(document, handler)
		return handler.data!!
	}
}
