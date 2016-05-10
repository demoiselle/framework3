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
package org.demoiselle.pagination;

import java.util.List;

/**
 * <p>
 * Structure used to handle pagination of data results on both <i>backend</i> (i.e., persistence) and <i>frontend</i>
 * (i.e., presentation) layers in the application.
 * </p>
 *
 * @author SERPRO
 */
public interface Pagination {

	/**
	 * Returns the (one-indexed) current page.
	 */
	int getCurrentPage();

	/**
	 * Sets the (one-indexed) current page.
	 */
	void setCurrentPage(int currentPage);

	/**
	 * Returns the number of items per page.
	 */
	int getPageSize();

	/**
	 * Sets the number of items per page.
	 */
	void setPageSize(int pageSize);

	/**
	 * Returns the total number of results.
	 */
	long getTotalResults();

	/**
	 * Sets the total number of results and calculates the number of pages.
	 */
	void setTotalResults(long totalResults);

	/**
	 * Returns the total number of pages.
	 */
	int getTotalPages();

	/**
	 * Returns the position for the first record according to current page and page size.
	 */
	int getFirstResult();

	/**
	 * Sets the position for the first record and hence calculates current page according to page size.
	 */
	void setFirstResult(int firstResult);

	/**
	 * <p>Returns a list of page numbers from <code>{@link #getCurrentPage()} - pageAmountBefore</code> to
	 * <code>{@link #getCurrentPage()} + (pageAmount - 1)</code>.</p>
	 *
	 * <p>For example, if the current page is 8 and this method is called with <code>pageAmount = 3</code>
	 * and <code>pageAmountBefore=2</code> we get an array with the following values:
	 * <pre>
	 *     [6, 7, 8, 9, 10]
	 * </pre>
	 * </p>
	 *
	 * @param pageAmount           Amount of pages to return
	 * @param pageAmountBefore     Amount of pages to list before the current page
	 * @return An array of the page numbers that fit the criteria.
	 */
	int[] getPages(int pageAmount, int pageAmountBefore);

	/**
	 * Same as calling {@link #getPages(int, int)} with pageAmountBefore=0.
	 * @see #getPages(int, int)
	 */
	int[] getPages(int pageAmount);

	/**
	 * Same as calling {@link #getPages(int, int)} with pageAmount = ({@link #getTotalPages()} - {@link #getCurrentPage()})
	 * and pagesAmountBefore = 0.
	 */
	int[] getPages();

	/**
	 * Same as {@link #getPages()}, but returns result as a {@link List} of {@link Integer}.
	 *
	 * @see #getPages()
	 */
	List<Integer> getPagesList();

	/**
	 * Same as {@link #getPages(int)}, but returns result as a {@link List} of {@link Integer}.
	 *
	 * @see #getPages(int)
	 */
	List<Integer> getPagesList(int pageAmount);

	/**
	 * Same as {@link #getPages(int, int)}, but returns result as a {@link List} of {@link Integer}.
	 *
	 * @see #getPages(int, int)
	 */
	List<Integer> getPagesList(int pageAmount, int pageAmountBefore);

	/**
	 * Same as calling {@link #setCurrentPage(int)} with currentPage = {@link #getCurrentPage()} + 1.
	 */
	void nextPage();

	/**
	 * Same as calling {@link #setCurrentPage(int)} with currentPage = {@link #getCurrentPage()} - 1.
	 */
	void previousPage();

}
