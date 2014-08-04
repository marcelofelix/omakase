package web.config;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

import blackcrow.HttpException;
import blackcrow.HttpExceptionBuild;
import blackcrow.HttpExceptionDescriptor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import static web.security.SecurityHelper.isUserLogged;

public class OmakaseHandlerExceptionResolver implements HandlerExceptionResolver {
	private static final Logger log = LoggerFactory.getLogger(OmakaseHandlerExceptionResolver.class);

	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			final Exception ex) {
		log.debug("Exception {} message {}", ex.getClass().getSimpleName(), ex.getMessage());
		HttpException httpException;
		if (ex instanceof HttpException) {
			httpException = (HttpException) ex;
		} else if (ex instanceof NoSuchRequestHandlingMethodException) {
			httpException = new HttpExceptionBuild(ex, HttpServletResponse.SC_NOT_FOUND).build();
		} else if (ex instanceof HttpRequestMethodNotSupportedException) {
			httpException = new HttpExceptionBuild(ex, HttpServletResponse.SC_METHOD_NOT_ALLOWED)
					.detail(new HttpExceptionDescriptor() {
						public void describle(Map<String, Object> p) {
							String[] supportedMethods = ((HttpRequestMethodNotSupportedException) ex).getSupportedMethods();
							if (supportedMethods != null) {
								p.put("Allow", StringUtils.arrayToDelimitedString(supportedMethods, ", "));
							}
						}
					})
					.build();
		} else if (ex instanceof HttpMediaTypeNotSupportedException) {
			httpException = new HttpExceptionBuild(ex, HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE)
					.detail(new HttpExceptionDescriptor() {
						public void describle(Map<String, Object> p) {
							List<MediaType> mediaTypes = ((HttpMediaTypeNotSupportedException) ex).getSupportedMediaTypes();
							if (!CollectionUtils.isEmpty(mediaTypes)) {
								p.put("Accept", MediaType.toString(mediaTypes));
							}
						}
					})
					.build();
		} else if (ex instanceof HttpMediaTypeNotAcceptableException) {
			httpException = new HttpExceptionBuild(ex, HttpServletResponse.SC_NOT_ACCEPTABLE).build();
		} else if (ex instanceof MissingServletRequestParameterException) {
			httpException = new HttpExceptionBuild(ex, HttpServletResponse.SC_BAD_REQUEST).build();
		} else if (ex instanceof ServletRequestBindingException) {
			httpException = new HttpExceptionBuild(ex, HttpServletResponse.SC_BAD_REQUEST).build();
		} else if (ex instanceof ConversionNotSupportedException) {
			httpException = new HttpExceptionBuild(ex, HttpServletResponse.SC_INTERNAL_SERVER_ERROR).build();
		} else if (ex instanceof TypeMismatchException) {
			httpException = new HttpExceptionBuild(ex, HttpServletResponse.SC_BAD_REQUEST).build();
		} else if (ex instanceof HttpMessageNotReadableException) {
			httpException = new HttpExceptionBuild(ex, HttpServletResponse.SC_BAD_REQUEST).build();
		} else if (ex instanceof HttpMessageNotWritableException) {
			httpException = new HttpExceptionBuild(ex, HttpServletResponse.SC_INTERNAL_SERVER_ERROR).build();
		} else if (ex instanceof MethodArgumentNotValidException) {
			httpException = new HttpExceptionBuild(ex, HttpServletResponse.SC_BAD_REQUEST).build();
		} else if (ex instanceof MissingServletRequestPartException) {
			httpException = new HttpExceptionBuild(ex, HttpServletResponse.SC_BAD_REQUEST).build();
		} else if (ex instanceof BindException) {
			httpException = resolveBindException((BindException) ex);
		} else if (ex instanceof NoHandlerFoundException) {
			httpException = new HttpExceptionBuild(ex, HttpServletResponse.SC_NOT_FOUND).build();
		} else if (ex instanceof AccessDeniedException) {
			if (isUserLogged()) {
				httpException = new HttpExceptionBuild(ex, HttpServletResponse.SC_FORBIDDEN)
						.message("user.forbidden")
						.build();
			} else {
				httpException = new HttpExceptionBuild(ex, HttpServletResponse.SC_UNAUTHORIZED)
						.message("user.unauthorized")
						.build();
			}
		} else {
			httpException = new HttpExceptionBuild(ex, HttpServletResponse.SC_INTERNAL_SERVER_ERROR).build();
			httpException.addParam("message", ex.getLocalizedMessage());
		}
		try {
			sendError(response, httpException);
		} catch (Exception e) {
			log.error(ex.getMessage(), e);
		}
		return new ModelAndView();
	}

	private HttpException resolveBindException(final BindException ex) {
		HttpException httpException;
		httpException = new HttpException(ex.getMessage());
		httpException.setErrorCode("bad.request");
		httpException.setHttpStatus(HttpServletResponse.SC_BAD_REQUEST);
		httpException.addParam("globalErros", ex.getGlobalErrors());
		List<Map<String, Object>> fields = new ArrayList<Map<String, Object>>();
		for (FieldError f : ex.getFieldErrors()) {
			Map<String, Object> value = new HashMap<String, Object>();
			value.put("message", f.getDefaultMessage());
			value.put("field", f.getField());
			value.put("objectName", f.getObjectName());
			value.put("rejectedValue", f.getRejectedValue());
			fields.add(value);
		}
		httpException.addParam("fieldErros", fields);
		return httpException;
	}

	private void sendError(HttpServletResponse response, HttpException httpException) throws JsonProcessingException,
			IOException {
		ObjectMapper mapper = new ObjectMapper();
		String value = mapper.writeValueAsString(httpException.getParams());
		response.setStatus(httpException.getHttpStatus());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.getWriter().write(value);
		response.getWriter().flush();
	}
}
