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
package org.demoiselle.template;

import org.demoiselle.pagination.Pagination;
import org.demoiselle.util.Reflections;

import javax.enterprise.inject.spi.CDI;
import java.util.List;

/**
 * <p>
 *     Delegator meant to be used by business Java beans. This class
 *     delegates common CRUD operations on entities to an implementation
 *     of {@link DatabaseAccess}, easing the creation of business classes
 *     by only requiring the implementation of specific business logic.
 * </p>
 *
 * @param <T>    Type of business entity.
 * @param <I>    Type of the business entity's primary key.
 * @param <C>    The {@link DatabaseAccess} subclass to delegate operations to.
 *
 * @see DatabaseAccess
 * @author SERPRO
 */
public class DatabaseDelegator<T, I, C extends DatabaseAccess<T, I>> implements DatabaseAccess<T, I> {

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
	public List<T> loadList(Pagination paginationInfo) {
		return getDelegate().loadList(paginationInfo);
	}

	@Override
	public List<T> listAll() {
		return getDelegate().listAll();
	}
}
