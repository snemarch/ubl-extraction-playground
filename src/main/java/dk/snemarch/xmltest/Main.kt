package dk.snemarch.xmltest

import org.codehaus.staxmate.SMInputFactory
import javax.xml.stream.XMLInputFactory
import com.ctc.wstx.stax.WstxInputFactory as WoodstoxImportFactory
import com.fasterxml.aalto.stax.InputFactoryImpl as AaltoInputFactory

private const val OUTERLOOPS = 10
private const val INNERLOOPS = 100_000

fun main(args: Array<String>) {
	//TODO: write tests that ensure the extractors actually work :-)

	//TODO: add invoice processing, it's a more complex document and should punish the simplistic path-segment based
	// extractor, most likely showing larger performance gains for StaxMate extraction.

	//TODO: specify number of suites and iterations on the commandline?

	//TODO: Should probably move to a proper benchmarking framework, perhaps JMH? It would be nice to have
	// one set of min/max/median timings.
	val document = loadResource("document.xml")

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
		benchmark("JRE/default", document) { ApplicationResponseExtractor(jreFactory) }
		benchmark("Woodstox/default", document) { ApplicationResponseExtractor(woodstoxFactory) }
		benchmark("Aalto/default", document) { ApplicationResponseExtractor(aaltoFactory) }
		benchmark("Woodstox/StaxMate", document) { ApplicationResponseExtractorSM(woodstoxSMFactory) }
		benchmark("Aalto/StaxMate", document) { ApplicationResponseExtractorSM(aaltoSMFactory) }
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

fun loadResource(name: String) = {}::class.java.classLoader.getResourceAsStream(name).readAllBytes()
