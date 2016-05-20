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
package org.demoiselle.jsf.template;

import org.demoiselle.pagination.Pagination;
import org.demoiselle.util.Reflections;

import javax.faces.model.CollectionDataModel;
import javax.faces.model.DataModel;
import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * Extends {@link AbstractPageBean} by providing methods to list a collection of instances
 * of an entity bean as described in the {@link ListPageBean} interface.
 * </p>
 *
 *
 *
 *
 * Template Managed Bean class that implements the methods defined by the interface ListPageBean.
 *
 * @param <T> bean object type
 * @param <I> bean id type
 * @author SERPRO
 * @see ListPageBean
 */
public abstract class AbstractListPageBean<T, I> extends AbstractPageBean implements ListPageBean<T, I> {

	private static final long serialVersionUID = -3591592393613618687L;

	private Collection<T> resultList;

	private transient DataModel<T> dataModel;

	private Map<I, Boolean> selection = new HashMap<>();

	private Class<T> beanClass;

	@SuppressWarnings("WeakerAccess")
	public void clear() {
		this.dataModel = null;
		this.resultList = null;
	}

	protected Class<T> getBeanClass() {
		if (this.beanClass == null) {
			this.beanClass = Reflections.getGenericTypeArgument(this.getClass(), 0);
		}

		return this.beanClass;
	}

	@Override
	public DataModel<T> getDataModel() {
		if (this.dataModel == null) {
			this.dataModel = new CollectionDataModel<>(getResultList());
		}

		return this.dataModel;
	}

	@Override
	public Collection<T> getResultList() {
		if (this.resultList == null) {
			this.resultList = handleResultList();
		}

		return this.resultList;
	}

	protected abstract Collection<T> handleResultList();

	@Override
	public void list() {
		clear();
	}

	public Map<I, Boolean> getSelection() {
		return selection;
	}

	public void setSelection(Map<I, Boolean> selection) {
		this.selection = selection;
	}

	@SuppressWarnings("unused")
	public void clearSelection() {
		this.selection = new HashMap<I, Boolean>();
	}

	public Collection<I> getSelected() {
		return getSelection().keySet()
				.stream()
				.filter(id -> getSelection().get(id))
				.collect(Collectors.toList());
	}

}
