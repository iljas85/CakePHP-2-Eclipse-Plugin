package com.github.iljas85.CakePHP2EclipsePlugin.view;

import org.eclipse.dltk.ti.GoalState;
import org.eclipse.dltk.ti.goals.GoalEvaluator;
import org.eclipse.dltk.ti.goals.IGoal;
import org.eclipse.php.internal.core.typeinference.PHPClassType;

@SuppressWarnings("restriction")
public class VThisGoalEvaluator extends GoalEvaluator {

	private String viewType;
	
	public VThisGoalEvaluator(IGoal goal, String viewType) {
		super(goal);
		this.viewType = viewType;
	}
	
	@Override
	public IGoal[] init() {
		return IGoal.NO_GOALS;
	}

	@Override
	public IGoal[] subGoalDone(IGoal subgoal, Object result, GoalState state) {
		return IGoal.NO_GOALS;
	}

	@Override
	public Object produceResult() {
		return new PHPClassType(viewType);
	}

}
