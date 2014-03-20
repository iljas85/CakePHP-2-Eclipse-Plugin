package com.github.iljas85.CakePHP2EclipsePlugin.test;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.eclipse.core.runtime.IPath;

import com.github.iljas85.CakePHP2EclipsePlugin.Activator;

import junit.framework.TestCase;

public class JpaTest extends TestCase {

	private EntityManager getEntityManager() {

		Map<String, String> dbProps = new HashMap<String, String>();

		dbProps.put("javax.persistence.jdbc.driver",
				"org.h2.Driver");
		
		IPath dbPath = Activator.getDefault().getStateLocation();
		StringBuilder buf = new StringBuilder("jdbc:h2:").append(dbPath.append(
				"test-db").toOSString());
		dbProps.put("javax.persistence.jdbc.url",
				buf.toString());
			
		dbProps.put("javax.persistence.jdbc.password",
				"");
		dbProps.put("javax.persistence.jdbc.user",
				"");

		EntityManagerFactory fact = Persistence.createEntityManagerFactory("myTestPU", dbProps);
		return fact.createEntityManager();
	}
	
	public EntityManager em;
	
	protected void setUp() throws Exception {
		
	}
	
	protected void tearDown() throws Exception {
		
	}
	
	public void testEM() {
		em = getEntityManager();
		assertNotNull(em);
		
		em.getTransaction().begin();
		Employee emp = new Employee(158);
		emp.setName("me");
		emp.setSalary(200);
		em.persist(emp);
		em.getTransaction().commit();
		
		
		TypedQuery<Employee> query = em.createQuery("SELECT e FROM Employee e",
				Employee.class);
		List<Employee> emps = query.getResultList();
		assertTrue(emps.size() > 0);
		
		TypedQuery<Employee> query2 = em.createQuery("SELECT e FROM Employee e WHERE e.name = :name",
				Employee.class);
		query2.setParameter("name", "some");
		boolean fail = false;
		try {
			Employee res = query2.getSingleResult();
		} catch (NoResultException e) {
			fail = true;
		}
		assertTrue(fail);
		
		em.close();
	}
}
