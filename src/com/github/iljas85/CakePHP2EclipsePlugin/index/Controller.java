package com.github.iljas85.CakePHP2EclipsePlugin.index;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.*;

@Entity
public class Controller {
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	private String name;
	private String fileName;
	@OneToMany(mappedBy="controller",fetch=FetchType.LAZY)
	private Collection<ControllerField> controllerFields = new ArrayList<ControllerField>();
	@OneToMany(mappedBy="controller",fetch=FetchType.LAZY)
	private Collection<VariableForView> variablesForViews = new ArrayList<VariableForView>();
	
	public Controller() {}
	public Controller(int id) { this.id = id; }
	
	public int getId() { return id; }
	public void setId(int id) { this.id = id; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getFileName() { return fileName; }
	public void setFileName(String fileName) { this.fileName = fileName; }
	public Collection<ControllerField> getControllerFields() { return controllerFields; }
	public Collection<VariableForView> getVariablesForViews() { return variablesForViews; }
}
