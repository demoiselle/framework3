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
package org.demoiselle.jsf.internal.implementation;

import org.demoiselle.stereotype.ApplicationException;
import org.demoiselle.exception.DemoiselleException;
import org.demoiselle.jsf.internal.configuration.ExceptionHandlerConfig;
import org.demoiselle.jsf.util.Faces;
import org.demoiselle.jsf.util.PageNotFoundException;
import org.demoiselle.jsf.util.Redirector;

import javax.enterprise.inject.spi.CDI;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import java.util.HashMap;
import java.util.Map;

/**
 * @author SERPRO
 */
public class ApplicationExceptionHandler extends AbstractExceptionHandler {

    public ApplicationExceptionHandler(final ExceptionHandler wrapped) {
        super(wrapped);
    }

    protected boolean handleException(final Throwable cause, FacesContext facesContext) {
        boolean handled = false;
        ExceptionHandlerConfig config = CDI.current().select(ExceptionHandlerConfig.class).get();// Beans.getReference(ExceptionHandlerConfig.class);

        if (config.isApplicationExceptionHandle() && isApplicationException(cause)) {

            if (isRendering(facesContext)) {
                handled = handlingDuringRenderResponse(cause, config);
            } else {
                Faces.addMessage(cause);
                handled = true;
            }
        }

        return handled;
    }

    private boolean isRendering(FacesContext context) {
        return PhaseId.RENDER_RESPONSE.equals(context.getCurrentPhaseId());
    }

    /**
     * In render response phase an exception interrupt the renderization. So this method will redirect the rendering to
     * an page configured in demoiselle.properties
     *
     * @param cause
     * @param config
     * @return
     */
    private boolean handlingDuringRenderResponse(final Throwable cause, final ExceptionHandlerConfig config) {
        boolean handled = false;
        try {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("exception", cause.getMessage());
            Redirector.redirect(config.getDefaultRedirectExceptionPage(), map);
            handled = true;
        } catch (PageNotFoundException ex) {
            // TODO Colocar a mensagem no bundle
            throw new DemoiselleException(
                    "A tela de exibição de erros: \""
                            + ex.getViewId()
                            + "\" não foi encontrada. Caso o seu projeto possua outra, defina no arquivo de configuração a chave \""
                            + "frameworkdemoiselle.exception.default.redirect.page" + "\"", ex);
        }
        return handled;
    }

    protected Throwable getRoot(final Throwable throwable) {
        Throwable root = throwable;

        while (root != null && !isApplicationException(root)) {
            root = root.getCause();
        }

        return root;
    }

    private static boolean isApplicationException(final Throwable throwable) {
        return throwable != null && throwable.getClass().isAnnotationPresent( ApplicationException.class);
    }
}
