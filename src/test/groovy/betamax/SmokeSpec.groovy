package betamax

import groovyx.net.http.RESTClient
import org.apache.http.impl.conn.ProxySelectorRoutePlanner
import org.junit.Rule
import static java.net.HttpURLConnection.HTTP_OK
import spock.lang.*
import groovyx.net.http.HttpResponseDecorator
import betamax.proxy.RecordAndPlaybackProxyInterceptor
import static betamax.proxy.RecordAndPlaybackProxyInterceptor.X_BETAMAX
import org.apache.http.HttpHeaders
import static org.apache.http.HttpHeaders.VIA

class SmokeSpec extends Specification {

	@Rule Recorder recorder = new Recorder()

	@Shared RESTClient http = new RESTClient()

	def setupSpec() {
		http.client.routePlanner = new ProxySelectorRoutePlanner(http.client.connectionManager.schemeRegistry, ProxySelector.default)
	}

	@Betamax(tape = "smoke spec")
	@Unroll("#type response data")
	def "various types of response data"() {
		when:
		HttpResponseDecorator response = http.get(uri: uri)

		then:
		response.status == HTTP_OK
		response.getFirstHeader(VIA)?.value == "Betamax"
		response.getFirstHeader(X_BETAMAX)?.value == "PLAY"

		where:
		type   | uri
		"html" | "http://grails.org/"
		"json" | "http://api.twitter.com/1/statuses/public_timeline.json?count=3&include_entities=true"
		"xml"  | "http://feeds.feedburner.com/wondermark"
		"png"  | "http://media.xircles.codehaus.org/_projects/groovy/_logos/small.png"
	}

}
