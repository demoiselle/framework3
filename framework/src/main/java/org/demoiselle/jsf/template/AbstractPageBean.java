/*
 * Demoiselle Framework
 * Copyright (C) 2010 SERPRO
 * ----------------------------------------------------------------------------
 * This file is part of Demoiselle Framework.
 * 
 * Demoiselle Framework is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License version 3
 * along with this program; if not,  see <http://www.gnu.org/licenses/>
 * or write to the Free Software Foundation, Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA  02110-1301, USA.
 * ----------------------------------------------------------------------------
 * Este arquivo é parte do Framework Demoiselle.
 * 
 * O Framework Demoiselle é um software livre; você pode redistribuí-lo e/ou
 * modificá-lo dentro dos termos da GNU LGPL versão 3 como publicada pela Fundação
 * do Software Livre (FSF).
 * 
 * Este programa é distribuído na esperança que possa ser útil, mas SEM NENHUMA
 * GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer MERCADO ou
 * APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral GNU/LGPL em português
 * para maiores detalhes.
 * 
 * Você deve ter recebido uma cópia da GNU LGPL versão 3, sob o título
 * "LICENCA.txt", junto com esse programa. Se não, acesse <http://www.gnu.org/licenses/>
 * ou escreva para a Fundação do Software Livre (FSF) Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02111-1301, USA.
 */
package org.demoiselle.jsf.template;

import org.demoiselle.jsf.annotation.NextView;
import org.demoiselle.jsf.annotation.PreviousView;
import org.demoiselle.jsf.util.Faces;

/**
 * <p>
 * This implementation reads information about page navigation
 * through the {@link NextView} and {@link PreviousView} annotations.
 * </p>
 *
 * <p>
 * Concrete implementations of this class can then be annotated with the above
 * annotations to provide information about where the framework can navigate
 * the user after a managed bean's action.
 * </p>
 *
 * ex:
 *
 * <pre>
 *     &#064;PreviousView("previous_page_when_canceled")
 *     &#064;NextView("next_page_when_success")
 *     public class ConcreteManagedBean extends AbstractPageBean {
 *         public String saveAction() {
 *             // ... Call a business object's method
 *
 *             if (sucess) {
 *  	           // Will return the next view described in the {@linkplain NextView @NextView} annotation.
 *                 return getNextView();
 *             }
 *             else {
 *                 // Output an error message
 *
 *                 // Will return the previous view described in the {@linkplain PreviousView @PreviousView} annotation.
 *                 return getPreviousView();
 *             }
 *         }
 *     }
 * </pre>
 *
 * @author SERPRO
 * @see PageBean
 */
public abstract class AbstractPageBean implements PageBean {

	private static final long serialVersionUID = 1L;

	private String nextView;

	private String previousView;

	@Override
	public String getCurrentView() {
		return Faces.getCurrentViewId();
	}

	@Override
	public String getNextView() {

		if (nextView == null) {
			NextView annotation = this.getClass().getAnnotation(NextView.class);

			if (annotation != null) {
				nextView = annotation.value();
			} else {
				// TODO Lançar exceção orientando o usuário a anotar sua classe com essa anotação ou sobrescrever esse método.
			}
		}

		return nextView;
	}

	@Override
	public String getPreviousView() {

		if (previousView == null) {
			PreviousView annotation = this.getClass().getAnnotation(PreviousView.class);

			if (annotation != null) {
				previousView = annotation.value();
			} else {
				// TODO Lançar exceção orientando o usuário a anotar sua classe com essa anotação ou sobrescrever esse método.
			}
		}

		return previousView;
	}

	@Override
	public String getTitle() {
		return null;
	}
}
