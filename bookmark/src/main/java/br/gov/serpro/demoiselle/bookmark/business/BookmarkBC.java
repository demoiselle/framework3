package br.gov.serpro.demoiselle.bookmark.business;

import br.gov.serpro.demoiselle.bookmark.db.BookmarkDAO;
import br.gov.serpro.demoiselle.bookmark.domain.Bookmark;
import org.demoiselle.lifecycle.Startup;
import org.demoiselle.template.DatabaseDelegator;

import javax.transaction.Transactional;

/**
 * <p>Contains business logic to persist and maintain bookmarks.</p>
 *
 * @author SERPRO
 */
public class BookmarkBC extends DatabaseDelegator<Bookmark, Long, BookmarkDAO> {

	private static final long serialVersionUID = 679187426644841581L;

	@Startup
	@Transactional
	public void importData() {
		Bookmark bookmark = new Bookmark();
		bookmark.setDescription("Google");
		bookmark.setLink("http://www.google.com");
		merge(bookmark);

		bookmark = new Bookmark();
		bookmark.setDescription("Demoiselle");
		bookmark.setLink("http://www.demoiselle.org");
		merge(bookmark);

		bookmark = new Bookmark();
		bookmark.setDescription("SERPRO");
		bookmark.setLink("http://www.serpro.gov.br");
		merge(bookmark);
	}

}
