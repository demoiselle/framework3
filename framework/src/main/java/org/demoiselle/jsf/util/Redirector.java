package org.demoiselle.jsf.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.enterprise.inject.spi.CDI;
import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.context.FacesContext;

/**
 * Utility class to launch page redirect events inside a JSF web application.
 */
public class Redirector {

    private Redirector() {
    }

    public static void redirect(String viewId) {
        redirect(viewId, null);
    }

    public static void redirect(String viewId, Map<String, Object> params) {
        try {
            if (viewId != null && !viewId.isEmpty()) {
                FacesContext facesContext = FacesContext.getCurrentInstance();//CDI.current().select(FacesContext.class).get(); //Beans.getReference(FacesContext.class);
                ViewHandler viewHandler = facesContext.getApplication().getViewHandler();
                String url = viewHandler.getBookmarkableURL(facesContext, viewId, parse(params),
						params != null && !params.isEmpty());

                facesContext.getExternalContext().redirect(url);
            }

        } catch (NullPointerException cause) {
            cause.printStackTrace();
            throw new PageNotFoundException(viewId);

        } catch (IOException cause) {
            throw new FacesException(cause);
        }
    }

    private static Map<String, List<String>> parse(Map<String, Object> map) {
        Map<String, List<String>> result = null;

        if (map != null) {
            ArrayList<String> list;
            result = new HashMap<String, List<String>>();

            for (Map.Entry<String, Object> entry : map.entrySet()) {
                list = new ArrayList<String>();
                list.add(entry.getValue().toString());
                result.put(entry.getKey(), list);
            }
        }

        return result;
    }
}
