/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
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
