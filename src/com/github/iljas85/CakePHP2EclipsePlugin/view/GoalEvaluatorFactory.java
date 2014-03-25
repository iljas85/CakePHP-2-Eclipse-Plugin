package com.github.iljas85.CakePHP2EclipsePlugin.view;

import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.ti.IContext;
import org.eclipse.dltk.ti.IGoalEvaluatorFactory;
import org.eclipse.dltk.ti.goals.GoalEvaluator;
import org.eclipse.dltk.ti.goals.IGoal;
import org.eclipse.php.internal.core.typeinference.context.FileContext;
import org.eclipse.php.internal.core.typeinference.goals.GlobalVariableReferencesGoal;

import com.github.iljas85.CakePHP2EclipsePlugin.PathUtils;

/**
 * code complete when typing $this-> in the views
 */
@SuppressWarnings("restriction")
public class GoalEvaluatorFactory implements IGoalEvaluatorFactory {
	
	// The only method of the interface
	public GoalEvaluator createEvaluator(IGoal goal) {
		Class<?> goalClass = goal.getClass();
		
		// Override only 'GlobalVariableReferencesGoal' type goals
		if (GlobalVariableReferencesGoal.class != goalClass) {
			return null;
		}
		
		GoalEvaluator result = null;
		
		GlobalVariableReferencesGoal variable = (GlobalVariableReferencesGoal) goal;
		
		result = produceGoalEvaluator(variable);
		
		return result;
	}
	
	private GoalEvaluator produceGoalEvaluator(GlobalVariableReferencesGoal variable) {
		String variableName = variable.getVariableName();
		if (variableName.equals("$this")) {
			IContext context = variable.getContext();
			if (context instanceof FileContext) {
				FileContext file = (FileContext) context;
				IPath path = file.getSourceModule().getPath();
				PathUtils utils = new PathUtils();
				if (utils.isViewPath(path)) {
					return new VGoalEvaluator(variable, "View");
				}
			}
		}
		
		return null;
	}
}
