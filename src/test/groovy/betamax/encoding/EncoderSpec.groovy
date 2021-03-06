package betamax.encoding

import spock.lang.*

class EncoderSpec extends Specification {

	@Unroll("#encoderClass can decode what it has encoded")
	def "can decode what it has encoded"() {
		given:
		def encoder = encoderClass.newInstance()
		def bytes = encoder.encode("this is some text that gets encoded")

		expect:
		new String(bytes) != "this is some text that gets encoded"
		encoder.decode(new ByteArrayInputStream(bytes)) == "this is some text that gets encoded"

		where:
		encoderClass << [GzipEncoder, DeflateEncoder]
	}

}
