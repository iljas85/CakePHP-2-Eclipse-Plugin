package com.github.iljas85.CakePHP2EclipsePlugin.ControllerField;

import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.php.internal.core.codeassist.CodeAssistUtils;
import org.eclipse.php.internal.core.util.text.PHPTextSequenceUtilities;
import org.eclipse.php.internal.core.util.text.TextSequence;
import org.eclipse.dltk.ti.types.IEvaluatedType;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ti.IContext;
import org.eclipse.php.internal.core.typeinference.PHPTypeInferenceUtils;
import org.eclipse.php.internal.core.compiler.ast.parser.ASTUtils;
import org.eclipse.dltk.core.SourceParserUtil;
import org.eclipse.dltk.ti.ISourceModuleContext;

/**
 * This code is taken from https://sites.google.com/site/50percentplan/eclipse
 */
@SuppressWarnings("restriction")
public class CompletionContextParser extends CodeAssistUtils {

	private static final IType[] EMPTY_TYPES_2 = new IType[0];
	
	public static IType[] getTypesFor(ISourceModule sourceModule,
			TextSequence statementText, int endPosition, int offset) {
		
		String className = "";
		try {
			IType[] tps = sourceModule.getTypes();
			if (tps.length > 0) {
				className = tps[0].getFullyQualifiedName();
			}
		} catch (ModelException e) {
			//Logger.log(e.getMessage());
		}
		
		String triggerText = statementText.subSequence(endPosition - 2,
				endPosition).toString();

		// If statement does not end with -> or :: then do nothing.
		if (!triggerText.equals(OBJECT_FUNCTIONS_TRIGGER)
				&& !triggerText.equals("::")) {
			return EMPTY_TYPES_2;
		}
		
		Resolver factorySearcher = new Resolver(
				statementText, className);

		if (!factorySearcher.containsFactoryCall()) {
			return EMPTY_TYPES_2;
		}

		// Broker has been found at offset brokerSearcher.getOffset()
		// while current parsing offset is endPosition

		int propertyEndPosition = PHPTextSequenceUtilities.readBackwardSpaces(
				statementText, endPosition - triggerText.length());

		if (factorySearcher.getOffset() >= propertyEndPosition) {
			// Broker considered found at offset brokerSearcher.getOffset()
			return getFactoryType(factorySearcher, sourceModule, offset);
		}
		
		// Broker reported at offset less then current - continue to build chain

		// Recursion to find the next member of the chain.
		int lastObjectOperator = PHPTextSequenceUtilities
				.getPrivousTriggerIndex(statementText, propertyEndPosition);
		int propertyStartPosition = PHPTextSequenceUtilities.readForwardSpaces(
				statementText, lastObjectOperator + triggerText.length());

		// Resulting types of the statement on the left side
		// from the current position - recursion tails now.
		IType[] types = getTypesFor(sourceModule, statementText,
				propertyStartPosition, offset);

		// property name is a part enclosed between next and previous triggers
		String propertyName = statementText.subSequence(propertyStartPosition,
				propertyEndPosition).toString();

		// It is safe now to give control to default implementation.
		// Unfortunately parent does not have an appropriate methods to call.
		// So copy default implementation.
		return getPropertyType(propertyName, types, sourceModule, offset);
	}

	private static IType[] getPropertyType(String propertyName, IType[] types,
			ISourceModule sourceModule, int offset) {
		int bracketIndex = propertyName.indexOf('(');

		if (bracketIndex == -1) {
			// meaning its a class variable and not a function
			return getVariableType(types, propertyName, offset);
		}

		String functionName = propertyName.substring(0, bracketIndex).trim();

		IType[] returnTypes = getFunctionReturnType(types, functionName,
				sourceModule, offset);

		return returnTypes != null ? returnTypes : EMPTY_TYPES_2;

	}
	
	private static IType[] getFactoryType(
			Resolver brokerSearcher,
			ISourceModule sourceModule, int offset) {

		IEvaluatedType evaluatedType = brokerSearcher.getClassType();

		ModuleDeclaration moduleDeclaration = SourceParserUtil
				.getModuleDeclaration(sourceModule, null);

		IContext context = ASTUtils.findContext(sourceModule,
				moduleDeclaration, offset);

		return PHPTypeInferenceUtils.getModelElements(evaluatedType,
				(ISourceModuleContext) context, offset);
	}
}
