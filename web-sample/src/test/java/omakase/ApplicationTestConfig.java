package omakase;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import test.config.JpaPersistenceProvider;

@Configuration()
@EnableTransactionManagement
public class ApplicationTestConfig extends ApplicationConfig {

	@Bean(destroyMethod = "")
	public JpaPersistenceProvider jpaPersistenceProvider() {
		return new JpaPersistenceProvider();
	}

}
