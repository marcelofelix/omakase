package test.config;

import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MockHttpServletRequestBuilderFactory {

	protected MockHttpServletRequestBuilder requestBuilder;

	public MockHttpServletRequestBuilder create() {
		return requestBuilder;
	}

	public static String json(Object value) {
		try {
			return new ObjectMapper().writeValueAsString(value);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
}
