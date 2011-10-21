package de.fkoeberle.autocommit.message.java;

import java.util.Set;


public class CommonParentPackageFinder {
	private String first;
	private int lengthOfPrefix;
	
	public void checkPackage(String p) {
		if (first == null) {
			first = p;
			lengthOfPrefix = p.length();
		} else {
			// default package handling:
			if ((lengthOfPrefix == 0)) {
				if (p.length() != 0) {
					lengthOfPrefix = -1;
				}
				return;
			}
			for (int i = 0; i < lengthOfPrefix; i++ ) {
				if ((i >= p.length())) {
					lengthOfPrefix = first.lastIndexOf('.', i);
					return;
				} else if ((first.charAt(i) != p.charAt(i))) {
					lengthOfPrefix = first.lastIndexOf('.', i - 1);
					return;
				}
			}
			if (lengthOfPrefix < p.length()) {
				lengthOfPrefix = p.lastIndexOf('.', lengthOfPrefix);
			}
		}
	}
	
	public String getCommonPackage() {
		if ((first == null) || (lengthOfPrefix == -1))
			return null;
		return first.substring(0, lengthOfPrefix);
	}

	public void checkPackages(Set<String> packages) {
		for (String p : packages) {
			checkPackage(p);
		}
	}
}
