package dk.snemarch.xmltest

import org.codehaus.staxmate.SMInputFactory
import javax.xml.stream.XMLInputFactory
import com.ctc.wstx.stax.WstxInputFactory as WoodstoxImportFactory
import com.fasterxml.aalto.stax.InputFactoryImpl as AaltoInputFactory

private const val OUTERLOOPS = 10
private const val INNERLOOPS = 100_000

fun main(args: Array<String>) {
	//TODO: specify number of suites and iterations on the commandline?

	//TODO: Should probably move to a proper benchmarking framework, perhaps JMH? It would be nice to have
	// one set of min/max/median timings.
	val applicationResponse = loadResource("ApplicationResponse.xml")
	val invoice = loadResource("Invoice.xml")

	val jreFactory = XMLInputFactory.newDefaultFactory()
	val woodstoxFactory = woodstoxFactory()
	val aaltoFactory = aaltoFactory()
	val woodstoxSMFactory = SMInputFactory(woodstoxFactory)
	val aaltoSMFactory = SMInputFactory(aaltoFactory)

	readerInfo(jreFactory)
	readerInfo(woodstoxFactory)
	readerInfo(aaltoFactory)

	for (i in 1 .. OUTERLOOPS) {
		println("Suite $i of $OUTERLOOPS: running $INNERLOOPS of each")
		benchmark("AR JRE/default", applicationResponse) { ApplicationResponseExtractor(jreFactory) }
		benchmark("AR Woodstox/default", applicationResponse) { ApplicationResponseExtractor(woodstoxFactory) }
		benchmark("AR Aalto/default", applicationResponse) { ApplicationResponseExtractor(aaltoFactory) }
		benchmark("AR Woodstox/StaxMate", applicationResponse) { ApplicationResponseExtractorSM(woodstoxSMFactory) }
		benchmark("AR Aalto/StaxMate", applicationResponse) { ApplicationResponseExtractorSM(aaltoSMFactory) }

		benchmark("INV JRE/default", invoice) { InvoiceExtractor(jreFactory) }
		benchmark("INV Woodstox/default", invoice) { InvoiceExtractor(woodstoxFactory) }
		benchmark("INV Aalto/default", invoice) { InvoiceExtractor(aaltoFactory) }
	}
}

private fun woodstoxFactory(): XMLInputFactory = WoodstoxImportFactory.newFactory().apply {
	setProperty(XMLInputFactory.IS_COALESCING, true)
	setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false)
}

private fun aaltoFactory(): XMLInputFactory = AaltoInputFactory().apply {
	//NOTE: calling AaltoInputFactory.newFactory creates a Woodstox factory if Woodstox is on the classpath,
	//we need a direct constructor invocation to get the Aalto factory!
	setProperty(XMLInputFactory.IS_COALESCING, true)
	setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false)
}

private fun readerInfo(factory: XMLInputFactory) {
	println("${factory.javaClass.canonicalName} produces ${factory.createXMLStreamReader(ByteArray(0).inputStream()).javaClass.canonicalName}")
}

private fun <T> benchmark(name:String, document:ByteArray, handlerSupplier: () -> DataExtractor<T>) {
	print("\t $name: ")
	val startTime = System.nanoTime()
	for (i in 0 until INNERLOOPS) {
		val extractor = handlerSupplier()
		extractor.extract(document.inputStream())
	}
	val endTime = System.nanoTime()
	println("execution took ${(endTime - startTime) / 1_000_000}ms")
}

public fun loadResource(name: String) = {}::class.java.classLoader.getResourceAsStream(name).readAllBytes()
