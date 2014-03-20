package com.github.iljas85.CakePHP2EclipsePlugin.index;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.jobs.ILock;
import org.eclipse.core.runtime.jobs.Job;
import org.h2.tools.DeleteDbFiles;

import com.github.iljas85.CakePHP2EclipsePlugin.Activator;
import com.github.iljas85.CakePHP2EclipsePlugin.IShutdownListener;


public class CakePHP2DbFactory {
	
	private static final String DB_NAME = "cake-php-2-model"; //$NON-NLS-1$
	private static final String DB_USER = ""; //$NON-NLS-1$
	private static final String DB_PASS = ""; //$NON-NLS-1$

	private static final int DB_LOCK_MODE = 0; //$NON-NLS-1$
	private static final String DB_CACHE_TYPE = "LRU"; //$NON-NLS-1$
	private static final int DB_CACHE_SIZE = 32000; //$NON-NLS-1$

	private static ILock instanceLock = Job.getJobManager().newLock();
	
	private static CakePHP2DbFactory instance = null;
	
	public static CakePHP2DbFactory getInstance() {

		if (instance == null) {
			try {
				instanceLock.acquire();
				instance = new CakePHP2DbFactory();
				/*
				 * Explicitly register shutdown handler, so it
				 * would be disposed only if class was loaded.
				 * 
				 * We don't want static initialization code to
				 * be executed during framework shutdown.
				 */
				Activator
				.addShutdownListener(new IShutdownListener() {
					public void shutdown() {
						if (instance != null) {
							try {
								instance.dispose();
							} catch (SQLException e) {
								/*Logger.logException(e);*/
							}
							instance = null;
						}
					}
				});

			} catch (Exception e) {
				/*Logger.logException(e);*/
			} finally {
				instanceLock.release();
			}
		}

		return instance;
	}
	
	private CakePHP2DbFactory() throws Exception {
		IPath dbPath = Activator.getDefault().getStateLocation();
		
		Schema schema = new Schema();
		if (!schema.isCompatible()) {
			// Destroy schema by removing DB (if exists)
			DeleteDbFiles.execute(dbPath.toOSString(), DB_NAME,
					true);
			schema.initialize();
		}
		
		em = createEntityManager(dbPath);
	}
	
	private EntityManager createEntityManager(IPath dbPath) {

		Map<String, String> dbProps = new HashMap<String, String>();

		dbProps.put("javax.persistence.jdbc.driver",
				"org.h2.Driver");
		
		String connString = getConnectionString(dbPath);
		dbProps.put("javax.persistence.jdbc.url",
				connString);
			
		dbProps.put("javax.persistence.jdbc.password",
				DB_PASS);
		dbProps.put("javax.persistence.jdbc.user",
				DB_USER);

		EntityManagerFactory fact = Persistence.createEntityManagerFactory("indexPU", dbProps);
		return fact.createEntityManager();
	}
	
	/**
	 * Generates connection string using user preferences
	 *
	 * @param dbPath Path to the database files
	 * @return
	 */
	private String getConnectionString(IPath dbPath) {

			StringBuilder buf = new StringBuilder("jdbc:h2:").append(dbPath.append(
							DB_NAME).toOSString());

			buf.append(";UNDO_LOG=0");
			buf.append(";LOCK_MODE=").append(DB_LOCK_MODE);

			buf.append(";CACHE_TYPE=").append(DB_CACHE_TYPE);

			buf.append(";CACHE_SIZE=").append(DB_CACHE_SIZE);

			return buf.toString();
	}
	
	private EntityManager em;
	
	public void dispose() throws SQLException {
		if (em != null) {
			em.close();
			em = null;
		}
	}
	
	public EntityManager getEntityManager() {
		return em;
	}
}
