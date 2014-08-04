package test.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import aleph.ChainPersistenceProvider;
import aleph.ContextUtil;

@Configuration
public class DefaultTestConfig {

	@Bean
	public ContextUtil contextUtil() {
		return new ContextUtil();
	}

	@Bean(destroyMethod="")
	public ChainPersistenceProvider persistenceProvider() {
		return new ChainPersistenceProvider();
	}
}
