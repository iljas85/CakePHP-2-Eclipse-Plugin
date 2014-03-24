package com.github.iljas85.CakePHP2EclipsePlugin.ControllerField;

import java.util.HashMap;

import org.eclipse.dltk.ti.IContext;
import org.eclipse.dltk.ti.IGoalEvaluatorFactory;
import org.eclipse.dltk.ti.goals.GoalEvaluator;
import org.eclipse.dltk.ti.goals.IGoal;
import org.eclipse.dltk.ti.types.IEvaluatedType;
import org.eclipse.php.internal.core.typeinference.context.TypeContext;
import org.eclipse.php.internal.core.typeinference.goals.ClassVariableDeclarationGoal;

import com.github.iljas85.CakePHP2EclipsePlugin.index.CakePHP2Indexer;

@SuppressWarnings("restriction")
public class GoalEvaluatorFactory implements IGoalEvaluatorFactory {
	
	// The only method of the interface
	public GoalEvaluator createEvaluator(IGoal goal) {
		Class<?> goalClass = goal.getClass();
		
		// Override only 'ClassVariableDeclarationGoal' type goals
		if (ClassVariableDeclarationGoal.class != goalClass) {
			return null;
		}
		
		GoalEvaluator result = null;
		
		ClassVariableDeclarationGoal classVariable = (ClassVariableDeclarationGoal) goal;
		
		result = produceGoalEvaluator(classVariable);
		
		return result;
	}
	
	private GoalEvaluator produceGoalEvaluator(ClassVariableDeclarationGoal classVariable) {
		String variableName = classVariable.getVariableName();
		if (variableName.startsWith("$")) {
			variableName = variableName.substring(1);
		}
		IContext context = classVariable.getContext();
		if (context instanceof TypeContext)
		{
			TypeContext type = (TypeContext) context;
			IEvaluatedType evaType = type.getInstanceType();
			String typeName = evaType.getTypeName();
			if (typeName.endsWith("Controller")) {
				try {
					CakePHP2Indexer indexer = CakePHP2Indexer.getInstance();
					HashMap<String, String> fields = indexer.getFieldsForController(typeName);
					if (fields.containsKey(variableName))
					{
						return new CFGoalEvaluator(classVariable, fields.get(variableName));
					}
					fields = indexer.getFieldsForController("AppController");
					if (fields.containsKey(variableName))
					{
						return new CFGoalEvaluator(classVariable, fields.get(variableName));
					}
				} catch (Exception e) {
					//Log.log()
				}
			}
		}
		
		return null;
	}
}
