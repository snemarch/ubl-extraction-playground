package dk.snemarch.xmltest

import org.codehaus.staxmate.SMInputFactory
import org.codehaus.staxmate.`in`.SMInputCursor
import java.io.InputStream
import java.time.LocalDate

/*
Structure we're interested in:

ApplicationResponse			[1..1]
	IssueDate				[1..1]
	DocumentResponse		[0..*]
		Response			[1..1]
			ResponseCode	[0..1]
			Description		[0..*]
 */
class ApplicationResponseExtractorSM(private val factory: SMInputFactory) : DataExtractor<ExtraDataApplicationResponse> {
	companion object {
		private val responseFilter = NamedElementFilter("Response")
	}

	private var numDocumentResponses: Int = 0
	private var description: String? = null
	private var responseCode: String? = null
	private var issueDate: LocalDate? = null

	override fun extract(document: InputStream): ExtraDataApplicationResponse {
		val root = factory.rootElementCursor(document).advance()
		try {
			val child = root.childElementCursor()
			while (child.next != null) {
				when (child.localName) {
					"IssueDate" -> issueDate = LocalDate.parse(child.elemStringValue)

					"DocumentResponse" ->
						// Response is [1..1], so we don't have to iterate but can just advance() into children.
						handleResponses(child.childCursor(responseFilter).advance().childElementCursor())
				}
			}
		} finally {
			root.streamReader.closeCompletely()
		}

		if (numDocumentResponses > 1) {
			System.err.println("Document had $numDocumentResponses DocumentResponses, expected only 1")
		}

		return ExtraDataApplicationResponse(
			issueDate = issueDate!!,
			responseCode = responseCode!!,
			description = description!!
		)
	}

	private fun handleResponses(response: SMInputCursor) {
		++numDocumentResponses
		while (response.next != null) {
			when (response.localName) {
				"ResponseCode" -> responseCode = response.elemStringValue
				"Description" -> description = response.elemStringValue
			}
		}
	}
}
