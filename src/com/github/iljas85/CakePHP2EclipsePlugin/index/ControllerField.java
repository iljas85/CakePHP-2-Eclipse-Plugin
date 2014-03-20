package com.github.iljas85.CakePHP2EclipsePlugin.index;

import javax.persistence.*;

@Entity
public class ControllerField {
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	private String name;
	private ControllerFieldType type;
	private String className;
	@ManyToOne
	private Controller controller;
	
	public ControllerField() {}
	public ControllerField(int id) { this.id = id; }
	
	public int getId() { return id; }
	public void setId(int id) { this.id = id; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public ControllerFieldType getType() { return type; }
	public void setType(ControllerFieldType type) { this.type = type; }
	public String getClassName() { return className; }
	public void setClassName(String className) { this.className = className; }
	public Controller getController() { return controller; }
	public void setController(Controller controller) { this.controller = controller; }
}
