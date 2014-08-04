package web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Configuration
public class WebConfig {

	@Bean
	public HandlerAdapter handlerAdapter() {
		RequestMappingHandlerAdapter handlerAdapter = new RequestMappingHandlerAdapter();
		handlerAdapter.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		handlerAdapter.getMessageConverters().add(new StringHttpMessageConverter());
		handlerAdapter.getMessageConverters().add(new FormHttpMessageConverter());
		handlerAdapter.getMessageConverters().add(new AllEncompassingFormHttpMessageConverter());
		handlerAdapter.getMessageConverters().add(new ByteArrayHttpMessageConverter());
		handlerAdapter.setWebBindingInitializer(webBindingInitializer());
		return handlerAdapter;
	}

	@Bean
	public ConfigurableWebBindingInitializer webBindingInitializer() {
		ConfigurableWebBindingInitializer webBindingInitializer = new ConfigurableWebBindingInitializer();
		webBindingInitializer.setValidator(validator());
		return webBindingInitializer;
	}

	@Bean
	public HandlerMapping handlerMapping() {
		return new RequestMappingHandlerMapping();
	}

	@Bean
	public Validator validator() {
		return new LocalValidatorFactoryBean();
	}

	@Bean
	public HandlerExceptionResolver exceptionResolvers() {
		return new OmakaseHandlerExceptionResolver();
	}

}
