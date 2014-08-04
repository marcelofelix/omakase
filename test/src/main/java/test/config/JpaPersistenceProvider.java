package test.config;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import aleph.AbstractBuilder;
import aleph.PersistenceProvider;

public class JpaPersistenceProvider implements PersistenceProvider {
	private List<String> commands = new ArrayList<String>();
	private List<String> nativeQuery = new ArrayList<String>();

	@PersistenceContext
	private EntityManager em;

	public void save(AbstractBuilder<?> builder) {
		builder.build();
		em.persist(builder.get());

	}

	public void close() {
		em.flush();
		em.clear();
		em.close();

	}

	public JpaPersistenceProvider addQuery(String query) {
		this.commands.add(query);
		return this;
	}

	public JpaPersistenceProvider addNativeQuery(String query) {
		addQuery(query);
		this.nativeQuery.add(query);
		return this;
	}

	public void clear() {
		for (String c : commands) {
			if (nativeQuery.contains(c)) {
				em.createNativeQuery(c).executeUpdate();
			} else {
				em.createQuery(c).executeUpdate();
			}
		}
	}

}
