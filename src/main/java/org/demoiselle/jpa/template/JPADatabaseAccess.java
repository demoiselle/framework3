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

import org.demoiselle.template.DatabaseAccess;
import org.demoiselle.util.Reflections;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link DatabaseAccess} that uses a JPA
 * persistence provider to run the underlying database commands.
 *
 * @see DatabaseAccess
 */
public abstract class JPADatabaseAccess<T, I> implements DatabaseAccess<T, I> {

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
