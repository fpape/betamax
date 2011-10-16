package betamax.proxy.owasp

import betamax.util.servlet.MockServletInputStream
import javax.servlet.ServletOutputStream
import org.apache.commons.collections.iterators.IteratorEnumeration
import betamax.encoding.*

import spock.lang.*
import org.owasp.proxy.http.MutableBufferedRequest
import org.owasp.proxy.http.MutableBufferedResponse
import org.owasp.proxy.http.NamedValue

class RequestImplSpec extends Specification {

	MutableBufferedRequest owaspRequest = Mock(MutableBufferedRequest)
	MutableBufferedResponse owaspResponse = Mock(MutableBufferedResponse)

	def "request can read basic fields"() {
		given:
		owaspRequest.getTarget() >> new InetSocketAddress("robfletcher.github.com", 80)
		owaspRequest.getMethod() >> "GET"
		owaspRequest.getResource() >> "/betamax"

		and:
		def request = new RequestImpl(owaspRequest)

		expect:
		request.method == "GET"
		request.uri == new URI("http://robfletcher.github.com/betamax")
	}

	def "request target includes query string"() {
		given:
		owaspRequest.getTarget() >> new InetSocketAddress("robfletcher.github.com", 80)
		owaspRequest.getMethod() >> "GET"
		owaspRequest.getResource() >> "/betamax?q=1"

		and:
		def request = new RequestImpl(owaspRequest)

		expect:
		request.uri == new URI("http://robfletcher.github.com/betamax?q=1")
	}

	def "request can read headers"() {
		given:
		owaspRequest.getHeaders() >> ([new NamedValue("If-None-Match", " *: *", "abc123"), new NamedValue("Accept-Encoding", " *: *", "gzip, deflate")] as NamedValue[])

		and:
		def request = new RequestImpl(owaspRequest)

		expect:
		request.getHeader("If-None-Match") == "abc123"
		request.getHeader("Accept-Encoding") == "gzip, deflate"
	}

	def "request headers are immutable"() {
		given:
		def request = new RequestImpl(owaspRequest)

		when:
		request.headers["If-None-Match"] = ["abc123"]

		then:
		thrown UnsupportedOperationException
	}

	def "request body is readable as text"() {
		given:
		owaspRequest.getContent() >> "value=\u00a31".getBytes("ISO-8859-1")
		owaspRequest.getDecodedContent() >> "value=\u00a31".getBytes("ISO-8859-1")
		owaspRequest.getHeader("Content-Type") >> "application/x-www-form-urlencoded; charset=ISO-8859-1"
		owaspRequest.getHeader("Content-Length") >> 8

		and:
		def request = new RequestImpl(owaspRequest)

		expect:
		request.hasBody()
		request.bodyAsText.text == "value=\u00a31"
	}

	def "request body is readable as binary"() {
		given:
		owaspRequest.getContent() >> "value=\u00a31".getBytes("ISO-8859-1")
		owaspRequest.getHeader("Content-Type") >> "application/x-www-form-urlencoded; charset=ISO-8859-1"
		owaspRequest.getHeader("Content-Length") >> 8

		and:
		def request = new RequestImpl(owaspRequest)

		expect:
		request.hasBody()
		request.bodyAsBinary.bytes == "value=\u00a31".getBytes("ISO-8859-1")
	}

	def "response can read basic fields"() {
		given:
		def response = new ResponseImpl(owaspResponse)

		when: "status and reason are set"
		response.status = 200

		then: "the underlying servlet response values are set"
		1 * owaspResponse.setStatus("200")
	}

	def "response can add and read headers"() {
		given:
		def response = new ResponseImpl(owaspResponse)

		when: "headers are added"
		response.addHeader("E-Tag", "abc123")
		response.addHeader("Vary", "Content-Language")
		response.addHeader("Vary", "Content-Type")

		then: "they are added to the underlying servlet response"
		1 * owaspResponse.addHeader("E-Tag", "abc123")
		1 * owaspResponse.addHeader("Vary", "Content-Language")
		1 * owaspResponse.addHeader("Vary", "Content-Type")
	}

	def "response headers are immutable"() {
		given:
		def response = new ResponseImpl(owaspResponse)

		when:
		response.headers["E-Tag"] = ["abc123"]

		then:
		thrown UnsupportedOperationException
	}

	def "response reports having no body before it is written to"() {
		given:
		def response = new ResponseImpl(owaspResponse)

		expect:
		!response.hasBody()
	}

//	@Unroll("response body can be written to and read from as #charset text")
//	def "response body can be written to and read from as text"() {
//		given: "the underlying servlet response writer"
//		def servletOutputStream = new ByteArrayOutputStream()
//		owaspResponse.getOutputStream() >> new ServletOutputStream() {
//			@Override
//			void write(int b) {
//				servletOutputStream.write(b)
//			}
//		}
//
//		and: "the underlying response charset has been set"
//		owaspResponse.getCharacterEncoding() >> charset
//
//		and:
//		def response = new ResponseImpl(owaspResponse)
//
//		when: "the response is written to"
//		response.writer.withWriter {
//			it << "O HAI! \u00a31 KTHXBYE"
//		}
//
//		then: "the content can be read back as text"
//		response.hasBody()
//		response.bodyAsText.text == "O HAI! \u00a31 KTHXBYE"
//
//		and: "the underlying servlet response is written to"
//		servletOutputStream.toByteArray() == "O HAI! \u00a31 KTHXBYE".getBytes(charset)
//
//		where:
//		charset << ["ISO-8859-1", "UTF-8"]
//	}
//
//	def "response body can be written to and read from as binary"() {
//		given: "the underlying servlet response output stream"
//		def servletOutputStream = new ByteArrayOutputStream()
//		owaspResponse.getOutputStream() >> new ServletOutputStream() {
//			@Override
//			void write(int b) {
//				servletOutputStream.write(b)
//			}
//		}
//
//		and:
//		def response = new ResponseImpl(owaspResponse)
//
//		when: "the response is written to"
//		response.outputStream.withStream {
//			it << "O HAI! \u00a31 KTHXBYE".getBytes("ISO-8859-1")
//		}
//
//		then: "the content can be read back as binary data"
//		response.hasBody()
//		response.bodyAsBinary.bytes == "O HAI! \u00a31 KTHXBYE".getBytes("ISO-8859-1")
//
//		and: "the underlying servlet response is written to"
//		servletOutputStream.toByteArray() == "O HAI! \u00a31 KTHXBYE".getBytes("ISO-8859-1")
//	}
//
//	@Unroll("#encoding encoded response body with #charset charset can be written to and read from")
//	def "encoded response body can be written to and read from"() {
//		given: "the underlying servlet response output stream"
//		def servletOutputStream = new ByteArrayOutputStream()
//		owaspResponse.getOutputStream() >> new ServletOutputStream() {
//			@Override
//			void write(int b) {
//				servletOutputStream.write(b)
//			}
//		}
//
//		and: "the underlying response charset has been set"
//		owaspResponse.getCharacterEncoding() >> charset
//
//		and:
//		def response = new ResponseImpl(owaspResponse)
//
//		when: "the response is written to"
//		response.addHeader("Content-Encoding", encoding)
//		response.writer.withWriter {
//			it << "O HAI! \u00a31 KTHXBYE"
//		}
//
//		then: "the content can be read back as encoded binary data"
//		response.bodyAsBinary.bytes == encoder.encode("O HAI! \u00a31 KTHXBYE", charset)
//
//		then: "the content can be read back as text"
//		response.bodyAsText.text == "O HAI! \u00a31 KTHXBYE"
//
//		and: "the underlying servlet response is written to"
//		servletOutputStream.toByteArray() == encoder.encode("O HAI! \u00a31 KTHXBYE", charset)
//
//		where:
//		encoding  | encoder              | charset
//		"gzip"    | new GzipEncoder()    | "ISO-8859-1"
//		"deflate" | new DeflateEncoder() | "ISO-8859-1"
//		"gzip"    | new GzipEncoder()    | "UTF-8"
//		"deflate" | new DeflateEncoder() | "UTF-8"
//	}

}
