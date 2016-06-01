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
package org.demoiselle.internal.implementation;

import org.demoiselle.annotation.literal.NameQualifier;
import org.demoiselle.internal.configuration.PaginationConfig;
import org.demoiselle.pagination.Pagination;
import org.demoiselle.util.ResourceBundle;
import org.demoiselle.util.Strings;

import javax.enterprise.inject.spi.CDI;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * Structure used to handle pagination of data results on both <i>backend</i> (i.e., persistence) and <i>frontend</i>
 * (i.e., presentation) layers in the application.
 * </p>
 * <p>
 * Internally, it stores the current page index on {@code currentPage} variable, the amount of records in a single page
 * on {@code pageSize}, and the total number of pages in {@code totalPages}.
 * </p>
 *
 * @author SERPRO
 * @see Pagination
 */
public class PaginationImpl implements Serializable, Pagination {

	private static final long serialVersionUID = 1L;

	private int currentPage = -1;

	private int pageSize = -1;

	private long totalResults = -1;

	private int totalPages = -1;

	private boolean initialized = false;

	private transient PaginationConfig configuration;

	private transient ResourceBundle bundle;

	public PaginationImpl() {
		reset();
	}

	@Override
	public void reset() {
		pageSize = -1;
		totalResults = -1;
		currentPage = -1;
		totalPages = -1;
		initialized = false;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		validateOneIndexedValue(currentPage);
		validateCurrentPage(currentPage);
		this.currentPage = currentPage;
		this.initialized = true;
	}

	@Override
	public int getPageSize() {
		return pageSize;
	}

	@Override
	public long getTotalResults() {
		return totalResults;
	}

	@Override
	public void setTotalResults(long totalResults) {
		this.totalResults = totalResults;
		this.initialized = true;
		setTotalPages();
	}

	@Override
	public int getTotalPages() {
		return totalPages;
	}

	@Override
	public int getFirstResult() {
		return (getCurrentPage() - 1) * getPageSize();
	}

	@Override
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;

		if (pageSize > 0) {
			setTotalPages();
		} else {
			reset();
		}
	}

	@Override
	public void setFirstResult(int firstResult) {
		validateNegativeValue(firstResult);
		validateFirstResult(firstResult);

		if (firstResult > 0) {
			setCurrentPage((firstResult / pageSize) + 1);
		} else {
			setCurrentPage(1);
		}

		initialized = true;
	}

	@Override
	public int[] getPages(final int pagesAfterCurrent, final int pagesBeforeCurrent) {
		if (!isInitialized()) {
			return new int[0];
		}

		// Se pagesAfterCurrent tem mais páginas que as restantes entre a página
		// atual e o total de páginas, reduzimos o número para conter apenas
		// as páginas restantes.
		int remainingPagesAfter = pagesAfterCurrent;
		if (getTotalPages() > 0) {
			if (getCurrentPage() + pagesAfterCurrent > getTotalPages()) {
				remainingPagesAfter = getTotalPages() -  getCurrentPage();

				if (remainingPagesAfter < 0) {
					remainingPagesAfter = 0;
				}
			}
		}

		// Se pagesBeforeCurrent tem mais paginas que o possível (pagina atual é 2 e pagesBeforeCurrent=3 por exemplo)
		// calculamos aqui um número válido para pagesBeforeCurrent
		final int remainingPagesBefore =
				pagesBeforeCurrent < getCurrentPage() ? pagesBeforeCurrent : getCurrentPage() - 1;

		// Armazena a lista de páginas
		final int[] pages = new int[1 + remainingPagesAfter + remainingPagesBefore];

		int i = 0;
		if (remainingPagesBefore > 0) {
            for (i = 0; i < remainingPagesBefore; i++) {
				pages[i] = getCurrentPage() - (remainingPagesBefore - i);
			}
		}

		pages[i] = getCurrentPage();

		if (remainingPagesAfter > 0) {
			for (i = remainingPagesBefore + 1; i < remainingPagesBefore + remainingPagesAfter + 1; i++) {
				pages[i] = i + 1;
			}
		}

		return pages;
	}

	@Override
	public int[] getPages(final int pagesAfterCurrent) {
		return getPages(pagesAfterCurrent, 0);
	}

	@Override
	public int[] getPages() {
		if (!isInitialized()) {
			return new int[0];
		}

		final int maxLinks = getConfiguration().getMaxPageLinks();
		int linksBefore;
		int linksAfter;

		if (getCurrentPage() > (maxLinks / 2)) {
			// Se maxLinks é par reduzimos linksBefore em 1, pois a distribuição deve ficar:
			// [x] [0] [x] [x], onde [x] é uma página antes ou depois e [0] é a página atual.
			// Se maxLinks é impar não precisamos reduzir linksBefore em 1 pois a página atual
			// fica exatamente no meio:
			// [x][x][0][x][x]
			// totalizando em metade de maxLinks antes, metade depois e a página atual no meio.
			linksBefore = ((maxLinks & 1) == 0) ? (maxLinks / 2) - 1 : (maxLinks / 2);
			linksAfter = (maxLinks / 2);
		} else {
			linksBefore = getCurrentPage() - 1;
			linksAfter = maxLinks - linksBefore - 1;
		}

		return getPages(linksAfter, linksBefore);
	}

	@Override
	public List<Integer> getPagesList() {
		ArrayList<Integer> pages = new ArrayList<>();
		for (int page : getPages()) {
			pages.add(page);
		}

		return pages;
	}

	@Override
	public List<Integer> getPagesList(int pagesAfterCurrent) {
		ArrayList<Integer> pages = new ArrayList<>();
		for (int page : getPages(pagesAfterCurrent)) {
			pages.add(page);
		}

		return pages;
	}

	@Override
	public List<Integer> getPagesList(int pagesAfterCurrent, int pagesBeforeCurrent) {
		ArrayList<Integer> pages = new ArrayList<>();
		for (int page : getPages(pagesAfterCurrent, pagesBeforeCurrent)) {
			pages.add(page);
		}

		return pages;
	}

	@Override
	public boolean isFirstPage() {
		return getCurrentPage() == 1;
	}

	@Override
	public boolean isLastPage() {
		return getTotalPages() > 0 && getCurrentPage() == getTotalPages();
	}

	@Override
	public boolean isInitialized() {
		return initialized;
	}

	@Override
	public String toString() {
		return Strings.toString(this);
	}

	private void setTotalPages(int totalPages) {
		this.totalPages = totalPages;

		if (totalPages == 0) {
			reset();
		} else if (totalPages > 0 && getCurrentPage() > totalPages) {
			setCurrentPage(totalPages);
		}
	}

	private void validateOneIndexedValue(int input) throws IndexOutOfBoundsException {
		if (input <= 0) {
			//TODO colocar mensagem
			throw new IndexOutOfBoundsException("colocar mensagem");
		}
	}

	private void validateCurrentPage(int currentPage) throws IndexOutOfBoundsException {
		if (currentPage > this.totalPages) {
			if (this.totalPages >= 0) {
				throw new IndexOutOfBoundsException( getBundle().getString("pagination-invalid-value", currentPage) );
			}
		}
	}

	private void setTotalPages() {
		if (totalResults > 0) {
			setTotalPages((int) Math.ceil(totalResults * 1d / getPageSize()));
		} else {
			setTotalPages((int) totalResults);
		}
	}

	private void validateFirstResult(int firstResult) throws IndexOutOfBoundsException {
		if (firstResult >= this.totalResults) {
			if (this.totalResults > 0) {
				throw new IndexOutOfBoundsException( getBundle().getString("pagination-invalid-value", firstResult) );
			}
		}
	}

	private void validateNegativeValue(long input) throws IndexOutOfBoundsException {
		if (input < 0L) {
			throw new IndexOutOfBoundsException( getBundle().getString("pagination-invalid-value", input) );
		}
	}

	private PaginationConfig getConfiguration() {
		if (configuration == null) {
			configuration = CDI.current().select(PaginationConfig.class).get();
		}

		return configuration;
	}

	private ResourceBundle getBundle() {
		if (bundle == null) {
			bundle = CDI.current().select(ResourceBundle.class, new NameQualifier("demoiselle-core-bundle")).get();
		}

		return bundle;
	}
}
