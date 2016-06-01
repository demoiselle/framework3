package org.demoiselle.jsf.internal.implementation;

import java.io.Serializable;

/**
 * Used to hold scoped values for {@link org.demoiselle.jsf.util.Parameter}
 * instances.
 */
@SuppressWarnings("WeakerAccess")
public class ParameterValueImpl implements Serializable {

	private static final long serialVersionUID = 1L;

	private Serializable value;

	public Serializable getValue() {
		return value;
	}

	public void setValue(Serializable value) {
		this.value = value;
	}
}
