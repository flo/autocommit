/*
 * Copyright (C) 2012, Florian Köberle <florian@fkoeberle.de>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fkoeberle.autocommit.message.tex;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a headline and it's body of a LaTeX document.
 * 
 */
public class OutlineNode {
	private final OutlineNodeType type;
	private final String caption;
	private List<OutlineNode> childNodes;
	private final String document;
	private final int firstIndex;
	private final int contentStartIndex;
	private int exlusiveEndIndex;
	private int length;
	private String text;

	public OutlineNode(OutlineNodeType type, String caption, String document,
			int firstIndex, int contentStartIndex) {
		this.type = type;
		this.caption = caption;
		this.document = document;
		this.firstIndex = firstIndex;
		this.contentStartIndex = contentStartIndex;
		this.childNodes = new ArrayList<OutlineNode>();
	}

	public OutlineNodeType getType() {
		return type;
	}

	public List<OutlineNode> getChildNodes() {
		return childNodes;
	}

	public void setChildNodes(List<OutlineNode> childNodes) {
		this.childNodes = childNodes;
	}

	public String getCaption() {
		return caption;
	}

	public void addChild(OutlineNode node) {
		childNodes.add(node);
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getLength() {
		return length;
	}

	public String getText() {
		if (text == null) {
			text = document.substring(firstIndex, exlusiveEndIndex);
		}
		return text;
	}

	public int getFirstIndex() {
		return firstIndex;
	}

	public int getExlusiveEndIndex() {
		return exlusiveEndIndex;
	}

	@Override
	public String toString() {
		return getText();
	}

	public void setExlusiveEndIndex(int index) {
		this.exlusiveEndIndex = index;
	}

	public int getContentStartIndex() {
		return contentStartIndex;
	}

}
