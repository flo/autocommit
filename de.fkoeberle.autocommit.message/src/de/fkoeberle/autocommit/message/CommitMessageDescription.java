package de.fkoeberle.autocommit.message;

import java.util.ArrayList;
import java.util.List;

public class CommitMessageDescription {
	private final List<IListener> listenerList = new ArrayList<IListener>(1);
	private final String defaultValue;
	private String currentValue;

	public CommitMessageDescription(CommitMessageTemplate template) {
		this.defaultValue = template.getDefaultValue();
		this.currentValue = template.getValue();
		if (currentValue == null) {
			throw new NullPointerException();
		}
	}

	public String getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(String currentValue) {
		this.currentValue = currentValue;
		for (IListener listener : listenerList) {
			listener.handleMessageChanged();
		}
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void reset() {
		setCurrentValue(getDefaultValue());
	}

	public boolean isResetPossible() {
		return !defaultValue.equals(currentValue);
	}

	public interface IListener {
		void handleMessageChanged();
	}

	public void addListener(IListener listener) {
		listenerList.add(listener);
	}

	public void removeListener(IListener listener) {
		listenerList.remove(listener);
	}
}
