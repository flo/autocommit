package de.fkoeberle.autocommit.message.java.test;

import org.eclipse.jdt.core.dom.MethodDeclaration;

public class MethodDelta {
	private final MethodDeclaration oldMethod;
	private final MethodDeclaration newMethod;

	public MethodDelta(MethodDeclaration oldMethod, MethodDeclaration newMethod) {
		this.oldMethod = oldMethod;
		this.newMethod = newMethod;
	}

	public MethodDeclaration getNewMethod() {
		return newMethod;
	}

	public MethodDeclaration getOldMethod() {
		return oldMethod;
	}
}
