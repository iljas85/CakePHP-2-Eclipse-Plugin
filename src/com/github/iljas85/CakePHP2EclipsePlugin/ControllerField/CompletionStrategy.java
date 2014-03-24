package com.github.iljas85.CakePHP2EclipsePlugin.ControllerField;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.IField;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ITypeHierarchy;
import org.eclipse.php.core.codeassist.ICompletionContext;
import org.eclipse.php.core.codeassist.ICompletionStrategy;
import org.eclipse.php.internal.core.codeassist.ICompletionReporter;
import org.eclipse.php.internal.core.codeassist.contexts.AbstractCompletionContext;
import org.eclipse.php.internal.core.codeassist.contexts.ClassMemberContext;
import org.eclipse.php.internal.core.codeassist.strategies.ClassMembersStrategy;
import org.eclipse.php.internal.core.typeinference.PHPModelUtils;
import org.eclipse.php.internal.core.util.text.PHPTextSequenceUtilities;
import org.eclipse.php.internal.core.util.text.TextSequence;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.dltk.core.CompletionRequestor;
import org.eclipse.dltk.internal.core.SourceRange;

/**
 * This code is taken from https://sites.google.com/site/50percentplan/eclipse
 */
@SuppressWarnings("restriction")
public class CompletionStrategy extends ClassMembersStrategy
		implements ICompletionStrategy {

	public CompletionStrategy(ICompletionContext context) {
		super(context);
	}

	@Override
	public void apply(ICompletionReporter reporter) throws BadLocationException {
		CompletionContext context = (CompletionContext) getContext();
		
		// As we are implementing object factory we are interesting in
		// contexts of '->' type only.
		if (!(context instanceof ClassMemberContext)) {
			return;
		}
		
		// Initialize data required for editor using code assistance
		CompletionContext concreteContext = (CompletionContext) context;
		CompletionRequestor requestor = concreteContext
				.getCompletionRequestor();
		String prefix = concreteContext.getPrefix();
		String suffix = "";
		SourceRange replaceRange = getReplacementRange(concreteContext);
		AbstractCompletionContext aContext = (AbstractCompletionContext) context;
		int offset = aContext.getOffset();
		TextSequence statementText = aContext.getStatementText();
		int triggerEnd = getTriggerEnd(statementText);

		// Call our own call chain resolver, asking to deduce types for code
		// fragment we are editing currently
		IType[] types = CompletionContextParser.getTypesFor(aContext.getSourceModule(),
				statementText, triggerEnd, offset);
		
		// Actually add content to completion proposals list
		boolean exactName = requestor.isContextInformationMode();
		for (IType type : types) {
			try {
				IField[] oneTypeProperties = PHPModelUtils
						.getTypeHierarchyField(type, prefix, exactName, null);

				for (IField property : oneTypeProperties) {
					if (!isFiltered(property, type, concreteContext)) {
						reporter.reportField(property, suffix, replaceRange,
								true);
					}
				}

			} catch (CoreException e) {
				e.printStackTrace();
			}
		}

		List<IMethod> methods = getMethodsFromTypes(types, prefix, exactName,
				concreteContext);

		for (IMethod method : methods) {
			reporter.reportMethod(method, suffix, replaceRange);
		}
	}
	
	/**
	 * Returns methods extracted from supplied types.
	 *
	 * This is actually a copy of PDT's own private method for methods
	 * extraction.
	 *
	 * @param types
	 * @param prefix
	 * @param exactName
	 * @param concreteContext
	 * @return List of methods
	 */
	private List<IMethod> getMethodsFromTypes(IType[] types, String prefix,
			boolean exactName, CompletionContext concreteContext) {

		List<IMethod> result = new LinkedList<IMethod>();

		for (IType type : types) {

			try {
				ITypeHierarchy hierarchy = getCompanion()
						.getSuperTypeHierarchy(type, null);

				IMethod[] methods = PHPModelUtils.getTypeHierarchyMethod(type,
						hierarchy, prefix, exactName, null);

				for (IMethod method : removeOverriddenElements(Arrays
						.asList(methods))) {

					if (!isFiltered(method, type, concreteContext)) {
						result.add(method);
					}
				}

			} catch (CoreException e) {
				System.out.print(e.toString());
			}

		}

		return result;
	}
	
	private int getTriggerEnd(TextSequence statementText) {

		int triggerEnd = PHPTextSequenceUtilities.readBackwardSpaces(
				statementText, statementText.length());

		triggerEnd = PHPTextSequenceUtilities.readIdentifierStartIndex(
				statementText, triggerEnd, true);

		triggerEnd = PHPTextSequenceUtilities.readBackwardSpaces(statementText,
				triggerEnd);

		return triggerEnd;
	}
}
