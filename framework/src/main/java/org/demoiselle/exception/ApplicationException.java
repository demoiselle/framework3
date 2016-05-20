package org.demoiselle.exception;

import org.demoiselle.message.SeverityType;

import javax.enterprise.inject.Stereotype;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by 01748913506 on 16/05/16.
 */

@Stereotype
@Inherited
@Target(TYPE)
@Retention(RUNTIME)
public @interface ApplicationException {

    /**
     * When raised, the exception that uses this annotation must cause a rollback in the current transaction?
     *
     * @return True if current transaction must be rolledback
     */
    boolean rollback() default true;

    /**
     * Exception Severity.
     *
     * @return
     */
    SeverityType severity() default SeverityType.INFO;
}
