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
package org.demoiselle.internal.bootstrap;

import org.demoiselle.internal.producer.LoggerProducer;
import org.demoiselle.internal.producer.ResourceBundleProducer;
import org.demoiselle.util.ResourceBundle;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.*;
import java.util.logging.Logger;

@SuppressWarnings("unused")
public class CoreBootstrap implements Extension {

	private Logger logger;

	private transient ResourceBundle bundle;

	private Logger getLogger() {
		if (this.logger == null) {
			this.logger = LoggerProducer.create("br.gov.frameworkdemoiselle.lifecycle");
		}

		return this.logger;
	}

	private ResourceBundle getBundle() {
		if (bundle == null) {
			bundle = ResourceBundleProducer.create("demoiselle-core-bundle");
		}

		return bundle;
	}

	public void engineOn(@SuppressWarnings("UnusedParameters") @Observes final BeforeBeanDiscovery event,
			BeanManager beanManager) {
		getLogger().info(getBundle().getString("engine-on"));

		/*Beans.setBeanManager(beanManager);
		getLogger().finer(getBundle().getString("setting-up-bean-manager", Beans.class.getCanonicalName()));*/
	}

	public void takeOff(@Observes final AfterDeploymentValidation event) {
		getLogger().fine(getBundle().getString("taking-off"));
	}

	public void engineOff(@Observes final BeforeShutdown event) {
		getLogger().fine(getBundle().getString("engine-off"));
	}
}
