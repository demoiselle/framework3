package br.gov.serpro.demoiselle.bookmark.business;

import br.gov.serpro.demoiselle.bookmark.db.BookmarkDAO;
import br.gov.serpro.demoiselle.bookmark.domain.Bookmark;
import org.demoiselle.template.DatabaseDelegator;

/**
 * <p>Contains business logic to persist and maintain bookmarks.</p>
 *
 * @author SERPRO
 */
public class BookmarkBC extends DatabaseDelegator<Bookmark, Long, BookmarkDAO> {

	private static final long serialVersionUID = 679187426644841581L;

}
