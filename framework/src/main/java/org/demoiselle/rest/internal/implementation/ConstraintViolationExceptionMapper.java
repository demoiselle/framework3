package org.demoiselle.rest.internal.implementation;

import org.demoiselle.annotation.literal.NameQualifier;
import org.demoiselle.rest.UnprocessableEntityException;
import org.demoiselle.util.ResourceBundle;

import javax.enterprise.inject.spi.CDI;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * Created by 01748913506 on 25/05/16.
 */
@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    private transient ResourceBundle bundle;

    private transient Logger logger;

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        UnprocessableEntityException failed = new UnprocessableEntityException();
        int status = new UnprocessableEntityException().getStatusCode();


        for (Iterator<ConstraintViolation<?>> iter = exception.getConstraintViolations().iterator(); iter.hasNext();) {
            ConstraintViolation<?> violation = iter.next();
            String property = getPropertyViolationName(violation);
            failed.addViolation(property, violation.getMessage());
        }

        getLogger().fine(getBundle().getString("mapping-violations", status, failed.getViolations().toString()));

        Object entity = failed.getViolations();
        String mediaType = failed.getMediaType();

        return Response.status(status).entity(entity).type(mediaType).build();
    }

    /**
     *Método que separa as partes da string que representa a propriedade na qual a violação
     * ocorreu, e devolve apenas a última parte, que é corresponde apenas ao nome do atributo.
     *
     * @param violation a ConstraintViolation ocorrida.
     *
     * @return o nome da propriedade que causou a violação.
     *
     */
    private String getPropertyViolationName(ConstraintViolation violation){
       /*Na implementação desse método estamos considerando que o property path (getPropertyPath().toString())
        * retorna sempre uma string no formato nomemetodo.posicaoargumento.nomeatributo, do qual iremos extrair
        * a última parte.
        * */
        //TODO: Verificar se a especificação determina esse formato, ou se essa é a implementação do Hibernate.
        String[] propertyParts = violation.getPropertyPath().toString().split("\\.");
        return propertyParts[2];
    }

    private ResourceBundle getBundle() {
        if (bundle == null) {
            bundle = CDI.current().select(ResourceBundle.class, new NameQualifier("demoiselle-rest-bundle")).get();
        }

        return bundle;
    }

    private Logger getLogger() {
        if (logger == null) {
            logger = CDI.current().select(Logger.class, new NameQualifier("org.demoiselle.exception")).get();// Beans.getReference(Logger.class, new NameQualifier("br.gov.frameworkdemoiselle.exception"));
        }

        return logger;
    }
}
