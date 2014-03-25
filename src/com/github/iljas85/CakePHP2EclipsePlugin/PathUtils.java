package com.github.iljas85.CakePHP2EclipsePlugin;

import org.eclipse.core.runtime.IPath;
import org.modeshape.common.text.Inflector;

public class PathUtils {
	public static final String APP_FOLDER = "app";
	public static final String VIEW_FOLDER = "View";
	
	public boolean isViewPath(IPath path) {
		//TODO consider plugins
		return path.segmentCount() > 3
				&& path.segment(1).equals(APP_FOLDER)
				&& path.segment(2).equals(VIEW_FOLDER);
	}

	public String getControllerName(IPath path) {
		//TODO consider plugins
		return path.segment(3) + "Controller";
	}

	public String getMethodName(IPath path) {
		String name = path.segment(4);
		name = name.replaceAll("\\.ctp$", "");
		Inflector inf = new Inflector();
		name = inf.capitalize(name);
		name = name.substring(0, 1).toLowerCase() + name.substring(1);
		return name;
	}
}
