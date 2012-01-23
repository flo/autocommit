/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.event.build;

import org.eclipse.ui.IStartup;

public class StartUp implements IStartup {

	@Override
	public void earlyStartup() {
		// do nothing except ensuring that this plugin must be loaded
	}

}
