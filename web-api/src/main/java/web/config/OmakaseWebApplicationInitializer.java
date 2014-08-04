package web.config;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

public abstract class OmakaseWebApplicationInitializer implements WebApplicationInitializer {

	public void onStartup(ServletContext container) throws ServletException {

		AnnotationConfigWebApplicationContext appContext = new AnnotationConfigWebApplicationContext();
		appContext.setConfigLocation("classpath:application-context.xml");
		appContext.register(configuration());
		container.addListener(new ContextLoaderListener(appContext));

		ServletRegistration.Dynamic registration = container.addServlet("dispatcher", new DispatcherServlet(appContext));
		registration.addMapping("/api/*");

		container.addFilter("HttpMethodFilter", org.springframework.web.filter.HiddenHttpMethodFilter.class)
				.addMappingForUrlPatterns(null, false, "/*");

		container.addFilter("springSecurityFilterChain", new DelegatingFilterProxy("springSecurityFilterChain", appContext))
				.addMappingForUrlPatterns(null, false, "/api/*");

		FilterRegistration charEncodingfilterReg = container.addFilter("CharacterEncodingFilter", CharacterEncodingFilter.class);
		charEncodingfilterReg.setInitParameter("encoding", "UTF-8");
		charEncodingfilterReg.setInitParameter("forceEncoding", "true");
		charEncodingfilterReg.addMappingForUrlPatterns(null, false, "/*");
	}

	public abstract Class<?>[] configuration();
}
