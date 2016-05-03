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
package org.demoiselle.servlet.util;

import org.demoiselle.lifecycle.AfterShutdownProccess;
import org.demoiselle.lifecycle.AfterStartupProccess;

import javax.enterprise.inject.spi.CDI;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * <p>Maps the Servlet container lifecycle to the framework lifecycle.</p>
 *
 * <p>This listener will fire the framework specific CDI events {@link org.demoiselle.lifecycle.AfterStartupProccess}
 * and {@link org.demoiselle.lifecycle.AfterShutdownProccess} - the first just after the servlet context has initialized
 * and the second after the servlet context has been destroyed. These events will in turn run any methods
 * annotated with {@link org.demoiselle.lifecycle.Startup} and {@link org.demoiselle.lifecycle.Shutdown}
 * when the related events are fired.</p>
 *
 * @author SERPRO
 */
@WebListener
public class LifecycleServletListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent event) {
		CDI.current().getBeanManager().fireEvent(new AfterStartupProccess() {
		});
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		CDI.current().getBeanManager().fireEvent(new AfterShutdownProccess() {
		});
	}
}
