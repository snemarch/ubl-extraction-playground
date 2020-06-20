package dk.snemarch.xmltest

import java.time.LocalDate

class ApplicationResponseContentHandler : BaseContentHandler {
	private var numberOfDocumentResponses = 0
	var data: ExtraDataApplicationResponse? = null
		private set

	private var issueDate: LocalDate? = null
	private var responseCode: String? = null
	private var description: String? = null

	override fun processElementData(path: String, value: String) {
		when (path) {
			".ApplicationResponse.IssueDate" -> issueDate = LocalDate.parse(value)
			".ApplicationResponse.DocumentResponse" -> ++numberOfDocumentResponses // This needs to be done on START_ELEMENT instead...
			".ApplicationResponse.DocumentResponse.Response.ResponseCode" -> responseCode = value
			".ApplicationResponse.DocumentResponse.Response.Description" -> description = value
		}
	}

	override fun processEndDocument() {
		data = ExtraDataApplicationResponse(issueDate!!, responseCode!!, description!!)
		if (numberOfDocumentResponses > 1) {
//            System.err.println("Document had " + numberOfDocumentResponses + " DocumentResponses, expected only 1");
		}
	}
}
