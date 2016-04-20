package org.demoiselle.template;

import org.demoiselle.util.Reflections;

import javax.enterprise.inject.spi.CDI;
import java.util.List;

/**
 * <p>
 *     Delegator meant to be used by business Java beans. This class
 *     delegates common CRUD operations on entities to an implementation
 *     of {@link DbAccess}, easing the creation of business classes
 *     by only requiring the implementation of specific business logic.
 * </p>
 *
 * @param <T>    Type of business entity.
 * @param <I>    Type of the business entity's primary key.
 * @param <C>    The {@link DbAccess} subclass to delegate operations to.
 *
 * @see DbAccess
 * @author SERPRO
 */
public class DbDelegator <T, I, C extends DbAccess <T, I>> implements DbAccess <T, I> {

	private static final long serialVersionUID = -1137992245770826894L;

	private Class<C> delegateClass;

	private transient C delegate;

	@SuppressWarnings("WeakerAccess")
	protected Class<C> getDelegateClass() {
		if (this.delegateClass == null) {
			this.delegateClass = Reflections.getGenericTypeArgument(this.getClass(), 2);
		}

		return this.delegateClass;
	}

	@SuppressWarnings("WeakerAccess")
	protected C getDelegate() {
		if (this.delegate == null) {
			this.delegate = CDI.current().select(getDelegateClass()).get();
		}

		return this.delegate;
	}

	@Override
	public void persist(T entity) {
		getDelegate().persist(entity);
	}

	@Override
	public void remove(I id) {
		getDelegate().remove(id);
	}

	@Override
	public int remove(List<I> ids) {
		return getDelegate().remove(ids);
	}

	@Override
	@SuppressWarnings("unchecked")
	public int remove(I... ids) {
		return getDelegate().remove(ids);
	}

	@Override
	public T merge(T entity) {
		return getDelegate().merge(entity);
	}

	@Override
	public T load(I id) {
		return getDelegate().load(id);
	}

	@Override
	public List<T> loadList(List<I> ids) {
		return getDelegate().loadList(ids);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<T> loadList(I... ids) {
		return getDelegate().loadList(ids);
	}

	@Override
	public List<T> listAll() {
		return getDelegate().listAll();
	}
}
