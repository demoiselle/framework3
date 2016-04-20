package org.demoiselle.template;

import java.io.Serializable;
import java.util.List;

/**
 * Template to access the persistence system, query for entities and persist them.
 *
 * @param <T>    Type of business entity.
 * @param <I>    Type of the business entity's primary key.
 *
 * @author SERPRO
 */
public interface DbAccess <T, I> extends Serializable {

	/**
	 * <p>
	 *     Persists an entity to the persistent storage of
	 *     the application.
	 * </p>
	 *
	 * <p>
	 *     It's up to the implementation to detect if the persisted
	 *     entity already exists on the storage and should be updated/replaced
	 *     or if it's a new instance of this entity and should be created
	 *     on the storage.
	 * </p>
	 *
	 * @param entity Entity being persisted
	 */
	void persist(T entity);

	/**
	 * <p>Removes an existing entity from the persistent storage.</p>
	 *
	 * @param id Unique identifier to find the entity to be removed.
	 */
	void remove(I id);

	/**
	 * @return The amount of entities removed.
	 * @see #remove(Object)
	 */
	int remove(List<I> ids);

	/**
	 * @return The amount of entities removed.
	 * @see #remove(Object)
	 */
	int remove(I... ids);

	/**
	 * <p>
	 *     Merges the current state of the passed entity to the storage, replacing
	 *     (and possibly creating, if such entity doesn't previously exist) the current
	 *     state of a matching entity with new data.
	 * </p>
	 *
	 * <p>
	 *     For unmanaged persistence systems like simple JDBC, this method will usually have
	 *     the same effect as {@link #persist(Object)}. For managed persistence systems like JPA
	 *     the merge operation copies data from the unmanaged entity passed to the managed entity
	 *     contained in the persistence system.
	 * </p>
	 *
	 * @param entity Copy of the entity containing new data to be merged.

	 * @return Returns the entity from the storage already synchronized with the new data.
	 */
	T merge(T entity);

	/**
	 * <p>Queries the persistent storage for an entity using it's primary key.</p>
	 *
	 * @param id    Primary key used to query this entity.
	 * @return The entity with this primary key, or <code>null</code> if no such entity exists.
	 */
	T load(I id);

	/**
	 * Load a list of entities based on a series of values for the entity's primary key.
	 *
	 * @param ids    The list of primary keys to query the entity.
	 * @return A (possibly empty) list of entities that have a matching primary key.
	 */
	List<T> loadList(List<I> ids);

	/**
	 * @see #loadList(List)
	 */
	List<T> loadList(I... ids);

	/**
	 * <p>Lists all instances of this entity contained in the persistent storage.</p>
	 *
	 * @return The list of all persisted entities of this type.
	 */
	List<T> listAll();

}
