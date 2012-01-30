/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.git;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.eclipse.jgit.lib.ObjectId;

public class HashCacluatingDeltaVisitor implements GitFileSetDeltaVisitor {
	private final MessageDigest messageDigest;
	/**
	 * 0 can be assumed not to occur in the filename string and can thus be used
	 * as marker for an added file
	 */
	private static final byte[] ADDED_FILE_MAGIC_WORD = new byte[] { 0, 1 };
	private static final byte[] REMOVED_FILE_MAGIC_WORD = new byte[] { 0, 2 };
	private static final byte[] CHANGED_FILE_MAGIC_WORD = new byte[] { 0, 3 };
	private final byte[] buffer;
	private static final int BYTES_PER_OBJECT_ID = 20;

	public HashCacluatingDeltaVisitor() {
		try {
			messageDigest = MessageDigest.getInstance("SHA");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		this.buffer = new byte[BYTES_PER_OBJECT_ID];
	}

	@Override
	public void visitAddedFile(String path, ObjectId newObjectId)
			throws IOException {
		messageDigest.update(ADDED_FILE_MAGIC_WORD);
		messageDigest.update(path.getBytes());
		newObjectId.copyRawTo(buffer, 0);
		messageDigest.update(buffer);
	}

	@Override
	public void visitRemovedFile(String path, ObjectId oldObjectId)
			throws IOException {
		messageDigest.update(REMOVED_FILE_MAGIC_WORD);
		messageDigest.update(path.getBytes());
		oldObjectId.copyRawTo(buffer, 0);
		messageDigest.update(buffer);
	}

	@Override
	public void visitChangedFile(String path, ObjectId oldObjectId,
			ObjectId newObjectId) throws IOException {
		messageDigest.update(CHANGED_FILE_MAGIC_WORD);
		messageDigest.update(path.getBytes());
		oldObjectId.copyRawTo(buffer, 0);
		messageDigest.update(buffer);
		newObjectId.copyRawTo(buffer, 0);
		messageDigest.update(buffer);
	}

	byte[] buildHash() {
		return messageDigest.digest();
	}

}
