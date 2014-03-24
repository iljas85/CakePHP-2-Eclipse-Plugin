package com.github.iljas85.CakePHP2EclipsePlugin;

import org.eclipse.core.runtime.IPath;

public class PathUtils {
	public static final String APP_FOLDER = "app";
	public static final String VIEW_FOLDER = "View";
	
	public boolean isViewPath(IPath path) {
		return path.segmentCount() > 3
				&& path.segment(1).equals(APP_FOLDER)
				&& path.segment(2).equals(VIEW_FOLDER);
	}
}
