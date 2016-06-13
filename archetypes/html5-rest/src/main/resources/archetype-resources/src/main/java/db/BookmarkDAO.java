#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.db;

import ${package}.domain.Bookmark;
import org.demoiselle.annotation.Type;
import org.demoiselle.jpa.template.ContainerManagedJPADatabaseAccess;
import org.demoiselle.pagination.Pagination;
import org.demoiselle.stereotype.PersistenceController;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * <p>
 * Gives access to {@link Bookmark} instances stored in the database.
 *</p>
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

	public List<Bookmark> find(String filter){
		StringBuffer ql = new StringBuffer();
		ql.append("  from Bookmark b ");
		ql.append(" where lower(b.description) like :description ");
		ql.append("    or lower(b.link) like :link ");

		TypedQuery<Bookmark> query = getEntityManager().createQuery(ql.toString(), Bookmark.class);
		query.setParameter("description", "%" + filter.toLowerCase() + "%");
		query.setParameter("link", "%" + filter.toLowerCase() + "%");

		return query.getResultList();
	}
}
