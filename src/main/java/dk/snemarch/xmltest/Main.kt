package dk.snemarch.xmltest

private const val ITERATIONS = 100_000

fun main(args: Array<String>) {
	//Performance baselines for 100k iterations:
	// JRE11 default:     7562ms
	// Woodstox 6.2.1:    3111ms
	// Aalto 1.2.2:       2956ms

	val document = loadResource("document.xml")

	println("Running $ITERATIONS iterations...")
	val startTime = System.nanoTime()
	for (i in 0 until ITERATIONS) {
		val extractor = ApplicationResponseExtractor()
		extractor.extractAndSave(document.inputStream())
	}
	val endTime = System.nanoTime()
	println("Execution took ${(endTime - startTime) / 1_000_000}ms")
}

fun loadResource(name: String) = {}::class.java.classLoader.getResourceAsStream(name).readAllBytes()
