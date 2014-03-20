package com.github.iljas85.CakePHP2EclipsePlugin.index;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;

import com.github.iljas85.CakePHP2EclipsePlugin.Activator;

public class Schema {
	public static final String VERSION = "0.1.0"; //$NON-NLS-1$
	
	public static final String SCHEMA_VERSION = "cacheSchemaVersion"; //$NON-NLS-1$

	/**
	 * Creates the database schema
	 *
	 */
	public void initialize() {
		// Store new schema version:
		storeSchemaVersion(VERSION);
	}
	
	/**
	 * Checks whether the schema version is compatible with the stored one.
	 */
	public boolean isCompatible() {
			String storedVersion = getStoredSchemaVersion();
			if (storedVersion != null && VERSION.equals(storedVersion)) {
					return true;
			}
			return false;
	}
	
	private String getStoredSchemaVersion() {
		return Platform.getPreferencesService().getString(Activator.PLUGIN_ID,
						SCHEMA_VERSION, null, null);
	}
	
	private void storeSchemaVersion(String newVersion) {
		IEclipsePreferences node = new InstanceScope()
						.getNode(Activator.PLUGIN_ID);
		node.put(SCHEMA_VERSION, newVersion);
		try {
				node.flush();
		} catch (BackingStoreException e) {
		}
	}
}
