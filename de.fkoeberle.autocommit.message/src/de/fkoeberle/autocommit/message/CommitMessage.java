package de.fkoeberle.autocommit.message;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
public @interface CommitMessage {

}
