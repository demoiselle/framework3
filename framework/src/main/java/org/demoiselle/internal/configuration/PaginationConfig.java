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
package org.demoiselle.internal.configuration;

import org.demoiselle.annotation.Name;
import org.demoiselle.configuration.Configuration;

import java.io.Serializable;

/**
 * The <code>PaginationConfig</code> class provides a pagination context to be used where this type of control is
 * needed. For example: in a UI as a data grid or on a search to the database.
 *
 * @author SERPRO
 */
@Configuration(prefix = "demoiselle.pagination")
public class PaginationConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	@Name("page.size")
	private int pageSize = 10;

	@Name("page.maxlinks")
	private int maxPageLinks = 5;

	@Name("page.requestparameter")
	private String requestParameter = "pagination.page";

	/**
	 * Returns the number of rows that will be shown in a data grid.
	 *
	 * @return the value defined for the key <i>frameworkdemoiselle.pagination.page.size</i> in the
	 * <b>demoiselle.properties</b> file. If there is no value defined, returns the default value 10
	 */
	@SuppressWarnings("unused")
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * Returns the maximum number of pages to be shown as selectable options on the presentation layer.
	 *
	 * @return the value defined for the key <i>frameworkdemoiselle.pagination.page.maxlinks</i> in the
	 * <b>demoiselle.properties</b> file. If there is no value defined, returns the default value of 5.
	 */
	@SuppressWarnings("unused")
	public int getMaxPageLinks() {
		return maxPageLinks;
	}

	/**
	 * Returns the name of a request parameter that the user can set, doing that will cause any pagination
	 * objects injected to have the {@link org.demoiselle.pagination.Pagination#setCurrentPage(int)} method
	 * called with the defined value as argument for that request.
	 *
	 * @return Name of a request parameter to define the current page. Default value is "pagination.page".
	 */
	@SuppressWarnings("unused")
	public String getRequestParameter() {
		return requestParameter;
	}
}
