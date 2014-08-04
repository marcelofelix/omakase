package web.security;

import static web.security.SecurityHelper.isUserLogged;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.ExceptionMappingAuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired(required = false)
	private SessionCreationPolicy sessionCreationPolicy = SessionCreationPolicy.ALWAYS;

	@Autowired(required = false)
	private UserDetailsService userDetailsService;

	@Autowired(required = false)
	private HttpSecurityConfigurer httpSecurityConfigurer;

	public void setSessionCreationPolicy(SessionCreationPolicy sessionCreationPolicy) {
		this.sessionCreationPolicy = sessionCreationPolicy;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.sessionManagement().sessionCreationPolicy(sessionCreationPolicy);
		http.formLogin()
				.loginProcessingUrl("/api/login")
				.usernameParameter("username")
				.passwordParameter("password")
				.successHandler(successHandler());
		http.logout().logoutUrl("/api/logout").logoutSuccessHandler(logoutSuccessHandler());
		http.exceptionHandling()
				.authenticationEntryPoint(authenticationEntryPoint())
				.accessDeniedHandler(accessDeniedHandler());
		http.authorizeRequests().antMatchers("/api/login").anonymous();
		http.csrf().disable();
		if (httpSecurityConfigurer != null) {
			httpSecurityConfigurer.configure(http);
		}

	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.eraseCredentials(true)
				.userDetailsService(userDetailsService)
				.passwordEncoder(new ShaPasswordEncoder(256));
	}

	@Bean
	public AccessDeniedHandler accessDeniedHandler() {
		return new AccessDeniedHandler() {
			public void handle(HttpServletRequest request, HttpServletResponse response,
					AccessDeniedException accessDeniedException) throws IOException, ServletException {
				if (isUserLogged()) {
					response.sendError(HttpStatus.FORBIDDEN.value(), accessDeniedException.getMessage());
				} else {
					response.sendError(HttpStatus.UNAUTHORIZED.value(), "user.unauthorized");
				}
			}
		};
	}

	@Bean
	public LogoutSuccessHandler logoutSuccessHandler() {
		return new LogoutSuccessHandler() {
			public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
					Authentication authentication)
					throws IOException, ServletException {
				response.setStatus(HttpStatus.OK.value());
			}
		};
	}

	@Bean
	public AuthenticationEntryPoint authenticationEntryPoint() {
		return new AuthenticationEntryPoint() {
			public void commence(HttpServletRequest request, HttpServletResponse response,
					AuthenticationException authException)
					throws IOException, ServletException {
				HttpServletResponse httpResponse = (HttpServletResponse) response;
				httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
			}
		};
	}

	@Bean
	public AuthenticationFailureHandler failureHandler() {
		return new ExceptionMappingAuthenticationFailureHandler();
	}

	@Bean
	public AuthenticationSuccessHandler successHandler() {
		return new AuthenticationSuccessHandler() {
			public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
					Authentication authentication) throws IOException, ServletException {
				response.setStatus(HttpServletResponse.SC_OK);

			}
		};
	}

}
