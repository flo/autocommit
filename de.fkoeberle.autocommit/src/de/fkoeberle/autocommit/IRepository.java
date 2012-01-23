/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit;

import java.io.IOException;

public interface IRepository {
	void commit() throws IOException;

	/**
	 * 
	 * @return true, if it could be verified that there are no uncomitted
	 *         changes. If it fails to determine if there are changes it returns
	 *         true.
	 */
	boolean noUncommittedChangesExist() throws IOException;

	/**
	 * When there has been no additional changes when commit gets called then
	 * session.add(data) will be called. Does nothing if there are no uncommited
	 * changes.
	 */
	void addSessionDataForUncommittedChanges(Object data) throws IOException;
}
