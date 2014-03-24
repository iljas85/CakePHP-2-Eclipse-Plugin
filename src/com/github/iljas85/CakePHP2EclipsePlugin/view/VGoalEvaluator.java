package com.github.iljas85.CakePHP2EclipsePlugin.view;

import org.eclipse.dltk.ti.GoalState;
import org.eclipse.dltk.ti.goals.GoalEvaluator;
import org.eclipse.dltk.ti.goals.IGoal;
import org.eclipse.php.internal.core.typeinference.PHPClassType;

@SuppressWarnings("restriction")
public class VGoalEvaluator extends GoalEvaluator {

	private String viewType;
	
	public VGoalEvaluator(IGoal goal, String fieldType) {
		super(goal);
		this.viewType = fieldType;
	}
	
	@Override
	public IGoal[] init() {
		return null;
	}

	@Override
	public IGoal[] subGoalDone(IGoal subgoal, Object result, GoalState state) {
		return null;
	}

	@Override
	public Object produceResult() {
		return new PHPClassType(viewType);
	}

}
