package com.github.iljas85.CakePHP2EclipsePlugin.ControllerField;

import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.eclipse.php.internal.core.typeinference.PHPClassType;
import org.eclipse.php.internal.core.util.text.TextSequence;

import com.github.iljas85.CakePHP2EclipsePlugin.index.CakePHP2Indexer;

@SuppressWarnings("restriction")
public class Resolver {

	private String fieldName = "";
	private int offset = 0;
	private HashMap<String, String> fields = new HashMap<String, String>();
	private String controllerName;

	public Resolver(TextSequence inputString, String className) {

		if (!className.endsWith("Controller")) {
			return;
		}
		else
		{
			controllerName = className;
		}

		
		Pattern factoryPattern = Pattern
				.compile("\\s*[$]this\\s*[-][>]\\s*(\\w+)\\s*");

		Matcher classNameSearcher = factoryPattern.matcher(inputString);
		if (classNameSearcher.find()) {
			fieldName = getFieldName(classNameSearcher.group(1));
			offset = classNameSearcher.end();
			
			collectFields();
		}
	}
	
	private void collectFields() {
		try {
			CakePHP2Indexer indexer = CakePHP2Indexer.getInstance();
			fields.putAll(indexer.getFieldsForController(controllerName));
			fields.putAll(indexer.getFieldsForController("AppController"));
		} catch (Exception e) {
			//Logger.log(e.getMessage());
		}
		
	}

	/**
	 * Returns true if input string contains controller magic field access
	 *
	 * @return true or false
	 */
	public boolean containsFactoryCall() {
		return !fieldName.isEmpty() 
				&& fields.containsKey(fieldName);
	}

	/**
	 * Returns PHP controller magic field
	 *
	 * @return PHP class type
	 */
	public PHPClassType getClassType() {
		return new PHPClassType(fields.get(fieldName));
	}

	/**
	 * Returns offset in context string where controller magic field access ends.
	 *
	 * @return end position of broker call
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * Transforms supplied parameter into PHP class field name according to defined
	 * conventions.
	 *
	 * @param className
	 * @return
	 */
	public static String getFieldName(String fieldName) {
		return fieldName;
	}
}