package br.gov.serpro.demoiselle.bookmark.controller;

import br.gov.serpro.demoiselle.bookmark.business.BookmarkBC;
import br.gov.serpro.demoiselle.bookmark.domain.Bookmark;
import org.demoiselle.jsf.annotation.NextView;
import org.demoiselle.jsf.annotation.PreviousView;
import org.demoiselle.jsf.stereotype.ViewController;
import org.demoiselle.jsf.template.AbstractListPageBean;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/**
 * <p>Gives clients access to services for accessing and maintaining Bookmark instances.</p>
 *
 * @author SERPRO
 */
@ViewController
@NextView("/bookmark_edit.xhtml")
@PreviousView("/bookmark_list.xhtml")
public class BookmarkListMB extends AbstractListPageBean<Bookmark, Long> implements Serializable {

	private static final long serialVersionUID = -6861120258720113640L;

	@Inject
	private BookmarkBC bookmarkBC;

	@Override
	protected List<Bookmark> handleResultList() {
		return bookmarkBC.listAll();
	}

	@Transactional
	public String deleteSelection() {
		boolean delete;

		for (Iterator<Long> iter = getSelection().keySet().iterator(); iter.hasNext(); ) {
			Long id = iter.next();
			delete = getSelection().get(id);

			if (delete) {
				bookmarkBC.remove(id);
				iter.remove();
			}
		}
		return getPreviousView();
	}
}
