package de.fkoeberle.autocommit.message;

import org.eclipse.osgi.util.NLS;

public final class CommitMessageTemplate {
	private final String value;

	public CommitMessageTemplate(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public String createMessageWithArgs(String... args) {
		return NLS.bind(value, args);
	}

}
