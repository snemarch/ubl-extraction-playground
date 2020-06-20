package dk.snemarch.xmltest

import org.codehaus.staxmate.SMInputFactory
import org.codehaus.staxmate.`in`.SMInputCursor
import java.io.InputStream
import java.time.LocalDate
import java.util.*
import javax.xml.stream.XMLInputFactory

class ApplicationResponseExtractorSM {
	companion object {
		private val FACTORY = SMInputFactory(initializeFactory())

		private fun initializeFactory(): XMLInputFactory {
			val factory = XMLInputFactory.newInstance()
			factory.setProperty(XMLInputFactory.IS_COALESCING, true)
			factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false)
			println("XMLInputFactory is " + factory.javaClass.canonicalName)
			return factory
		}
	}

	var numDocumentResponses: Int = 0
	private var description: String? = null
	private var responseCode: String? = null
	private var issueDate: LocalDate? = null

	fun extract(document: InputStream): ExtraDataApplicationResponse {
		val root = FACTORY.rootElementCursor(document).advance()
		try {
			val child = root.childElementCursor()
			while(child.next != null) {
				when(child.localName) {
					"IssueDate" -> issueDate = LocalDate.parse(child.elemStringValue)
					"DocumentResponse" -> handleDocumentResponses(child.childElementCursor())
				}
			}
		} finally {
		    root.streamReader.closeCompletely()
		}

		if(numDocumentResponses > 1) {
			System.err.println("Document had $numDocumentResponses DocumentResponses, expected only 1")
		}

		return ExtraDataApplicationResponse(UUID.randomUUID(), issueDate!!, responseCode!!, description!!)
	}

	private fun handleDocumentResponses(documentResponse: SMInputCursor) {
		++numDocumentResponses
		while(documentResponse.next != null) {
			when(documentResponse.localName) {
				"Response" -> handleResponse(documentResponse.childElementCursor())
			}
		}
	}

	private fun handleResponse(response: SMInputCursor) {
		while(response.next != null) {
			when(response.localName) {
				"ResponseCode" -> responseCode = response.elemStringValue
				"Description" -> description = response.elemStringValue
			}
		}
	}
}
