package br.gov.serpro.demoiselle.bookmark.db;

import br.gov.serpro.demoiselle.bookmark.domain.Bookmark;
import org.demoiselle.annotation.Type;
import org.demoiselle.jpa.template.ContainerManagedJPADatabaseAccess;
import org.demoiselle.pagination.Pagination;
import org.demoiselle.stereotype.PersistenceController;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

/**
 * Gives access to {@link Bookmark} instances stored in the database.
 *
 * @author SERPRO
 */
@PersistenceController
public class BookmarkDAO extends ContainerManagedJPADatabaseAccess<Bookmark, Long> {

	private static final long serialVersionUID = -4856256172654532078L;

	@Inject
	@Type(Bookmark.class)
	private Instance<Pagination> bookmarkPagination;

	@Override
	protected Pagination getPagination() {
		return bookmarkPagination.get();
	}
}
