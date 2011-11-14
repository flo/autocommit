package de.fkoeberle.autocommit.message.java;

import java.io.IOException;
import java.util.EnumSet;

public interface IDelta {
	/**
	 * 
	 * @return which kind of changes this delta represents. The result must not
	 *         be modified.
	 */
	EnumSet<BodyDeclarationChangeType> getChangeTypes() throws IOException;

}
