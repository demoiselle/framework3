package br.gov.serpro.demoiselle.bookmark.business;

import br.gov.serpro.demoiselle.bookmark.db.BookmarkDAO;
import br.gov.serpro.demoiselle.bookmark.domain.Bookmark;
import org.demoiselle.jpa.template.JPADatabaseAccess;
import org.demoiselle.lifecycle.Startup;
import org.demoiselle.stereotype.BusinessController;
import org.demoiselle.template.DatabaseDelegator;

import javax.enterprise.inject.spi.CDI;
import javax.persistence.Query;
import javax.transaction.Transactional;

/**
 * <p>Contains business logic to persist and maintain bookmarks.</p>
 *
 * @author SERPRO
 */
@BusinessController
public class BookmarkBC extends DatabaseDelegator<Bookmark, Long, BookmarkDAO> {

	private static final long serialVersionUID = 679187426644841581L;

	@Startup
	@Transactional
	public void importData() {
		if (listAll().isEmpty()) {
			persist(new Bookmark("Demoiselle Portal", "http://www.frameworkdemoiselle.gov.br"));
			persist(new Bookmark("Demoiselle SourceForge", "http://sf.net/projects/demoiselle"));
			persist(new Bookmark("Twitter", "http://twitter.frameworkdemoiselle.gov.br"));
			persist(new Bookmark("Blog", "http://blog.frameworkdemoiselle.gov.br"));
			persist(new Bookmark("Wiki", "http://wiki.frameworkdemoiselle.gov.br"));
			persist(new Bookmark("Bug Tracking", "http://tracker.frameworkdemoiselle.gov.br"));
			persist(new Bookmark("Forum", "http://forum.frameworkdemoiselle.gov.br"));
			persist(new Bookmark("SVN", "http://svn.frameworkdemoiselle.gov.br"));
			persist(new Bookmark("Maven", "http://repository.frameworkdemoiselle.gov.br"));
			persist(new Bookmark("Downloads", "http://download.frameworkdemoiselle.gov.br"));
		}
	}

	public long countBookmarks() {
		Query q = getDelegate().getEntityManager().createQuery("SELECT count(b.id) FROM Bookmark b");
		return (long) q.getSingleResult();
	}
}
