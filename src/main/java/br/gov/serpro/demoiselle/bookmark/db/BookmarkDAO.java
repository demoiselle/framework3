package br.gov.serpro.demoiselle.bookmark.db;

import br.gov.serpro.demoiselle.bookmark.domain.Bookmark;
import org.demoiselle.jpa.template.ContainerManagedJPADatabaseAccess;

/**
 * Gives access to {@link Bookmark} instances stored in the database.
 *
 * @author SERPRO
 */
public class BookmarkDAO extends ContainerManagedJPADatabaseAccess<Bookmark, Long> {

	private static final long serialVersionUID = -4856256172654532078L;
}
