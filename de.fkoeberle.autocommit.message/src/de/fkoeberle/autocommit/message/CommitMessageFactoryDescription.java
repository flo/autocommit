package de.fkoeberle.autocommit.message;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommitMessageFactoryDescription {
	private final Class<?> factoryClass;
	private final List<CommitMessageDescription> commitMessageDescriptions;

	public CommitMessageFactoryDescription(ICommitMessageFactory factory) {
		this.factoryClass = factory.getClass();
		this.commitMessageDescriptions = new ArrayList<CommitMessageDescription>();
		for (Field field : factoryClass.getFields()) {
			if (field.getType().equals(CommitMessageTemplate.class)) {
				Object templateObject;
				try {
					templateObject = field.get(factory);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				CommitMessageTemplate originalTemplate = (CommitMessageTemplate) templateObject;
				commitMessageDescriptions.add(new CommitMessageDescription(
						originalTemplate));
			}
		}
	}

	public String getTitle() {
		return factoryClass.getSimpleName();
	}

	public String getDescription() {
		return "dummy description";
	}

	public List<String> getArgumentDescriptions() {
		if (factoryClass.getName().contains("A")) {
			return Arrays.asList("dummy 1", "dummy 2", "addional dummy");
		} else {
			return Arrays.asList("dummy 1", "dummy 2");
		}
	}

	public List<CommitMessageDescription> getCommitMessageDescriptions() {
		return commitMessageDescriptions;
	}
}
