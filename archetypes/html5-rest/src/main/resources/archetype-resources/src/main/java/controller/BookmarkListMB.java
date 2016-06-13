#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.controller;

import ${package}.business.BookmarkBC;
import ${package}.domain.Bookmark;
import org.demoiselle.annotation.literal.TypeQualifier;
import org.demoiselle.jsf.annotation.NextView;
import org.demoiselle.jsf.annotation.PreviousView;
import org.demoiselle.jsf.stereotype.ViewController;
import org.demoiselle.jsf.template.AbstractListPageBean;
import org.demoiselle.pagination.Pagination;

import javax.enterprise.inject.spi.CDI;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

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

	@Inject
	private Logger logger;

	@Override
	protected List<Bookmark> handleResultList() {
		getBookmarkPagination();
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

	public void setPage(ActionEvent event) {
		try {
			Number pageNumber = (Number) event.getComponent().getAttributes().get("page");
			getBookmarkPagination().setCurrentPage(pageNumber.intValue());
		} catch (RuntimeException re) {
			getBookmarkPagination().setCurrentPage(1);
		}
	}

	public Pagination getBookmarkPagination() {
		Pagination pagination = CDI.current().select(Pagination.class, new TypeQualifier(Bookmark.class)).get();

		if (!pagination.isInitialized()) {
			pagination.setCurrentPage(1);
		}

		return pagination;
	}

}
