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
 * Structure that can be used to manage lazy loading of collections
 * of entities, organizing the loading process into <i>pages</i>.
 * </p>
 * <p>
 * A page is a small collection of entities with a pre-defined size
 * of '{@link #getPageSize()}' elements that is a subset of the full list of entities
 * that fit a certain criteria (a <i>query</i>).
 * An user can navigate through pages by calling {@link #setCurrentPage(int)},
 * which will in turn load the next batch of '{@link #getPageSize()}' results
 * from the full collection.
 * </p>
 * <p>
 * Classes responsible for loading entities from persistence can optionally define
 * the total amount of results using {@link #setTotalResults(long)}, this allows
 * the application's presentation layer to know beforehand the maximum number of pages
 * that collection was split into. There are certain scenarios where it's not possible
 * to predict the final size of the collection, in such cases a persistence layer can
 * define a negative number for {@link #setTotalResults(long)}, indicating that this
 * number is unknown. Presentation layers can use this information to customize the presentation
 * indicating visually to the user that this number is unknown, for example by only providing
 * <i>next</i> and <i>previous</i> options instead of specific page numbers to be chosen.
 * </p>
 *
 * <p>
 * To use Pagination instances in your code inject them with {@link javax.inject.Inject} and qualify
 * them with either the {@link org.demoiselle.annotation.Name} qualifier or the {@link org.demoiselle.annotation.Type}
 * qualifier,the pagination will then be mapped to that resource and multiple injection points that reference
 * the same resource will point to the same paginator. This allows you to have multiple pagination objects paginating
 * multiple collections in the same request.
 * </p>
 *
 * <p>
 * For example:
 * <pre>
 *     \u0040ViewController
 *     public class BookmarkView {
 *         \u0040Inject
 *         \u0040Name("BookmarkCollection")
 *         private Pagination bookmarkPagination;
 *
 *         \u0040Inject
 *         \u0040Type(LinkReference.class)
 *         private Pagination linkPagination;
 *
 *         \u0040Inject
 *         private BookmarkPersistence bookmarkPersistence;
 *
 *         public List \u003C Bookmark \u003E getBookmarkList(int currentPage) {
 *             if (bookmarkPagination != null) {
 *                 bookmarkPagination.setCurrentPage(currentPage);
 *             }
 *
 *             return bookmarkPersistence.listAll();
 *         }
 *     }
 *
 *     \u0040PersistenceController
 *     public class BookmarkPersistence {
 *
 *         \u0040Inject
 *         \u0040Name("BookmarkCollection")
 *         private Pagination bookmarkPagination;
 *
 *         public List \u003C Bookmark \u003E listAll() {
 *             Query bookmarkQuery = //... Initialize database query
 *
 *             if (bookmarkPagination != null && bookmarkPagination.getFirstResult() >= 0) {
 *                 // Indicates to our presentation layer the maximum number of bookmarks
 *                 bookmarkPagination.setTotalResults( this.countAll() );
 *
 *                 bookmarkQuery.setFirstResult( bookmarkPagination.getFirstResult() );
 *                 bookmarkQuery.setMaxResults( bookmarkPagination.getMaxResults() );
 *             }
 *
 *             return bookmarkQuery.getResultList();
 *         }
 *
 *         public int countAll() {
 *             // Implements count query
 *         }
 *     }
 * </pre>
 * </p>
 *
 * <p>
 * Pagination objects are {@link javax.enterprise.context.RequestScoped} by default and need this scope
 * to be active to work. When injecting a Pagination reference outside a {@link javax.enterprise.context.RequestScoped}
 * scope the resulting injection point will be <code>null</code>.
 * </p>
 *
 * <p>
 * It's possible to globaly configure certain aspects of pagination objects through the <i>demoiselle.properties</i>
 * file. The pertaining configuration properties are:
 *
 * <pre>
 * <code><b>demoiselle.pagination.page.size</b></code>: Default size of a page, defining the number of entities shown in a single page.
 * This property will set the default for newly injected instances for that request but can be redefined by calling {@link #setPageSize(int)}
 * on an injected instance.
 *
 * <code><b>demoiselle.pagination.page.maxlinks</b></code>: Intended for the presentation layer, this property defines
 * the maximum amount of page numbers to show around the current page for the user to select. For example, if the total amount of pages
 * for a certain entity is 100 and we are at page 12 and this property value is 5, the presentation layer is instructed
 * to then show links as:
 *
 * [Previous Page][10][11][12][13][14][Next Page]
 *
 *
 * </pre>
 * </p>
 *
 * @author SERPRO
 */
public interface Pagination {

	/**
	 * Resets all pagination definitions. {@link #isInitialized()} will return <code>false</code> after this call.
	 */
	void reset();

	/**
	 * Returns the (one-indexed) current page.
	 *
	 * @throws IllegalStateException If the pagination hasn't been initialized by first calling
	 * one of {@link #setCurrentPage(int)}, {@link #setFirstResult(int)} or {@link #setTotalResults(long)}.
	 */
	int getCurrentPage();

	/**
	 * Sets the (one-indexed) current page.
	 */
	void setCurrentPage(int currentPage);

	/**
	 * Returns the number of items per page.
	 * @throws IllegalStateException If the pagination hasn't been initialized by first calling
	 * one of {@link #setCurrentPage(int)}, {@link #setFirstResult(int)} or {@link #setTotalResults(long)}.
	 */
	int getPageSize();

	/**
	 * Sets the number of items per page.
	 */
	void setPageSize(int pageSize);

	/**
	 * Returns the total number of results or a negative number to indicate
	 * the number is unknown.
	 *
	 * @throws IllegalStateException If the pagination hasn't been initialized by first calling
	 * one of {@link #setCurrentPage(int)}, {@link #setFirstResult(int)} or {@link #setTotalResults(long)}.
	 */
	long getTotalResults();

	/**
	 * Optional operation. Sets the total number of results and calculates the number of pages.
	 * Setting this to a negative value will indicate to clients that the total number of results is unknown.
	 */
	void setTotalResults(long totalResults);

	/**
	 * Returns the total number of pages.
	 *
	 * @throws IllegalStateException If the pagination hasn't been initialized by first calling
	 * one of {@link #setCurrentPage(int)}, {@link #setFirstResult(int)} or {@link #setTotalResults(long)}.
	 */
	int getTotalPages();

	/**
	 * Returns the position for the first record according to current page and page size, or a
	 * negative number if this pagination hasn't been initialized.
	 *
	 * @throws IllegalStateException If the pagination hasn't been initialized by first calling
	 * one of {@link #setCurrentPage(int)}, {@link #setFirstResult(int)} or {@link #setTotalResults(long)}.
	 */
	int getFirstResult();

	/**
	 * Sets the position for the first record and hence calculates current page according to page size.
	 */
	void setFirstResult(int firstResult);

	/**
	 * <p>Returns a list of page numbers from <code>{@link #getCurrentPage()} - pageAmountBefore</code> to
	 * <code>{@link #getCurrentPage()} + (pageAmount - 1)</code>.</p>
	 * <p>For example, if the current page is 8 and this method is called with <code>pageAmount = 3</code>
	 * and <code>pageAmountBefore=2</code> we get an array with the following values:
	 * <pre>
	 *     [6, 7, 8, 9, 10]
	 * </pre>
	 * </p>
	 *
	 * @param pagesAfterCurrent       Amount of pages to return after the current page
	 * @param pagesBeforeCurrent Amount of pages to list before the current page
	 * @return An array of the page numbers that fit the criteria.
	 *
	 * @throws IllegalStateException If the pagination hasn't been initialized by first calling
	 * one of {@link #setCurrentPage(int)}, {@link #setFirstResult(int)} or {@link #setTotalResults(long)}.
	 */
	int[] getPages(int pagesAfterCurrent, int pagesBeforeCurrent);

	/**
	 * Same as calling {@link #getPages(int, int)} with pagesBeforeCurrent=0.
	 *
	 * @see #getPages(int, int)
	 */
	int[] getPages(int pageAmount);

	/**
	 * Uses the default pagination configuration defined in the 'demoiselle.properties' file
	 * to obtain a value for the 'demoiselle.pagination.page.maxlinks' property and then
	 * calls {@link #getPages(int, int)}, setting values for <code>pagesAfterCurrent</code> and <code>pagesBeforeCurrent</code>
	 * so that the {@link #getCurrentPage()} stays in the middle (unless it's the first or last page) and the remaining pages
	 * are distributed around the current one.
	 *
	 * @see org.demoiselle.internal.configuration.PaginationConfig
	 * @see #getPages(int, int)
	 *
	 * @throws IllegalStateException If the pagination hasn't been initialized by first calling
	 * one of {@link #setCurrentPage(int)}, {@link #setFirstResult(int)} or {@link #setTotalResults(long)}.
	 */
	int[] getPages();

	/**
	 * Same as {@link #getPages()}, but returns result as a {@link List} of {@link Integer}.
	 *
	 * @see #getPages()
	 *
	 * @throws IllegalStateException If the pagination hasn't been initialized by first calling
	 * one of {@link #setCurrentPage(int)}, {@link #setFirstResult(int)} or {@link #setTotalResults(long)}.
	 */
	List<Integer> getPagesList();

	/**
	 * Same as {@link #getPages(int)}, but returns result as a {@link List} of {@link Integer}.
	 *
	 * @see #getPages(int)
	 *
	 * @throws IllegalStateException If the pagination hasn't been initialized by first calling
	 * one of {@link #setCurrentPage(int)}, {@link #setFirstResult(int)} or {@link #setTotalResults(long)}.
	 */
	List<Integer> getPagesList(int pagesAfterCurrent);

	/**
	 * Same as {@link #getPages(int, int)}, but returns result as a {@link List} of {@link Integer}.
	 *
	 * @see #getPages(int, int)
	 */
	List<Integer> getPagesList(int pagesAfterCurrent, int pagesBeforeCurrent);

	/**
	 * @return <code>true</code> if {@link #getCurrentPage()} == 1.
	 *
	 * @throws IllegalStateException If the pagination hasn't been initialized by first calling
	 * one of {@link #setCurrentPage(int)}, {@link #setFirstResult(int)} or {@link #setTotalResults(long)}.
	 */
	boolean isFirstPage();

	/**
	 * @return <code>true</code> if {@link #getCurrentPage()} == {@link #getTotalPages()}.
	 *
	 * @throws IllegalStateException If the pagination hasn't been initialized by first calling
	 * one of {@link #setCurrentPage(int)}, {@link #setFirstResult(int)} or {@link #setTotalResults(long)}.
	 */
	boolean isLastPage();

	/**
	 * @return <code>false</code> when recently created, <code>true</code> after one of
	 * {@link #setCurrentPage(int)}, {@link #setFirstResult(int)} or {@link #setTotalResults(long)} is called.
	 */
	boolean isInitialized();
}
