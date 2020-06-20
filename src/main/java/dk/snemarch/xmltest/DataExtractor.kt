package dk.snemarch.xmltest

import java.io.InputStream
import javax.xml.stream.XMLStreamException

interface DataExtractor<T> {
	@Throws(XMLStreamException::class)
	fun extract(input: InputStream): T
}
