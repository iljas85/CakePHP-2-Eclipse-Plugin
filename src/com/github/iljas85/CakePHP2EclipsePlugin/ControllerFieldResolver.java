package com.github.iljas85.CakePHP2EclipsePlugin;

import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.eclipse.php.internal.core.typeinference.PHPClassType;
import org.eclipse.php.internal.core.util.text.TextSequence;

@SuppressWarnings("restriction")
public class ControllerFieldResolver {

	private String effectiveClassName;
	private int offset = 0;
	private HashMap<String, String> fields;

	public ControllerFieldResolver(TextSequence inputString) {

		Pattern factoryPattern = Pattern
				.compile("\\s*[$]this\\s*[-][>]\\s*(\\w+)\\s*");

		Matcher classNameSearcher = factoryPattern.matcher(inputString);
		if (classNameSearcher.find()) {
			effectiveClassName = getClassname(classNameSearcher.group(1));
			offset = classNameSearcher.end();
		}
		
		fields = new HashMap<String, String>();
		fields.put("Html", "HtmlHelper");
	}

	/**
	 * Returns true if input string contains factory call.
	 *
	 * @return true or false
	 */
	public boolean containsFactoryCall() {
		return !effectiveClassName.isEmpty() 
				&& fields.containsKey(effectiveClassName);
	}

	/**
	 * Returns PHP type deducted from factory call
	 *
	 * @return PHP class type
	 */
	public PHPClassType getClassType() {
		return new PHPClassType(fields.get(effectiveClassName));
	}

	/**
	 * Returns offset in context string where Factory::get static call ends.
	 *
	 * @return end position of broker call
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * Transforms supplied parameter into PHP class name according to defined
	 * conventions.
	 *
	 * 'abc' -> My_Abc
	 *
	 * @param className
	 * @return
	 */
	public static String getClassname(String className) {

		return className;
		
		/*if (className.length() < 1) {
			return "";
		}

		String classPrefix = "Prefix_";
		String capitalizedClassName = className.substring(0, 1).toUpperCase()
				+ className.substring(1);

		return classPrefix + capitalizedClassName;*/
	}
}