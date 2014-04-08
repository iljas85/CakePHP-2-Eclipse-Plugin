package com.github.iljas85.CakePHP2EclipsePlugin.index;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class VariableForView {
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	private String name;
	private String type;
	private String methodName;
	@ManyToOne
	private Controller controller;
	
	public VariableForView() {}
	public VariableForView(int id) { this.id = id; }
	
	public int getId() { return id; }
	public void setId(int id) { this.id = id; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getType() { return type; }
	public void setType(String type) { this.type = type; }
	public String getMethodName() { return methodName; }
	public void setMethodName(String methodName) { this.methodName = methodName; }
	public Controller getController() { return controller; }
	public void setController(Controller controller) { this.controller = controller; }
}
