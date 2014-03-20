package com.github.iljas85.CakePHP2EclipsePlugin.index;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.*;

@Entity
public class Controller {
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	private String name;
	@OneToMany(mappedBy="controller",fetch=FetchType.LAZY)
	private Collection<ControllerField> controllerFields = new ArrayList<ControllerField>();;
	
	public Controller() {}
	public Controller(int id) { this.id = id; }
	
	public int getId() { return id; }
	public void setId(int id) { this.id = id; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public Collection<ControllerField> getControllerFields() { return controllerFields; }
}
