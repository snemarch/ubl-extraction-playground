package dk.snemarch.xmltest;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import java.io.InputStream;

public class ApplicationResponseExtractor extends DataExtractorBase implements DataExtractor<ExtraDataApplicationResponse> {
	public ApplicationResponseExtractor(XMLInputFactory factory) {
		super(factory);
	}

	public ExtraDataApplicationResponse extract(InputStream document) throws XMLStreamException {
		ApplicationResponseContentHandler handler = new ApplicationResponseContentHandler();
		super.extractAndSave(document, handler);

		ExtraDataApplicationResponse data = handler.getData();
		return data;
	}
}
