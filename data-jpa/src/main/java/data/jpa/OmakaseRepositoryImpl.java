package data.jpa;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import blackcrow.NotFound;

public class OmakaseRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, Serializable> implements
		OmakaseRepository<T, Serializable> {

	private Class<T> domainClass;

	@Autowired(required = false)
	private List<RepositoryFilter> filters;

	public OmakaseRepositoryImpl(Class<T> domainClass, EntityManager em) {
		super(domainClass, em);
		this.domainClass = domainClass;
	}

	@Override
	public T load(Serializable id) {
		T entity = findOne(id);
		applayFilters(entity);
		if (entity == null) {
			throw new NotFound()
					.addParam("Entity", domainClass.getSimpleName())
					.addParam("id", id);
		}
		return entity;
	}

	private void applayFilters(T entity) {
		if (filters != null) {
			for (RepositoryFilter rf : filters) {
				rf.applay(entity);
			}
		}
	}

}
