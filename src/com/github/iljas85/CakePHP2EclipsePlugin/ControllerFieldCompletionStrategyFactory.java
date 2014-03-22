package com.github.iljas85.CakePHP2EclipsePlugin;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.php.core.codeassist.ICompletionContext;
import org.eclipse.php.core.codeassist.ICompletionStrategy;
import org.eclipse.php.core.codeassist.ICompletionStrategyFactory;

public class ControllerFieldCompletionStrategyFactory implements
		ICompletionStrategyFactory {

	@Override
	public ICompletionStrategy[] create(ICompletionContext[] contexts) {
		List<ICompletionStrategy> result = new LinkedList<ICompletionStrategy>();
		for (ICompletionContext context : contexts) {
			if (context.getClass() == ControllerFieldCompletionContext.class) {
				result.add(new ControllerFieldCompletionStrategy(context));
			}
		}
		return (ICompletionStrategy[]) result
				.toArray(new ICompletionStrategy[result.size()]);
	}

}
