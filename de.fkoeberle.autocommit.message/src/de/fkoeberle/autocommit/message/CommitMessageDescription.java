package de.fkoeberle.autocommit.message;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class CommitMessageDescription {
	private final List<IListener> listenerList = new ArrayList<IListener>(1);
	private final Field field;
	private final String defaultValue;
	private String currentValue;

	public CommitMessageDescription(Field field, String defaultValue,
			String currentValue) {
		this.field = field;
		this.defaultValue = defaultValue;
		this.currentValue = currentValue;
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

	public void injectCurrentValueTo(ICommitMessageFactory factory)
			throws IllegalArgumentException, IllegalAccessException {
		if (Modifier.isFinal(field.getModifiers())) {
			System.err.printf("Field %s#%s needs to be made non final",
					field.getName());
			CommitMessageTemplate template = (CommitMessageTemplate) field
					.get(factory);
			template.setValue(currentValue);
		} else {
			field.set(factory, new CommitMessageTemplate(currentValue));
		}
	}

	public Field getField() {
		return field;
	}
}
