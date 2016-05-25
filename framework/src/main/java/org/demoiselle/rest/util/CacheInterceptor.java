package org.demoiselle.rest.util;

import javax.enterprise.inject.spi.CDI;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;

/**
 * Created by 01748913506 on 24/05/16.
 */
@Interceptor
@Cache(value = "")
public class CacheInterceptor implements Serializable {

    private static final long serialVersionUID = 1L;

    @AroundInvoke
    public Object manage(final InvocationContext ic) throws Exception {
        Object result = ic.proceed();

        HttpServletResponse response = CDI.current().select(HttpServletResponse.class).get(); //Beans.getReference(HttpServletResponse.class);
        response.setHeader("Cache-Control", ic.getMethod().getAnnotation(Cache.class).value());

        return result;
    }
}
