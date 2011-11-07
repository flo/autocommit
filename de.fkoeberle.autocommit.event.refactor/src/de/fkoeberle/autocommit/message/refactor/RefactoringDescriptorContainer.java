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
