/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.message.ui;

import java.nio.charset.Charset;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

public class UniqueIdTransfer extends ByteArrayTransfer {
	private static final Charset ASCII = Charset.forName("ASCII");
	private final String TYPE_NAME = "unique id";
	private final int TYPE_ID = registerType(TYPE_NAME);
	public static final UniqueIdTransfer INSTANCE = new UniqueIdTransfer();

	@Override
	protected String[] getTypeNames() {
		return new String[] { TYPE_NAME };
	}

	@Override
	protected int[] getTypeIds() {
		return new int[] { TYPE_ID };
	}

	@Override
	protected void javaToNative(Object object, TransferData transferData) {
		if (!isSupportedType(transferData)) {
			throw new IllegalArgumentException();
		}
		if (!(object instanceof Long)) {
			throw new IllegalArgumentException("Argument must be a long");
		}
		Long id = (Long) object;
		String idString = Long.toString(id);
		byte[] rawBytes = idString.getBytes(ASCII);
		super.javaToNative(rawBytes, transferData);
	}

	@Override
	protected Object nativeToJava(TransferData transferData) {
		// TODO Auto-generated method stub
		byte[] rawBytes = (byte[]) super.nativeToJava(transferData);
		String longAsString = new String(rawBytes, ASCII);
		return Long.valueOf(Long.parseLong(longAsString));
	}

}
