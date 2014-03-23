package com.github.iljas85.CakePHP2EclipsePlugin.index;

import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import com.github.iljas85.CakePHP2EclipsePlugin.test.Employee;

public class CakePHP2Indexer {

	private static CakePHP2Indexer instance = null;
	
	private CakePHP2DbFactory factory;
	private EntityManager em;
	
	private CakePHP2Indexer() throws Exception {
		factory = CakePHP2DbFactory.getInstance();
		em = factory.getEntityManager();
	}
	
	public static CakePHP2Indexer getInstance() throws Exception {
		if (instance == null)
			instance = new CakePHP2Indexer();
		
		return instance;		
	}
	
	public HashMap<String, String> getFieldsForController(String name) {
		HashMap<String, String> result = new HashMap<String, String>();
		
		TypedQuery<ControllerField> query = em
			.createQuery(
				"SELECT f FROM ControllerField f WHERE f.controller.name = :name",
				ControllerField.class)
			.setParameter("name", name);
		List<ControllerField> emps = query.getResultList();
		
		for (ControllerField field: emps) {
			result.put(field.getName(), field.getClassName());
		}
		
		return result;
	}
	
	public void addControllerField(String fileName, String controllerName, String fieldName, ControllerFieldType fieldType) {
		TypedQuery<Controller> query = em
			.createQuery(
				"SELECT c FROM Controller c WHERE c.name = :name AND c.fileName = :fileName",
				Controller.class)
			.setParameter("name", controllerName)
			.setParameter("fileName", fileName);
		Controller ctrl = null;
		try {
			ctrl = query.getSingleResult();
		} catch (NoResultException e) {
			em.getTransaction().begin();
			ctrl = new Controller();
			ctrl.setName(controllerName);
			ctrl.setFileName(fileName);
			em.persist(ctrl);
			em.getTransaction().commit();
		}
		
		
		TypedQuery<ControllerField> query2 = em
				.createQuery(
					"SELECT f FROM ControllerField f WHERE f.name = :fname AND f.controller.name = :cname AND f.type = :type AND f.controller.fileName = :fileName",
					ControllerField.class)
				.setParameter("cname", controllerName)
				.setParameter("fname", fieldName)
				.setParameter("type", fieldType)
				.setParameter("fileName", fileName);
		ControllerField field = null;
		try {
			field = query2.getSingleResult();
		} catch (NoResultException e) {
			em.getTransaction().begin();
			field = new ControllerField();
			field.setName(fieldName);
			field.setType(fieldType);
			field.setClassName(getClassNameForControllerField(fieldName, fieldType));
			field.setController(ctrl);
			em.persist(field);
			em.getTransaction().commit();
		}
	}
	
	private String getClassNameForControllerField(String fieldName, ControllerFieldType fieldType) {
		String postfix = "";
		if (fieldType == ControllerFieldType.HELPER)
			postfix = "Helper";
		else if (fieldType == ControllerFieldType.COMPONENT)
			postfix = "Component";
		else if (fieldType == ControllerFieldType.MODEL)
			postfix = "";
		
		return fieldName + postfix;
	}
	
	public void removeControllerFields(String fileName, String controllerName) {
		em.getTransaction().begin();
		em.createQuery("DELETE FROM ControllerField f " +
				"WHERE f.controller.name = :name AND f.controller.fileName = :fileName")
			.setParameter("name", controllerName)
			.setParameter("fileName", fileName)
			.executeUpdate();
		em.getTransaction().commit();
	}
}
