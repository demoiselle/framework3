package org.demoiselle.jsf.message;

import org.demoiselle.message.DefaultMessage;
import org.demoiselle.message.SeverityType;

public class ValidationFailedMessage extends DefaultMessage {

	public ValidationFailedMessage(String text, Object[] params) {
		super(text, params);
	}

	public ValidationFailedMessage(String text, SeverityType severity, Object... params) {
		super(text, severity, params);
	}
}
