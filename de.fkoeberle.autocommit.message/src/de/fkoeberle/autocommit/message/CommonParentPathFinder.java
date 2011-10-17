package de.fkoeberle.autocommit.message;

public class CommonParentPathFinder {
	private String firstPath;
	private int lengthOfPrefix;

	public void checkPath(String currentPath) {
		if (firstPath == null) {
			firstPath = currentPath;
			lengthOfPrefix = currentPath.length();
		} else {
			for (int i = 0; i < lengthOfPrefix; i++) {
				if ((i >= currentPath.length())) {
					lengthOfPrefix = firstPath.lastIndexOf('/', i) + 1;
					return;
				} else if ((firstPath.charAt(i) != currentPath.charAt(i))) {
					lengthOfPrefix = firstPath.lastIndexOf('/', i - 1) + 1;
					return;
				}
			}
			if (lengthOfPrefix < currentPath.length()) {
				lengthOfPrefix = currentPath.lastIndexOf('/', lengthOfPrefix) + 1;
			}
		}
	}

	public String getCommonPath() {
		if ((firstPath == null) || (lengthOfPrefix == -1))
			return null;
		return firstPath.substring(0, lengthOfPrefix);
	}
}
