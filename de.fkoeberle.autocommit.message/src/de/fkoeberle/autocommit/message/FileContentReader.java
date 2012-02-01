/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.message;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FileContentReader extends
		AbstractViewWithCache<Map<IFileContent, String>> {

	public String getStringFor(IFileContent fileContent) throws IOException {
		Map<IFileContent, String> map = getCachableValue();
		String text = map.get(fileContent);
		if (text == null) {
			byte[] bytes = fileContent.getBytesForReadOnlyPurposes();
			text = new String(bytes);
			map.put(fileContent, text);
		}
		return text;
	}

	@Override
	protected Map<IFileContent, String> determineCachableValue()
			throws IOException {
		return new HashMap<IFileContent, String>();
	}

}
