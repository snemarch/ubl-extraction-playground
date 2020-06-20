package dk.snemarch.xmltest;

public interface BaseContentHandler {
	void processElementData(String path, String value);
	void processEndDocument();
}
