package br.gov.serpro.demoiselle.bookmark.db;

import org.demoiselle.jpa.template.JPADbAccess;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author SERPRO
 */
public abstract class DAO <T, I> extends JPADbAccess <T, I> {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	protected EntityManager getEntityManager() {
		return entityManager;
	}
}
