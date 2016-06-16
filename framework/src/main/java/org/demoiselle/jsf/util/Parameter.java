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
package org.demoiselle.jsf.util;

import java.io.Serializable;

import javax.faces.convert.Converter;

/**
 * <p>
 * This class represents a parameter defined during an HTTP request. The main purpose
 * of this class is to get a direct reference to a value defined during a request
 * in any layer of the application.
 * </p>
 * <p>
 * The most common form of usage is to have a request parameter sent from the client
 * (either on the URL as a GET parameter or on the header as a POST parameter)
 * and then inject this in any bean involved on the request processing, using generics
 * to activate automatic conversion to the generics type and (optionally) the {@link org.demoiselle.annotation.Name}
 * annotation to specify the name of the parameter, though if no {@link org.demoiselle.annotation.Name} annotation
 * is specified then the name of the attribute must match the name of the request parameter.
 * </p>
 * <pre>
 *
 * public class BookmarkView {
 *
 *     &#064;Inject
 *     &#064;Name("page")
 *
 *     Parameter&#60;Integer&#62; pageParameter;
 *
 *     public void doAction() {
 *         System.out.println("The value of the 'page' request parameter is: " + pageParameter.getValue();
 *     }
 * }
 *
 * </pre>
 * <p>
 * By default parameters will be retrieved from the current request parameter list, as if calling
 * {@link javax.servlet.ServletRequest#getParameter(String)} with the name of this parameter as argument.
 * It's also possible to use this to define and retrieve parameters in other scopes. If the attribute is annotated
 * with {@link javax.enterprise.context.SessionScoped} then the first time the parameter is defined on a request
 * it will be retrieved from that and then copied with the same name to the current {@link javax.servlet.http.HttpSession}
 * instance, from then on subsequent accesses to this parameter will get the value from the session.
 * </p>
 * <p>
 * The same happens on a JSF application if this is annotated with {@link javax.faces.view.ViewScoped},
 * the parameter is first retrieved from the current request and saved on the
 * current view, meaning the value will persist while the user is navigating on the same JSF view.
 * </p>
 * @param <T> Type of the parameter, used for automatic conversion from String.
 * @author SERPRO
 */
public interface Parameter<T extends Serializable> extends Serializable {

    void setValue(T value);

    String getKey();

    T getValue();

}
