package dk.snemarch.xmltest

interface BaseContentHandler {
	fun processElementData(path: String, value: String)
	fun processEndDocument()
}
