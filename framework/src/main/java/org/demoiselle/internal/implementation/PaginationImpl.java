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

import org.demoiselle.pagination.Pagination;
import org.demoiselle.util.Strings;

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

	private int currentPage = 1;

	private int pageSize;

	private long totalResults;

	private int totalPages;

	public PaginationImpl() {
		pageSize = 0;
		totalResults = 0;
		reset();
	}

	private void reset() {
		currentPage = 1;
		totalPages = 0;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	private void setTotalPages(int totalPages) {
		validateNegativeValue(totalPages);
		this.totalPages = totalPages;

		if (totalPages == 0) {
			reset();
		} else if (getCurrentPage() > totalPages) {
			setCurrentPage(totalPages - 1);
		}
	}

	private void validateOneIndexedValue(int input) throws IndexOutOfBoundsException {
		if (input <= 0) {
			throw new IndexOutOfBoundsException("colocar mensagem");
		}
	}

	private void validateNegativeValue(int input) throws IndexOutOfBoundsException {
		if (input < 0) {
			throw new IndexOutOfBoundsException("colocar mensagem");
		}
	}

	private void validateNegativeValue(long input) throws IndexOutOfBoundsException {
		if (input < 0L) {
			throw new IndexOutOfBoundsException("colocar mensagem");
		}
	}

	private void validateCurrentPage(int currentPage) throws IndexOutOfBoundsException {
		if (currentPage > this.totalPages) {
			if (this.totalPages > 0) {
				throw new IndexOutOfBoundsException("colocar mensagem");
			}
		}
	}

	public void setCurrentPage(int currentPage) {
		validateOneIndexedValue(currentPage);
		validateCurrentPage(currentPage);
		this.currentPage = currentPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public long getTotalResults() {
		return totalResults;
	}

	public void setTotalResults(long totalResults) {
		validateNegativeValue(totalResults);
		this.totalResults = totalResults;

		if (totalResults > 0) {
			setTotalPages();
		} else {
			reset();
		}
	}

	private void setTotalPages() {
		if (totalResults > 0) {
			setTotalPages((int) Math.ceil(totalResults * 1d / getPageSize()));
		} else {
			setTotalPages(0);
		}
	}

	public int getTotalPages() {
		return totalPages;
	}

	public int getFirstResult() {
		return (getCurrentPage()-1) * getPageSize();
	}

	public void setPageSize(int pageSize) {
		validateNegativeValue(pageSize);
		this.pageSize = pageSize;

		if (pageSize > 0) {
			setTotalPages();
		} else {
			reset();
		}
	}

	private void validateFirstResult(int firstResult) throws IndexOutOfBoundsException {
		if (firstResult >= this.totalResults) {
			if (this.totalResults > 0) {
				throw new IndexOutOfBoundsException("colocar mensagem");
			}
		}
	}

	public void setFirstResult(int firstResult) {
		validateNegativeValue(firstResult);
		validateFirstResult(firstResult);

		if (firstResult > 0) {
			setCurrentPage((firstResult / pageSize) + 1);
		} else {
			setCurrentPage(1);
		}
	}

	@Override
	public int[] getPages(int pageAmount, int pageAmountBefore) {
		if (pageAmount < 0 || pageAmountBefore < 0) {
			throw new IllegalArgumentException();
		}

		final int[] pages = new int[pageAmount + pageAmountBefore];

		int i = 0;
		if (pageAmountBefore > 0) {
			for (i = 0; i < pageAmountBefore; i++) {
				pages[i] = getCurrentPage() - (pageAmountBefore - i);
			}
		}

		if (pageAmount > 0) {
			for (i = pageAmountBefore; i < pageAmountBefore + pageAmount; i++) {
				pages[i] = getCurrentPage() + i;
			}
		}

		return pages;
	}

	@Override
	public int[] getPages(int pageAmount) {
		return getPages(pageAmount, 0);
	}

	@Override
	public int[] getPages() {
		if (getCurrentPage() > 0) {
			return getPages(getTotalPages() - (getCurrentPage() - 1), 0);
		}
		else {
			return getPages(getTotalPages(), 0);
		}
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
	public List<Integer> getPagesList(int pageAmount) {
		ArrayList<Integer> pages = new ArrayList<>();
		for (int page : getPages(pageAmount)) {
			pages.add(page);
		}

		return pages;
	}

	@Override
	public List<Integer> getPagesList(int pageAmount, int pageAmountBefore) {
		ArrayList<Integer> pages = new ArrayList<>();
		for (int page : getPages(pageAmount, pageAmountBefore)) {
			pages.add(page);
		}

		return pages;
	}

	@Override
	public void nextPage() {
		if (getCurrentPage() < getTotalPages()) {
			setCurrentPage(getCurrentPage() + 1);
		}
	}

	@Override
	public void previousPage() {
		if (getCurrentPage() > 1) {
			setCurrentPage(getCurrentPage() - 1);
		}
	}

	@Override
	public String toString() {
		return Strings.toString(this);
	}
}
