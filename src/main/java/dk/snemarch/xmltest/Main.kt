package dk.snemarch.xmltest

private const val ITERATIONS = 500_000

fun main(args: Array<String>) {
	//Performance baselines for 500k iterations:
	// JRE11 default:   27519ms (com.sun.xml.internal.stream.XMLInputFactoryImpl)
	// Woodstox 6.2.1:  9907ms (com.ctc.wstx.stax.WstxInputFactory)
	// Aalto 1.2.2:     8049ms (com.fasterxml.aalto.stax.InputFactoryImpl)
	//
	// JRE11 + SM:		n/a - requires stax2
	// Woodstox + SM:	11237ms
	// Aalto + SM:		5107ms

	//TODO: write tests that ensure the extractors actually work :-)

	//TODO: it might be possible to include both Woodstox and Aalto and select the factory at runtime, so the test
	// harness can run through all combinations instead of requiring manual rebuilds. Refactor code to take factory
	// as constructor argument instead of having own statics (should do this anyway!). If Aalto and Woodstox factories
	// can be constructed directly, the system-default implementation can still be reached with newDefaultFactory().

	//TODO: add invoice processing, it's a more complex document and should punish the simplistic path-segment based
	// extractor, most likely showing larger performance gains for StaxMate extraction.

	//TODO: perform warmup phase to ensure CPU isn't running in power savings mode. Or perhaps set up JMH?
	val document = loadResource("document.xml")

	println("Running $ITERATIONS iterations...")
	val startTime = System.nanoTime()
	for (i in 0 until ITERATIONS) {
		val extractor = ApplicationResponseExtractorSM()
		extractor.extract(document.inputStream())
	}
	val endTime = System.nanoTime()
	println("Execution took ${(endTime - startTime) / 1_000_000}ms")
}

fun loadResource(name: String) = {}::class.java.classLoader.getResourceAsStream(name).readAllBytes()
