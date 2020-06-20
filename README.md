XMLTest
=======

A small project for exploring XML parsing speed (and memory efficiency),
specifically dealing with Tradeshift UBL documents.

Performance baseline
====================
The "old version" baselines required manual work – uncommenting the wanted StAX
implementation in the Gradle build file, uncommeting the wanted processing
(path-segment or StaxMate) version, and running. It had a lot of jitter, but
lowest numbers picked from a bunch of runs were:

Performance baselines for 500k iterations:
* JRE11 default:   27519ms (com.sun.xml.internal.stream.XMLInputFactoryImpl)
* Woodstox 6.2.1:  9907ms (com.ctc.wstx.stax.WstxInputFactory)
* Aalto 1.2.2:     8049ms (com.fasterxml.aalto.stax.InputFactoryImpl)
* JRE11 + SM:		n/a - requires stax2
* Woodstox + SM:	11237ms
* Aalto + SM:		5107ms

The "suite based" baselines should be more reliable – running is automated, so
there's no "oh, I ran the Aalto version again, not Woodstox", the JVM is warmed
up, and by running the suite several times, the CPU should be in full
performance mode, and hopefully all code paths should be JIT'ed...

Suite 10 of 10: running 100000 of each
* JRE/default: execution took 5013ms
* Woodstox/default: execution took 1920ms
* Aalto/default: execution took 1474ms
* Woodstox/StaxMate: execution took 1257ms
* Aalto/StaxMate: execution took 814ms
