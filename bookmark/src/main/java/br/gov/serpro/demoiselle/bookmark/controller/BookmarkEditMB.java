package br.gov.serpro.demoiselle.bookmark.controller;

import br.gov.serpro.demoiselle.bookmark.business.BookmarkBC;
import br.gov.serpro.demoiselle.bookmark.domain.Bookmark;
import org.demoiselle.jsf.annotation.NextView;
import org.demoiselle.jsf.annotation.PreviousView;
import org.demoiselle.jsf.template.AbstractEditPageBean;
import org.demoiselle.security.LoggedIn;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

/**
 * @author SERPRO
 */
@ViewScoped
@Named
@PreviousView("bookmark_list.jsf")
@NextView("bookmark_edit.jsf")
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
		String idParameter = null;

		if (bean != null) {
			bean.setId(null);
			Bookmark savedBookmark = bookmarkBC.merge(bean);

			if (savedBookmark != null && savedBookmark.getId() != null) {
				bean.setId(savedBookmark.getId());
				idParameter = "?id="+savedBookmark.getId();
			}
		}

		//TODO Fazer esse retorno abrir novamente a tela de edição em modo update com esse registro.
		return getNextView() + idParameter;
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
}
