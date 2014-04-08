package com.github.iljas85.CakePHP2EclipsePlugin.view;

import java.util.Set;

import org.eclipse.dltk.evaluation.types.MultiTypeType;
import org.eclipse.dltk.evaluation.types.UnknownType;
import org.eclipse.dltk.ti.GoalState;
import org.eclipse.dltk.ti.goals.GoalEvaluator;
import org.eclipse.dltk.ti.goals.IGoal;
import org.eclipse.php.internal.core.typeinference.PHPClassType;

@SuppressWarnings("restriction")
public class VVariableGoalEvaluator extends GoalEvaluator {

	private Set<String> types;
	
	public VVariableGoalEvaluator(IGoal goal, Set<String> types) {
		super(goal);

		this.types = types;
	}

	@Override
	public IGoal[] init() {
		return IGoal.NO_GOALS;
	}

	@Override
	public Object produceResult() {
		if (types.contains("")) {
			types.remove("");
		}
		
		if (types.size() == 0) {
			return UnknownType.INSTANCE;
		} else if (types.size() == 1) {
			String result = "";
			for (String type: types) {
				result = type;
			}
			return new PHPClassType(result);
		} else {
			MultiTypeType result = new MultiTypeType();
			for (String type: types) {
				result.addType(new PHPClassType(type));
			}
			return result;
		}
	}

	@Override
	public IGoal[] subGoalDone(IGoal subgoal, Object result, GoalState state) {
		return IGoal.NO_GOALS;
	}

}
