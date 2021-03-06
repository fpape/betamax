package betamax.util.servlet

import org.apache.commons.collections.iterators.*

abstract class AbstractMockServletMessage {

	String characterEncoding
	String contentType
	Locale locale
	byte[] body

	private final Map<String, List<String>> headers = [:]

	final long getDateHeader(String name) {
		return 0  //To change body of implemented methods use File | Settings | File Templates.
	}

	final String getHeader(String name) {
		headers[name]?.join(", ")
	}

	final Enumeration getHeaders(String name) {
		def itr = headers.containsKey(name) ? headers[name].iterator() : EmptyIterator.INSTANCE
		new IteratorEnumeration(itr)
	}

	final Enumeration getHeaderNames() {
		def itr = headers.keySet().iterator()
		new IteratorEnumeration(itr)
	}

	final int getIntHeader(String name) {
		getHeader(name)?.toInteger() ?: -1
	}

	final boolean containsHeader(String name) {
		headers.containsKey(name)
	}

	final void setDateHeader(String name, long date) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	final void addDateHeader(String name, long date) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	final void setHeader(String name, String value) {
		headers[name] = [value]
	}

	final void addHeader(String name, String value) {
		if (headers.containsKey(name)) {
			headers[name] << value
		} else {
			setHeader(name, value)
		}
	}

	final void setIntHeader(String name, int value) {
		setHeader(name, value.toString())
	}

	final void addIntHeader(String name, int value) {
		addHeader(name, value.toString())
	}

}
