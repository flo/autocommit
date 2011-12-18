package de.fkoeberle.autocommit.message;

import java.lang.reflect.Field;

public class DummyCommitMessageUtil {
	public static void insertUniqueCommitMessagesWithNArgs(
			ICommitMessageFactory factory, int numberOfArguments) {
		for (Field field : factory.getClass().getDeclaredFields()) {
			CommitMessage annotation = field.getAnnotation(CommitMessage.class);
			if (annotation != null) {
				if (field.getType().equals(CommitMessageTemplate.class)) {
					String s = createMessageFor(field, numberOfArguments);
					CommitMessageTemplate template = new CommitMessageTemplate(
							s);
					try {
						field.set(factory, template);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				} else {
					throw new RuntimeException(
							String.format(
									"Field %s of class %s is annotated with %s, but this field type isn't supported by the annotation.",
									field.getName(),
									factory.getClass().getSimpleName(),
									CommitMessageTemplate.class.getSimpleName(),
									CommitMessage.class.getSimpleName()));
				}

			} else {
				if (field.getType().equals(CommitMessageTemplate.class)) {
					throw new RuntimeException(
							String.format(
									"Field %s of class %s is of type %s but is not annotated with %s",
									field.getName(),
									factory.getClass().getSimpleName(),
									CommitMessageTemplate.class.getSimpleName(),
									CommitMessage.class.getSimpleName()));
				}
			}
		}

	}

	private static String createMessageFor(Field field, int numberOfArguments) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(field.getName());
		stringBuilder.append("(");
		for (int i = 0; i < numberOfArguments; i++) {
			if (i != 0) {
				stringBuilder.append(", ");
			}
			stringBuilder.append("{");
			stringBuilder.append(Integer.toString(i));
			stringBuilder.append("}");
		}
		stringBuilder.append(")");
		String s = stringBuilder.toString();
		return s;
	}
}
