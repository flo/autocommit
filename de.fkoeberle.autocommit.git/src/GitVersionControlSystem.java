import de.fkoeberle.autocommit.IVersionControlSystem;
import org.eclipse.core.resources.IProject ;


public class GitVersionControlSystem implements IVersionControlSystem {

	@Override
	public void commit(IProject project, String message) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean hasCommittableChangesFor(IProject project) {
		// TODO Auto-generated method stub
		return false;
	}

}
