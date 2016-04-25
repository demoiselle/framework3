package br.gov.serpro.demoiselle.bookmark.controller;

import br.gov.serpro.demoiselle.bookmark.business.BookmarkBC;
import org.demoiselle.jsf.template.AbstractListPageBean;

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

	@Inject
	private BookmarkBC bookmarkBO;

	@Override
	protected List handleResultList() {
		return bookmarkBO.listAll();
	}
}
