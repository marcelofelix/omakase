package data.jpa;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface OmakaseRepository<T, ID extends Serializable> extends JpaRepository<T, Serializable> {

	T load(ID id);
}
