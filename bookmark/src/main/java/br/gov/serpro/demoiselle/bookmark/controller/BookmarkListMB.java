package br.gov.serpro.demoiselle.bookmark.controller;

import br.gov.serpro.demoiselle.bookmark.business.BookmarkBC;
import br.gov.serpro.demoiselle.bookmark.domain.Bookmark;
import org.demoiselle.annotation.Name;
import org.demoiselle.jsf.template.AbstractListPageBean;
import org.demoiselle.pagination.Pagination;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

/**
 * <p>Gives clients access to services for accessing and maintaining Bookmark instances.</p>
 *
 * @author SERPRO
 */
@Named
@ViewScoped
public class BookmarkListMB extends AbstractListPageBean implements Serializable {

	private static final long serialVersionUID = -6861120258720113640L;

	@Inject
	private BookmarkBC bookmarkBO;

	@Override
	protected List handleResultList() {
		return bookmarkBO.listAll();
	}
}
