package de.fkoeberle.autocommit;

import java.util.Arrays;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

public class Nature implements IProjectNature {
	public static final String ID = "de.fkoeberle.autocommit.nature";

	private IProject project;

	@Override
	public void configure() throws CoreException {
		// do nothing
	}

	@Override
	public void deconfigure() throws CoreException {
		// do nothing
	}

	@Override
	public IProject getProject() {
		return project;
	}

	@Override
	public void setProject(IProject project) {
		this.project = project;
	}

	public static void removeSelfFrom(IProject project) throws CoreException {
		IProjectDescription projectDescription = project.getDescription();
		String[] natureIds = projectDescription.getNatureIds();
		for (int i = 0; i < natureIds.length; i++) {
			if (natureIds[i] == Nature.ID) {
				natureIds[natureIds.length - 1] = natureIds[i];
				break;
			}
		}
		natureIds = Arrays.copyOf(natureIds, natureIds.length - 1);
		projectDescription.setNatureIds(natureIds);
		project.setDescription(projectDescription, null);
	}

	public static void addSelfTo(IProject project) throws CoreException {
		IProjectDescription projectDescription = project.getDescription();
		String[] natureIds = projectDescription.getNatureIds();
		natureIds = Arrays.copyOf(natureIds, natureIds.length + 1);
		natureIds[natureIds.length - 1] = Nature.ID;
		projectDescription.setNatureIds(natureIds);
		project.setDescription(projectDescription, null);
	}

}
