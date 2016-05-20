package org.demoiselle.jsf.util;

import org.demoiselle.exception.DemoiselleException;

/**
 * Created by 01748913506 on 13/05/16.
 */
public class PageNotFoundException extends DemoiselleException {

    private static final long serialVersionUID = 1L;

    private final String viewId;

    public PageNotFoundException(String viewId) {
        // TODO Colocar a mensage no bundle
        super(viewId + " not found");
        this.viewId = viewId;
    }

    public String getViewId() {
        return viewId;
    }
}

