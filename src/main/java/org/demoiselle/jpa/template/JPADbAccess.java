package org.demoiselle.jpa.template;

import org.demoiselle.template.DbAccess;
import org.demoiselle.util.Reflections;
import org.hibernate.jpa.criteria.predicate.InPredicate;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Implementation of {@link DbAccess} that uses a JPA
 * persistence provider to run the underlying database commands.
 *
 * @see DbAccess
 */
public abstract class JPADbAccess <T, I> implements DbAccess <T, I> {

	private static final long serialVersionUID = -6304662620826264383L;

	private Class<T> beanClass;

	protected abstract EntityManager getEntityManager();

	private final List<I> idListCache = new ArrayList<>();

	protected Class<T> getBeanClass() {
		if (this.beanClass == null) {
			this.beanClass = Reflections.getGenericTypeArgument(this.getClass(), 0);
		}

		return this.beanClass;
	}

	@Override
	public void persist(T entity) {
		getEntityManager().persist(entity);
	}

	@Override
	public void remove(I id) {
		T entity = getEntityManager().getReference(getBeanClass(), id);
		getEntityManager().remove(entity);
	}

	@Override
	@SuppressWarnings("unchecked")
	public int remove(List<I> ids) {
		Class idClass = null;
		if (!ids.isEmpty()) {
			I id = ids.get(0);
			idClass = id.getClass();
		}

		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaDelete<T> criteriaDelete = builder.createCriteriaDelete(getBeanClass());

		Root<T> typeRoot = criteriaDelete.from(getBeanClass());
		SingularAttribute<T, I> idAttribute = typeRoot.getModel().getId(idClass);
		Query removeQuery = getEntityManager().createQuery(criteriaDelete.where(typeRoot.get(idAttribute).in(ids)));

		return removeQuery.executeUpdate();
	}

	@Override
	@SuppressWarnings(value = "unchecked")
	public int remove(I... ids) {
		if (ids == null) {
			throw new IllegalArgumentException("'ids' can't be null");
		}

		Class idClass = null;
		if (ids.length > 0) {
			I id = ids[0];
			idClass = id.getClass();
		}

		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaDelete<T> criteriaDelete = builder.createCriteriaDelete(getBeanClass());

		Root<T> typeRoot = criteriaDelete.from(getBeanClass());
		SingularAttribute<T, I> idAttribute = typeRoot.getModel().getId(idClass);
		Query removeQuery = getEntityManager().createQuery(criteriaDelete.where(typeRoot.get(idAttribute).in(ids)));

		return removeQuery.executeUpdate();
	}

	@Override
	public T merge(T entity) {
		return getEntityManager().merge(entity);
	}

	@Override
	public T load(I id) {
		return getEntityManager().find(getBeanClass(), id);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<T> loadList(List<I> ids) {
		Class idClass = null;
		if (!ids.isEmpty()) {
			I id = ids.get(0);
			idClass = id.getClass();
		}

		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<T> criteria = builder.createQuery(getBeanClass());

		Root<T> typeRoot = criteria.from(getBeanClass());
		SingularAttribute<T, I> idAttribute = typeRoot.getModel().getId(idClass);
		TypedQuery<T> listQuery = getEntityManager().createQuery(criteria.where(typeRoot.get(idAttribute).in(ids)));

		return listQuery.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<T> loadList(I... ids) {
		if (ids == null) {
			throw new IllegalArgumentException("'ids' can't be null");
		}

		Class idClass = null;
		if (ids.length > 0) {
			I id = ids[0];
			idClass = id.getClass();
		}

		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<T> criteria = builder.createQuery(getBeanClass());

		Root<T> typeRoot = criteria.from(getBeanClass());
		SingularAttribute<T, I> idAttribute = typeRoot.getModel().getId(idClass);
		TypedQuery<T> listQuery = getEntityManager().createQuery(criteria.where(typeRoot.get(idAttribute).in(ids)));

		return listQuery.getResultList();
	}

	@Override
	public List<T> listAll() {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();

		TypedQuery<T> query = getEntityManager().createQuery( builder.createQuery(getBeanClass()) );
		return query.getResultList();
	}
}
