/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.message.tex;

import static java.util.regex.Pattern.compile;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.fkoeberle.autocommit.message.InjectedBySession;
import de.fkoeberle.autocommit.message.Session;

/**
 * This is a helper class to create {@link OutlineNode} data structures. It
 * should be used as an field annotated with {@link InjectedBySession} which in
 * turn gets initialized by a {@link Session} object. Even if it does not cache
 * data yet, it might do so in future.
 * 
 */
public class TexParser {
	Pattern COMMENT_PATTERN = compile("(#.*)(\n|\r)");
	Pattern HEADLINE_PATTERN = compile("\\\\(chapter|section|subsection|subsubsection)\\s*\\{(.*?)\\}");

	public OutlineNode parse(String documentName, String text) {
		CharSequence noCommentsText = withoutComments(text);
		Matcher headlineMatcher = HEADLINE_PATTERN.matcher(noCommentsText);
		Deque<OutlineNode> nodeStack = new ArrayDeque<OutlineNode>();
		OutlineNode root = new OutlineNode(OutlineNodeType.DOCUMENT,
				documentName, text, 0, 0);
		nodeStack.addLast(root);
		while (headlineMatcher.find()) {
			String typeString = headlineMatcher.group(1);
			String caption = headlineMatcher.group(2);
			OutlineNodeType type = OutlineNodeType.typeOfCommand(typeString);
			int startIndex = headlineMatcher.start();
			int contentStartIndex = headlineMatcher.end();
			OutlineNode node = new OutlineNode(type, caption, text, startIndex,
					contentStartIndex);
			while (type.causesTheEndOf(nodeStack.getLast().getType())) {
				OutlineNode last = nodeStack.removeLast();
				last.setExlusiveEndIndex(startIndex);
			}
			OutlineNode parent = nodeStack.getLast();
			parent.addChild(node);
			nodeStack.add(node);
		}
		for (OutlineNode node : nodeStack) {
			node.setExlusiveEndIndex(text.length());
		}
		return root;
	}

	static CharSequence withoutComments(CharSequence text) {
		boolean commentMode = false;
		StringBuilder stringBuilder = new StringBuilder(text.length());
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c == '%') {
				commentMode = true;
			} else if (c == '\n' || c == '\r') {
				commentMode = false;
			}
			if (commentMode) {
				stringBuilder.append('%');
			} else {
				stringBuilder.append(c);
			}
		}
		CharSequence noCommentsText = stringBuilder;
		return noCommentsText;
	}
}
