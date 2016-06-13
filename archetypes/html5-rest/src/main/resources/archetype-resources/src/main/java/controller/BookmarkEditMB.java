#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.controller;

import ${package}.business.BookmarkBC;
import ${package}.domain.Bookmark;
import org.demoiselle.jsf.annotation.PreviousView;
import org.demoiselle.jsf.stereotype.ViewController;
import org.demoiselle.jsf.template.AbstractEditPageBean;
import org.demoiselle.security.LoggedIn;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Iterator;

/**
 * @author SERPRO
 */
@ViewController
@PreviousView("/bookmark_list.xhtml")
public class BookmarkEditMB extends AbstractEditPageBean<Bookmark, Long> {

	private static final long serialVersionUID = 1041624130855491357L;

	@Inject
	private BookmarkBC bookmarkBC;

	@Override
	@Transactional
	protected Bookmark handleLoad(Long id) {
		Bookmark bookmarkDTO = new Bookmark();

		if (id != null) {
			Bookmark bookmark = bookmarkBC.load(id);

			if (bookmark != null) {
				bookmarkDTO.setId(bookmark.getId());
				bookmarkDTO.setDescription(bookmark.getDescription());
				bookmarkDTO.setLink(bookmark.getLink());
			}
		}

		return bookmarkDTO;
	}

	@Override
	@Transactional
	@LoggedIn
	public String insert() {
		Bookmark bean = getBean();

		if (bean != null) {
			bean.setId(null);
			Bookmark savedBookmark = bookmarkBC.merge(bean);

			if (savedBookmark != null && savedBookmark.getId() != null) {
				bean.setId(savedBookmark.getId());
			}
		}

		return null;
	}

	@Override
	@Transactional
	public String update() {
		Bookmark bean = getBean();

		if (bean != null) {
			if (bean.getId() == null) {
				bean.setId(getId());
			}

			bookmarkBC.merge(bean);
		}

		return null;
	}

	@Override
	@Transactional
	public String delete() {
		Bookmark bean = getBean();
		if (bean != null && bean.getId() != null) {
			bookmarkBC.remove(bean.getId());
			this.clear();
		}

		return getPreviousView();
	}

	public String getMessageStyle(String clientId) {
		String style = "";
		for (Iterator<FacesMessage> it = FacesContext.getCurrentInstance().getMessages(clientId); it.hasNext(); ) {
			FacesMessage msg = it.next();

			if (msg.getSeverity().equals(FacesMessage.SEVERITY_WARN)) {
				style = "has-warning";
			}
			else if (msg.getSeverity().equals(FacesMessage.SEVERITY_ERROR)
					|| msg.getSeverity().equals(FacesMessage.SEVERITY_FATAL)) {
				style = "has-error";
				break;
			}
		}

		return style;
	}
}
