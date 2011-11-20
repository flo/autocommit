package de.fkoeberle.autocommit.message;

public class CommitMessageDescription {
	private final String defaultValue;
	private String currentValue;

	public CommitMessageDescription(CommitMessageTemplate template) {
		this.defaultValue = template.getDefaultValue();
		this.currentValue = template.getValue();
	}

	public String getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(String currentValue) {
		this.currentValue = currentValue;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void reset() {
		this.currentValue = defaultValue;
	}
}
