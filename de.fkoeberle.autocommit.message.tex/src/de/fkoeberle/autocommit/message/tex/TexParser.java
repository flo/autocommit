package de.fkoeberle.autocommit.message.tex;

import static java.util.regex.Pattern.compile;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TexParser {
	Pattern COMMENT_PATTERN = compile("(#.*)(\n|\r)");
	Pattern HEADLINE_PATTERN = compile("\\\\(chapter|section|subsection|subsubsection)\\s*\\{(.*?)\\}");


	OutlineNode parse(String filename, CharSequence text) {
		CharSequence noCommentsText = withoutComments(text);
		Matcher headlineMatcher = HEADLINE_PATTERN.matcher(noCommentsText);
		Deque<OutlineNode> nodeStack = new ArrayDeque<OutlineNode>();
		nodeStack.addLast(new OutlineNode(OutlineNodeType.DOCUMENT, filename));
		while (headlineMatcher.find()) {
			String typeString = headlineMatcher.group(1);
			String caption = headlineMatcher.group(2);
			OutlineNodeType type = OutlineNodeType.typeOfCommand(typeString);
			OutlineNode node = new OutlineNode(type, caption);
			while (type.causesTheEndOf(nodeStack.getLast().getType())) {
				nodeStack.removeLast();
			}
			OutlineNode parent = nodeStack.getLast();
			parent.addChild(node);
			nodeStack.add(node);
		}
		return nodeStack.getFirst();
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
