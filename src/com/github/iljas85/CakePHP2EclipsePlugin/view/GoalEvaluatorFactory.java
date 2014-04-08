package com.github.iljas85.CakePHP2EclipsePlugin.view;

import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.ti.IContext;
import org.eclipse.dltk.ti.IGoalEvaluatorFactory;
import org.eclipse.dltk.ti.goals.GoalEvaluator;
import org.eclipse.dltk.ti.goals.IGoal;
import org.eclipse.php.internal.core.typeinference.context.FileContext;
import org.eclipse.php.internal.core.typeinference.goals.GlobalVariableReferencesGoal;

import com.github.iljas85.CakePHP2EclipsePlugin.PathUtils;
import com.github.iljas85.CakePHP2EclipsePlugin.index.CakePHP2Indexer;

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
		IContext context = variable.getContext();
		
		if (!isView(context)) {
			return null;
		}
		
		String variableName = variable.getVariableName();
		if (variableName.equals("$this")) {
			return new VThisGoalEvaluator(variable, "View");
		} else {
			variableName = variableName.substring(1);
			
			String controllerName = getControllerName(context);
			String methodName = getMethodName(context);
			
			CakePHP2Indexer indexer;
			Map<String, Set<String>> variables = null;
			try {
				indexer = CakePHP2Indexer.getInstance();
				variables = indexer.getVariables(controllerName, methodName);
			} catch (Exception e) {
				// Loger.log(e);
			}
			
			if (variables != null && variables.containsKey(variableName)) {
				return new VVariableGoalEvaluator(variable, variables.get(variableName));
			}
		}
		
		return null;
	}

	private boolean isView(IContext context) {
		if (context instanceof FileContext) {
			FileContext file = (FileContext) context;
			IPath path = file.getSourceModule().getPath();
			PathUtils utils = new PathUtils();
			return utils.isViewPath(path);
		} else {
			return false;
		}
	}
	
	private String getControllerName(IContext context) {
		if (context instanceof FileContext) {
			IPath path = ((FileContext) context).getSourceModule().getPath();
			PathUtils utils = new PathUtils();
			
			return utils.getControllerName(path);
		} else {
			return "";
		}
	}
	
	private String getMethodName(IContext context) {
		if (context instanceof FileContext) {
			IPath path = ((FileContext) context).getSourceModule().getPath();
			PathUtils utils = new PathUtils();
			
			return utils.getMethodName(path);
		} else {
			return "";
		}
	}
}
