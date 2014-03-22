package com.github.iljas85.CakePHP2EclipsePlugin;

import org.eclipse.dltk.ti.GoalState;
import org.eclipse.dltk.ti.goals.GoalEvaluator;
import org.eclipse.dltk.ti.goals.IGoal;
import org.eclipse.php.internal.core.typeinference.PHPClassType;

@SuppressWarnings("restriction")
public class ControllerFieldGoalEvaluator extends GoalEvaluator {

	private String fieldType;
	
	public ControllerFieldGoalEvaluator(IGoal goal, String fieldType) {
		super(goal);
		this.fieldType = fieldType;
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
		return new PHPClassType(fieldType);
	}

}
