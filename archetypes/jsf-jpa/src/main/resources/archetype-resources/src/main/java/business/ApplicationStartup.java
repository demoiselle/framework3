#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.business;

import org.demoiselle.annotation.Name;
import org.demoiselle.pagination.Pagination;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

/**
 * <p>
 * Initializes the Bookmark application
 * </p>
 */
@Startup
@Singleton
public class ApplicationStartup {

	@Inject
	private BookmarkBC bookmarkBC;

	@PostConstruct
	protected void initDatabase() {
		bookmarkBC.importData();
	}

}
