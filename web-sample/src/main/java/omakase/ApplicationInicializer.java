package omakase;

import web.config.OmakaseWebApplicationInitializer;

public class ApplicationInicializer extends OmakaseWebApplicationInitializer {

	@Override
	public Class<?>[] configuration() {
		return new Class<?>[] { ApplicationConfig.class };
	}
}
