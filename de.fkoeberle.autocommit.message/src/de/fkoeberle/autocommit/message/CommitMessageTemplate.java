package de.fkoeberle.autocommit.message;

import org.eclipse.osgi.util.NLS;

public final class CommitMessageTemplate {
	private final String defaultValue;
	private String value;

	public CommitMessageTemplate(String defaultValue) {
		this.defaultValue = defaultValue;
		this.value = defaultValue;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void resetToDefault() {
		this.value = defaultValue;
	}

	public String createMessageWithArgs(String... args) {
		return NLS.bind(value, args);
	}

}
