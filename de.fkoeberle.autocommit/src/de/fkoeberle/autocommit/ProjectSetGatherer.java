/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;

public final class ProjectSetGatherer implements IResourceDeltaVisitor {
	private final Set<IProject> projects = new HashSet<IProject>();

	@Override
	public boolean visit(IResourceDelta delta) throws CoreException {
		IProject project = delta.getResource().getProject();
		if (project == null) {
			// workspace root -> iterate further
			return true;
		} else {
			projects.add(project);
			return false;
		}
	}

	public Set<IProject> getProjects() {
		return projects;
	}
}