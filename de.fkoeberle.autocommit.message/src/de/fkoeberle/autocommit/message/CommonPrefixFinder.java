package de.fkoeberle.autocommit.message;

public class CommonPrefixFinder {
	private String first;
	private int lengthOfPrefix;
	
	public void checkForShorterPrefix(String s) {
		if (first == null) {
			first = s;
			lengthOfPrefix = s.length();
		} else {
			for (int i = 0; i < lengthOfPrefix; i++ ) {
				if (first.charAt(i) != s.charAt(i)) {
					lengthOfPrefix = i;
					return;
				}
			}
		}
	}
	
	String getPrefix() {
		if (first == null)
			return null;
		return first.substring(0, lengthOfPrefix);
	}
}
