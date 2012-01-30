/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit;

import org.eclipse.core.resources.IProject;

/**
 * Can be used to iterate over the repositories with enabled autocommit support.
 * 
 */
public interface IVersionControlSystem {

	IRepository getRepositoryFor(IProject project);

}
