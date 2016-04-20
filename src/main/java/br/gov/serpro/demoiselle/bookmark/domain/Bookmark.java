package br.gov.serpro.demoiselle.bookmark.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * Entity responsible for keeping bookmark information.
 *
 * @author SERPRO
 */
@Entity
public class Bookmark implements Serializable, Comparable<Bookmark> {

	private static final long serialVersionUID = -2804250741725999566L;

	private Long id;

	private String description;

	private String link;

	@Id
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@NotNull
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@NotNull
	@Pattern(regexp = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")
	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Bookmark bookmark = (Bookmark) o;

		if (!getDescription().equals(bookmark.getDescription()))
			return false;
		return getLink().equals(bookmark.getLink());

	}

	@Override
	public int hashCode() {
		int result = getDescription().hashCode();
		result = 31 * result + getLink().hashCode();
		return result;
	}

	@Override
	public int compareTo(Bookmark o) {
		return 0;
	}
}
