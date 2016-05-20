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
package org.demoiselle.internal.producer;

import org.demoiselle.annotation.Name;
import org.demoiselle.annotation.Type;
import org.demoiselle.annotation.literal.NamedQualifier;
import org.demoiselle.internal.configuration.PaginationConfig;
import org.demoiselle.internal.implementation.PaginationImpl;
import org.demoiselle.pagination.Pagination;

import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.util.TypeLiteral;
import javax.inject.Inject;
import javax.inject.Named;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * Producer that qualifies pagination objects by name. Internally a request scoped
 * concurrent {@link Map} is used to store all pagination objects for that request.
 * </p>
 *
 * @author SERPRO
 */
@Dependent
public class RequestScopedPaginationProducer {

	@Inject
	private PaginationConfig config;

	@Produces
	@Name
	public Pagination getPagination(InjectionPoint ip) {
		// Requer o escopo RequestScoped ativo
		try {
			CDI.current().getBeanManager().getContext(RequestScoped.class);
		} catch (ContextNotActiveException ce) {
			return null;
		}

		Name nameQualifier = null;
		Pagination pagination = null;

		for (Annotation annotation : ip.getQualifiers()) {
			if (annotation.annotationType().isAssignableFrom(Name.class)) {
				nameQualifier = (Name) annotation;
				break;
			}
		}

		if (nameQualifier != null) {
			Map<String, Pagination> paginationCache = getPaginationCache();
			pagination = paginationCache.get(nameQualifier.value());

			if (pagination == null) {
				pagination = new PaginationImpl();
				pagination.setPageSize(config.getPageSize());

				paginationCache.put(nameQualifier.value(), pagination);
			}
		}

		return pagination;
	}

	@Produces
	@Type
	public Pagination getTypedPagination(InjectionPoint ip) {
		// Requer o escopo RequestScoped ativo
		try {
			CDI.current().getBeanManager().getContext(RequestScoped.class);
		} catch (ContextNotActiveException ce) {
			return null;
		}

		Type typeQualifier = null;
		Pagination pagination = null;

		for (Annotation annotation : ip.getQualifiers()) {
			if (annotation.annotationType().isAssignableFrom(Type.class)) {
				typeQualifier = (Type) annotation;
				break;
			}
		}

		if (typeQualifier != null) {
			Map<Class, Pagination> paginationCache = getTypedPaginationCache();
			pagination = paginationCache.get(typeQualifier.value());

			if (pagination == null) {
				pagination = new PaginationImpl();
				pagination.setPageSize(config.getPageSize());

				paginationCache.put(typeQualifier.value(), pagination);
			}
		}

		return pagination;
	}

	@SuppressWarnings("serial")
	private Map<String, Pagination> getPaginationCache() {
		TypeLiteral<Map<String, Pagination>> mapTypeLiteral = new TypeLiteral<Map<String, Pagination>>() {

		};
		return CDI.current().select(mapTypeLiteral, new NamedQualifier("demoiselle-request-scoped-pagination-cache"))
				.get();
	}

	@SuppressWarnings("serial")
	private Map<Class, Pagination> getTypedPaginationCache() {
		TypeLiteral<Map<Class, Pagination>> mapTypeLiteral = new TypeLiteral<Map<Class, Pagination>>() {

		};
		return CDI.current()
				.select(mapTypeLiteral, new NamedQualifier("demoiselle-request-scoped-class-pagination-cache")).get();
	}

	@Produces
	@RequestScoped
	@Named("demoiselle-request-scoped-pagination-cache")
	protected static Map<String, Pagination> createCache() {
		return new ConcurrentHashMap<>();
	}

	@Produces
	@RequestScoped
	@Named("demoiselle-request-scoped-class-pagination-cache")
	protected static Map<Class, Pagination> createTypedCache() {
		return new ConcurrentHashMap<>();
	}

}
