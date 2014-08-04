package data.jpa;

import org.springframework.core.Ordered;

public interface RepositoryFilter extends Ordered {

	void applay(Object object);
}
