package dk.snemarch.xmltest

import com.ctc.wstx.stax.WstxInputFactory
import com.fasterxml.aalto.stax.InputFactoryImpl
import org.codehaus.staxmate.`in`.SMEvent
import org.codehaus.staxmate.`in`.SMFilter
import org.codehaus.staxmate.`in`.SMInputCursor
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.XMLStreamConstants

object Utilities {
	fun woodstoxFactory(): XMLInputFactory = WstxInputFactory.newFactory().apply {
		setProperty(XMLInputFactory.IS_COALESCING, true)
		setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false)
	}

	fun aaltoFactory(): XMLInputFactory = InputFactoryImpl().apply {
		//NOTE: calling AaltoInputFactory.newFactory creates a Woodstox factory if Woodstox is on the classpath,
		//we need a direct constructor invocation to get the Aalto factory!
		setProperty(XMLInputFactory.IS_COALESCING, true)
		setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false)
	}

	fun loadResource(name: String) = {}::class.java.classLoader.getResourceAsStream(name).readAllBytes()
}

class NamedElementFilter(private val name: String):	SMFilter() {
	override fun accept(evt: SMEvent, caller: SMInputCursor) = when(evt.eventCode) {
		XMLStreamConstants.START_ELEMENT, XMLStreamConstants.END_ELEMENT -> caller.localName == name
		else -> false
	}
}
