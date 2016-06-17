/*
 * Demoiselle Framework
 * Copyright (C) 2010 SERPRO
 * ----------------------------------------------------------------------------
 * This file is part of Demoiselle Framework.
 *
 * Demoiselle Framework is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License version 3
 * along with this program; if not,  see <http://www.gnu.org/licenses/>
 * or write to the Free Software Foundation, Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA  02110-1301, USA.
 * ----------------------------------------------------------------------------
 * Este arquivo é parte do Framework Demoiselle.
 *
 * O Framework Demoiselle é um software livre; você pode redistribuí-lo e/ou
 * modificá-lo dentro dos termos da GNU LGPL versão 3 como publicada pela Fundação
 * do Software Livre (FSF).
 *
 * Este programa é distribuído na esperança que possa ser útil, mas SEM NENHUMA
 * GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer MERCADO ou
 * APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral GNU/LGPL em português
 * para maiores detalhes.
 *
 * Você deve ter recebido uma cópia da GNU LGPL versão 3, sob o título
 * "LICENCA.txt", junto com esse programa. Se não, acesse <http://www.gnu.org/licenses/>
 * ou escreva para a Fundação do Software Livre (FSF) Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02111-1301, USA.
 */
package org.demoiselle.jpa.template;

import org.demoiselle.pagination.Pagination;
import org.demoiselle.template.DatabaseAccess;
import org.demoiselle.util.Reflections;

import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.inject.spi.CDI;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;
import java.util.List;

/**
 * Implementation of {@link DatabaseAccess} that uses a JPA
 * persistence provider to run the underlying database commands.
 *
 * @see DatabaseAccess
 */
@SuppressWarnings("WeakerAccess")
public abstract class JPADatabaseAccess<T, I> implements DatabaseAccess<T, I> {

	private static final long serialVersionUID = -6304662620826264383L;

	private Class<T> beanClass;

	protected abstract EntityManager getEntityManager();

	@SuppressWarnings("WeakerAccess")
	public Class<T> getBeanClass() {
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

		Pagination pagination = getPagination();
		if (pagination != null && pagination.isInitialized()) {
			listQuery.setFirstResult(pagination.getFirstResult());
			listQuery.setMaxResults(pagination.getPageSize());
		}

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

		Pagination pagination = getPagination();
		if (pagination != null && pagination.isInitialized()) {
			listQuery.setFirstResult(pagination.getFirstResult());
			listQuery.setMaxResults(pagination.getPageSize());
		}

		return listQuery.getResultList();
	}

	@Override
	public List<T> listAll() {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();

		CriteriaQuery<T> criteriaQuery = builder.createQuery(getBeanClass());
		criteriaQuery.from(getBeanClass());

		TypedQuery<T> query = getEntityManager().createQuery( criteriaQuery );

		Pagination pagination = getPagination();
		if (pagination != null && pagination.isInitialized()) {
			pagination.setTotalResults(this.countAll().intValue());

			query.setFirstResult(pagination.getFirstResult());
			query.setMaxResults(pagination.getPageSize());
		}

		return query.getResultList();
	}

	/**
	 * <p>
	 * Returns a valid pagination object to control the listing
	 * of instances of the entity controlled by this class.
	 * </p>
	 *
	 * <p>
	 * The default implementation always return <code>null</code>. If
	 * you want to paginate results returned by the methods {@link #loadList(Object[])},
	 * {@link #loadList(List)} and {@link #listAll()} then overwrite this method
	 * and return a valid pagination object.
	 * </p>
	 *
	 * @return Pagination object to control the pagination of results for this
	 * class, or <code>null</code> if no pagination should occurr.
	 */
	protected Pagination getPagination() {
		return null;
	}

	@SuppressWarnings("WeakerAccess")
	protected Number countAll() {
		CriteriaBuilder qb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Long> cq = qb.createQuery(Long.class);
		cq.select(qb.count(cq.from(getBeanClass())));

		return getEntityManager().createQuery(cq).getSingleResult();
	}
}
