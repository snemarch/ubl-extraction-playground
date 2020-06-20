package dk.snemarch.xmltest

import org.codehaus.staxmate.SMInputFactory
import javax.xml.stream.XMLInputFactory

private const val OUTERLOOPS = 10
private const val INNERLOOPS = 100_000

fun main(args: Array<String>) {
	//TODO: specify number of suites and iterations on the commandline?

	//TODO: Should probably move to a proper benchmarking framework, perhaps JMH? It would be nice to have
	// one set of min/max/median timings.
	val applicationResponse = Utilities.loadResource("ApplicationResponse.xml")
	val invoice = Utilities.loadResource("Invoice.xml")

	val jreFactory = XMLInputFactory.newDefaultFactory()
	val woodstoxFactory = Utilities.woodstoxFactory()
	val aaltoFactory = Utilities.aaltoFactory()
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
		benchmark("INV Woodstox/StaxMate", invoice) { InvoiceExtractorSM(woodstoxSMFactory) }
		benchmark("INV Aalto/StaxMate", invoice) { InvoiceExtractorSM(aaltoSMFactory) }
	}
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
