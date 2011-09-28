package de.fkoeberle.autocommit.event.build;

import org.eclipse.ui.IStartup;

public class StartUp implements IStartup {

	@Override
	public void earlyStartup() {
		// do nothing except ensuring that this plugin must be loaded
	}

}
