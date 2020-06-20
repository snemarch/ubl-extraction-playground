package dk.snemarch.xmltest

import java.io.InputStream
import java.util.*
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.XMLStreamConstants
import javax.xml.stream.XMLStreamException

open class DataExtractorBase(private val factory: XMLInputFactory) {
	@Throws(XMLStreamException::class)
	protected fun extractAndSave(document: InputStream, handler: BaseContentHandler) {
		val reader = factory.createXMLStreamReader(document)
		try {
			val pathSegments: Deque<String> = ArrayDeque()
			val path = StringBuilder(150)
			while (reader.hasNext()) {
				when (reader.next()) {
					XMLStreamConstants.START_ELEMENT -> pathSegments.push(reader.localName)
					XMLStreamConstants.CHARACTERS -> {
						path.setLength(0)
						val segment = pathSegments.descendingIterator()
						while (segment.hasNext()) {
							path.append('.').append(segment.next())
						}
						handler.processElementData(path.toString(), reader.text)
					}
					XMLStreamConstants.END_ELEMENT -> pathSegments.pop()
				}
			}
			handler.processEndDocument()
		} finally {
			reader.close()
		}
	}
}
