package de.fkoeberle.autocommit;
import org.eclipse.core.resources.IProject ;

public interface IVersionControlSystem {
	void commit(IProject project, String message);
	boolean hasCommittableChangesFor(IProject project);
}
