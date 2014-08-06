package omakase;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import web.config.WebConfig;
import web.security.HttpSecurityConfigurer;
import web.security.SecurityConfig;

@Configuration
@ComponentScan(basePackages = "omakase")
@ImportResource("classpath:application-context.xml")
@Import({ WebConfig.class, SecurityConfig.class })
/**@EnableJpaRepositories( basePackages = "put your package here", repositoryFactoryBeanClass = OmakaseRepositoryFactoryBean.class)*/
public class ApplicationConfig {

	@Bean
	public UserDetailsService userDetailsService() {
		List<UserDetails> users = new ArrayList<UserDetails>();
		users.add(new User("Username", "password", asList(new SimpleGrantedAuthority("USER"))));
		return new InMemoryUserDetailsManager(users);
	}

	@Bean
	public HttpSecurityConfigurer httpSecurityConfigurer() {
		return new HttpSecurityConfigurer() {

			public void configure(HttpSecurity http) throws Exception {
				http.authorizeRequests().anyRequest().permitAll();
			}
		};
	}

	@Bean(name = "dataSource")
	public DataSource dataSource() {
		return new EmbeddedDatabaseBuilder().
				setType(EmbeddedDatabaseType.HSQL)
				.build();
	}

	@Bean(name = "jpaProperties")
	public Properties jpaProperties() {
		Properties p = new Properties();
		p.put("dialect", "ch.qos.logback.core.db.dialect.HSQLDBDialect");
		p.put("hibernate.format_sql", false);
		p.put("hibernate.show_sql", false);
		p.put("hibernate.hbm2ddl.auto", "update");
		return p;
	}

}
