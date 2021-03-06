package omakase;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static java.util.Arrays.asList;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import web.config.WebConfig;
import web.security.HttpSecurityConfigurer;
import web.security.SecurityConfig;

@Configuration
@ComponentScan(basePackages = "omakase")
@Import({ WebConfig.class, SecurityConfig.class })
@EnableTransactionManagement(proxyTargetClass = true)
/**@EnableJpaRepositories( basePackages = "put your package here", repositoryFactoryBeanClass = OmakaseRepositoryFactoryBean.class)*/
public class ApplicationConfig {

	@Bean
	public UserDetailsService userDetailsService() {
		List<UserDetails> users = new ArrayList<UserDetails>();
		// password = password
		users.add(new User("Username", "5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8", asList(new SimpleGrantedAuthority("USER"))));
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

	@Bean
	public EntityManagerFactory entityManagerFactory() {
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		vendorAdapter.setGenerateDdl(true);
		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setDataSource(dataSource());
		factory.setLoadTimeWeaver(new InstrumentationLoadTimeWeaver());
		factory.setPackagesToScan("br.com.bc.model");
		factory.setPersistenceProviderClass(HibernatePersistenceProvider.class);
		factory.setJpaProperties(jpaProperties());
		factory.afterPropertiesSet();
		factory.setJpaVendorAdapter(vendorAdapter);
		return factory.getObject();
	}

	@Bean
	public TransactionTemplate template() {
		return new TransactionTemplate(transactionManager());
	}

	@Bean(name = "transactionManager")
	public JpaTransactionManager transactionManager() {
		return new JpaTransactionManager(entityManagerFactory());
	}

}
