package dk.snemarch.xmltest;

import javax.xml.stream.XMLStreamException;
import java.io.InputStream;
import java.util.UUID;

public class ApplicationResponseExtractor extends DataExtractorBase {
	public void extractAndSave(InputStream document) throws XMLStreamException {
		ApplicationResponseContentHandler handler = new ApplicationResponseContentHandler();
		super.extractAndSave(document, handler);

		ExtraDataApplicationResponse data = handler.getData();
		data.setDocumentId(UUID.randomUUID());
	}
}
