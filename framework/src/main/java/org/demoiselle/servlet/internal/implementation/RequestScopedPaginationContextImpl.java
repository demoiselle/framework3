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
package org.demoiselle.servlet.internal.implementation;

import org.demoiselle.internal.configuration.PaginationConfig;
import org.demoiselle.internal.implementation.PaginationImpl;
import org.demoiselle.pagination.Pagination;
import org.demoiselle.pagination.PaginationContext;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.spi.CDI;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * Context implementation reserved for pagination purposes. Internally a hash map is used to store pagination data for
 * each class type.
 * </p>
 * 
 * @author SERPRO
 * @see PaginationContext
 */
@RequestScoped
public class RequestScopedPaginationContextImpl implements Serializable, PaginationContext {

	private static final long serialVersionUID = 1L;

	private PaginationConfig config;

	private final Map<Class<?>, Pagination> cache = new ConcurrentHashMap<>();

	public RequestScopedPaginationContextImpl() {}

	public Pagination getPagination(final Class<?> clazz) {
		return this.getPagination(clazz, false);
	}

	public Pagination getPagination(final Class<?> clazz, final boolean create) {
		Pagination pagination = cache.get(clazz);

		if (pagination == null && create) {
			pagination = new PaginationImpl();
			pagination.setPageSize(getConfig().getPageSize());

			cache.put(clazz, pagination);
		}

		return pagination;
	}

	private PaginationConfig getConfig() {
		if (config == null) {
			config = CDI.current().select(PaginationConfig.class).get();
		}

		return config;
	}
}
