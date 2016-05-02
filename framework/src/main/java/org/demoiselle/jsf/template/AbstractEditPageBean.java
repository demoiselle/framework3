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

import org.demoiselle.annotation.Name;
import org.demoiselle.exception.DemoiselleException;
import org.demoiselle.jsf.util.Parameter;
import org.demoiselle.util.Reflections;
import org.demoiselle.util.ResourceBundle;

import javax.enterprise.inject.spi.CDI;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;

/**
 * <p>
 * Extends {@link AbstractPageBean} by providing methods to automatically handle common CRUD
 * operations described in {@link EditPageBean} and then navigate to the next view on
 * the navigation hierarchy.
 * </p>
 *
 * <p>
 * Pages that navigate to a page controlled by this class can pass a request parameter
 * called <code>id</code> with a String representation of an entity's identifier. If such
 * parameter exists when this bean is instantiated then the user implemented method
 * {@link #handleLoad(I id)} will be called passing the already converted value of this
 * parameter. The user then has to implement {@link #handleLoad(I id)} to load this entity
 * from the persistence layer using the value of <code>id</code> as the unique identifier.
 * </p>
 *
 * <p>
 * From there the returned bean will be managed by this class and can be accessed from other
 * methods by calling {@link #getBean()}. One such use is when implementing the CRUD pattern action
 * methods {@link #insert()}, {@link #delete()} and {@link #update()}, the user needs only to call
 * {@link #getBean()} to access the current instance of the bean on that scope.
 * </p>
 *
 * <p>
 * If a page controlled by this class is called without the <code>id</code> request parameter
 * then it assumes a new instance of the bean is being created. In this case
 * {@link #handleLoad(I id)} won't be called and {@link #getBean()} will create a new, empty instance
 * of the bean the first time it's called, but will then always return this same instance while
 * the current instance of this controller is on scope (assuming the concrete implementation
 * defined a scope such as {@link javax.enterprise.context.RequestScoped}).
 * </p>
 *
 * @param <T> Type of the bean handled by this implementation.
 * @param <I> Type of the primary identification key of the bean, used by the persistence layer to
 *           handle loading and persisting tasks.
 * @author SERPRO
 * @see EditPageBean
 */
public abstract class AbstractEditPageBean<T, I> extends AbstractPageBean implements EditPageBean<T> {

	private static final long serialVersionUID = 1L;

	@Inject
	private Parameter<String> id;

	private T bean;

	private Class<T> beanClass;

	private Class<I> idClass;

	@Inject
	@Name("demoiselle-jsf-bundle")
	private ResourceBundle bundle;

	protected void clear() {
		this.id = null;
		this.bean = null;
	}

	protected T createBean() {
		return CDI.current().select(getBeanClass()).get();
	}

	@Override
	public T getBean() {
		if (this.bean == null) {
			initBean();
		}

		return this.bean;
	}

	protected Class<T> getBeanClass() {
		if (this.beanClass == null) {
			this.beanClass = Reflections.getGenericTypeArgument(this.getClass(), 0);
		}

		return this.beanClass;
	}

	protected Class<I> getIdClass() {
		if (this.idClass == null) {
			this.idClass = Reflections.getGenericTypeArgument(this.getClass(), 1);
		}

		return this.idClass;
	}

	@Override
	@SuppressWarnings("unchecked")
	public I getId() {
		Converter converter = getIdConverter();

		if (converter == null && String.class.equals(getIdClass())) {
			return (I) id.getValue();

		} else if (converter == null) {
			throw new DemoiselleException(bundle.getString("id-converter-not-found", getIdClass().getCanonicalName()));

		} else {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			return (I) converter.getAsObject(facesContext, facesContext.getViewRoot(), id.getValue());
		}
	}

	private Converter getIdConverter() {
		Converter result;

		try {
			Application application = FacesContext.getCurrentInstance().getApplication();
			result = application.createConverter(getIdClass());
		} catch (Exception e) {
			result = null;
		}

		return result;
	}

	protected abstract T handleLoad(final I id);

	private void initBean() {
		if (isUpdateMode()) {
			this.bean = this.loadBean();
		} else {
			setBean(createBean());
		}
	}

	@Override
	public boolean isUpdateMode() {
		return getId() != null;
	}

	private T loadBean() {
		return handleLoad(getId());
	}

	protected void setBean(final T bean) {
		this.bean = bean;
	}
}
