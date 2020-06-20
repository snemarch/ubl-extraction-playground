package dk.snemarch.xmltest;

import static javax.xml.stream.XMLStreamConstants.CHARACTERS;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class DataExtractorBase {
	private final XMLInputFactory factory;

	public DataExtractorBase(XMLInputFactory factory) {
		this.factory = factory;
	}

	protected void extractAndSave(InputStream document, BaseContentHandler handler) throws XMLStreamException {
		XMLStreamReader reader = factory.createXMLStreamReader(document);

		try {
			Deque<String> pathSegments = new ArrayDeque<>();
			StringBuilder path = new StringBuilder(150);

			while (reader.hasNext()) {
				switch (reader.next()) {
					case START_ELEMENT:
						pathSegments.push(reader.getLocalName());
						break;

					case CHARACTERS:
						path.setLength(0);
						Iterator<String> segment = pathSegments.descendingIterator();
						while (segment.hasNext()) {
							path.append('.').append(segment.next());
						}
						handler.processElementData(path.toString(), reader.getText());

						break;

					case END_ELEMENT:
						pathSegments.pop();
						break;

					default:
						// Not interested in other element types
						break;
				}
			}

			handler.processEndDocument();
		}
		finally {
			reader.close();
		}
	}
}
