package org.demoiselle.annotation;

import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.util.Nonbinding;
import javax.inject.Named;
import javax.inject.Qualifier;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * <p>
 * Type based non-binding qualifier.
 * </p>
 *
 * <p>
 * This annotation is used to qualify beans using a class type. {@link javax.enterprise.inject.Produces}
 * methods can then read this type and use it to customize the bean creation process.
 * </p>
 *
 * <p>
 * The {@link #value()} attribute is non-binding, meaning multiple classes
 * qualified with this annotation, even with different values, will be considered the same candidate for
 * injection points. To avoid ambiguous resolutions and select which candidate to choose usually you'll need a
 * producer method to read the type and select the best fitted candidate.
 * </p>
 *
 * <p>
 * The framework classes qualified with this annotation already have such producers and the accepted values for
 * this annotation will be detailed in their respective documentations.
 * </p>
 *
 *
 * @author SERPRO
 *
 */
@Qualifier
@Inherited
@Retention(RUNTIME)
@Target({ TYPE, FIELD, METHOD, PARAMETER })
public @interface Type {

	@Nonbinding
	Class<?> value() default Object.class;

}
