package dk.snemarch.xmltest;

import java.time.LocalDate;
import java.util.UUID;

public class ApplicationResponseContentHandler implements BaseContentHandler {
	private int numberOfDocumentResponses = 0;

	private ExtraDataApplicationResponse data;
	private LocalDate issueDate;
	private String responseCode;
	private String description;


	public ExtraDataApplicationResponse getData() {
		return data;
	}

	@Override
	public void processElementData(String path, String value) {
		switch (path) {
			case ".ApplicationResponse.IssueDate":
				issueDate = LocalDate.parse(value);
				break;

            case ".ApplicationResponse.DocumentResponse":
//				This needs to be done on START_ELEMENT instead...
				++numberOfDocumentResponses;
                break;

			case ".ApplicationResponse.DocumentResponse.Response.ResponseCode":
				responseCode = value;
				break;

			case ".ApplicationResponse.DocumentResponse.Response.Description":
				description = value;
				break;

			default:
				break;
		}
	}

	@Override
	public void processEndDocument() {
		data = new ExtraDataApplicationResponse(UUID.randomUUID(), issueDate, responseCode, description);

        if(numberOfDocumentResponses > 1) {
//            System.err.println("Document had " + numberOfDocumentResponses + " DocumentResponses, expected only 1");
        }
	}
}
