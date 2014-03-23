package com.github.iljas85.CakePHP2EclipsePlugin;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.php.core.compiler.PHPSourceElementRequestorExtension;
import org.eclipse.dltk.ast.Modifiers;
import org.eclipse.dltk.ast.declarations.TypeDeclaration;
import org.eclipse.dltk.ast.expressions.Expression;
import org.eclipse.php.internal.core.compiler.ast.nodes.ArrayCreation;
import org.eclipse.php.internal.core.compiler.ast.nodes.ArrayElement;
import org.eclipse.php.internal.core.compiler.ast.nodes.ClassDeclaration;
import org.eclipse.php.internal.core.compiler.ast.nodes.PHPFieldDeclaration;
import org.eclipse.php.internal.core.compiler.ast.nodes.Scalar;
import org.eclipse.dltk.compiler.ISourceElementRequestor;
import org.modeshape.common.text.Inflector;

import com.github.iljas85.CakePHP2EclipsePlugin.index.CakePHP2Indexer;
import com.github.iljas85.CakePHP2EclipsePlugin.index.ControllerFieldType;

@SuppressWarnings("restriction")
public class SourceElementRequestor extends PHPSourceElementRequestorExtension {
	
	private ClassDeclaration currentClass;
	private Set<Scalar> deferredFields = new HashSet<Scalar>();
	private String fileName = "";
	
	public boolean visit(TypeDeclaration s) throws Exception {
		if (s instanceof ClassDeclaration) {
			currentClass = (ClassDeclaration) s;
			fileName = getSourceModule().getFileName();
			cleanClassFields();
			addDefaultModel();
		}
		return true;
	}
	
	private void cleanClassFields() throws Exception {
		CakePHP2Indexer indexer = CakePHP2Indexer.getInstance();
		indexer.removeControllerFields(fileName, currentClass.getName());
	}
	
	/**
	 * Adds model associated with controller,
	 * e.g. Post for PostsController
	 * 
	 * @throws Exception
	 */
	private void addDefaultModel() throws Exception {
		Inflector inf = new Inflector();
		String modelName = inf.singularize(currentClass.getName().replaceAll("Controller$", ""));
		Scalar model = new Scalar(currentClass.getNameStart(), currentClass.getNameEnd(), modelName, Scalar.TYPE_STRING);
		deferredFields.add(model);
		
		CakePHP2Indexer indexer = CakePHP2Indexer.getInstance();
		indexer.addControllerField(fileName, currentClass.getName(), model.getValue(), ControllerFieldType.MODEL);
	}
	
	public boolean endvisit(TypeDeclaration s) throws Exception {
		currentClass = null;
		fileName = "";
		
		for (Scalar field : deferredFields) {
			ISourceElementRequestor.FieldInfo fieldInfo =
				new ISourceElementRequestor.FieldInfo();
			fieldInfo.name = "$" + field.getValue().replaceAll("['\"]", "");
			fieldInfo.modifiers = Modifiers.AccPublic;
			fieldInfo.declarationStart = field.sourceStart();
			fieldInfo.nameSourceStart = field.sourceStart();
			fieldInfo.nameSourceEnd= field.sourceEnd();
			fRequestor.enterField(fieldInfo);
			fRequestor.exitField(field.sourceEnd());
		}
		deferredFields.clear();
		
		return true;
	}
	
	public boolean visit(PHPFieldDeclaration s) throws Exception {
		//TODO consider case Js => JsPrototype
		if (s.getVariableValue() instanceof ArrayCreation) {
			ControllerFieldType type = ControllerFieldType.MODEL;
			CakePHP2Indexer indexer = CakePHP2Indexer.getInstance();
			boolean magicField = false;
			if (s.getName().equals("$helpers")) {
				//Html -> HtmlHelper
				type = ControllerFieldType.HELPER;
				magicField = true;
			}
			if (s.getName().equals("$components")) {
				//Session -> SessionComponent
				type = ControllerFieldType.COMPONENT;
				magicField = true;
			}
			if (s.getName().equals("$uses")) {
				type = ControllerFieldType.MODEL;
				magicField = true;
			}
			if (magicField) {
				ArrayCreation value = (ArrayCreation)s.getVariableValue();
				for (ArrayElement elem : value.getElements()) {
					Expression name = elem.getValue();
					if (name instanceof Scalar) {
						Scalar scalar = (Scalar) name;
						if (scalar.getScalarType() == Scalar.TYPE_STRING) {
							deferredFields.add(scalar);
							indexer.addControllerField(fileName, currentClass.getName(), scalar.getValue().replaceAll("['\"]", ""), type);
						}
					}
				}
			}
		}
		
		return true;
	}
	
	public boolean visitGeneral(org.eclipse.dltk.ast.ASTNode node) throws Exception {
		if (node instanceof PHPFieldDeclaration) {
			return visit((PHPFieldDeclaration)node);
		}
		
		return super.visitGeneral(node);
	}
}
