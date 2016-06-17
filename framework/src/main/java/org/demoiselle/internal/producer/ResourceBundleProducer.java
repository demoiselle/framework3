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
package org.demoiselle.internal.producer;

import org.demoiselle.annotation.Name;
import org.demoiselle.util.CDIUtils;
import org.demoiselle.util.ResourceBundle;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.inject.spi.InjectionPoint;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.Locale;

/**
 * This factory creates ResourceBundles with the application scopes.
 *
 * @author SERPRO
 */
@Dependent
public class ResourceBundleProducer implements Serializable {

	private static final long serialVersionUID = 1L;

	@Default
	@Produces
	public ResourceBundle createDefault() {
		return create((String) null);
	}

	/*
	 * Produces a {@link java.util.ResourceBundle} instance loading the properties file whose name
	 * is defined by the {@link Name} literal. If no value is specified
	 * then the default "messages.properties" file is loaded.
	 */
	@Name
	@Produces
	public ResourceBundle create(InjectionPoint ip) {
		String baseName = null;
		if (ip != null && ip.getQualifiers() != null) {
			Name nameQualifier = CDIUtils.getQualifier(Name.class, ip);
			if (nameQualifier != null) {
				baseName = nameQualifier.value();

				// Trata situações onde não foi especificado um valor
				// para o atributo "value"
				if ("".equals(baseName)) {
					baseName = null;
				}
			}
		}

		return create(baseName);
	}

	public static ResourceBundle create(String baseName) {
		ResourceBundle bundle;

		try {
			bundle = baseName != null ?
					new ResourceBundle(baseName, CDI.current().select(Locale.class).get()) :
					new ResourceBundle("messages", CDI.current().select(Locale.class).get());
		} catch (RuntimeException e) {
			bundle = baseName != null ?
					new ResourceBundle(baseName, Locale.getDefault()) :
					new ResourceBundle("messages", Locale.getDefault());
		}

		return bundle;
	}
}
