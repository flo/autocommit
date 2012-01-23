/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.message.refactor;

import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;

public class RefactoringDescriptorContainer {
	private RefactoringDescriptor refactoringDescriptor;

	public RefactoringDescriptorContainer() {
		this.refactoringDescriptor = null;
	}

	public RefactoringDescriptorContainer(
			RefactoringDescriptor refactoringDescriptor) {
		this.refactoringDescriptor = refactoringDescriptor;
	}

	public RefactoringDescriptor getRefactoringDescriptor() {
		return refactoringDescriptor;
	}

	public void setRefactoringDescriptor(
			RefactoringDescriptor refactoringDescriptor) {
		this.refactoringDescriptor = refactoringDescriptor;
	}

}
