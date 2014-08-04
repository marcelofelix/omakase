package test.config;

import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

public class MockHttpServletRequestBuilderFactory {

	protected MockHttpServletRequestBuilder requestBuilder;

	public MockHttpServletRequestBuilder create() {
		return requestBuilder;
	}
}
