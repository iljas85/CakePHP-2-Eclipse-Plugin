package com.github.iljas85.CakePHP2EclipsePlugin.ControllerField;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.php.core.codeassist.ICompletionContext;
import org.eclipse.php.core.codeassist.ICompletionStrategy;
import org.eclipse.php.core.codeassist.ICompletionStrategyFactory;

public class CompletionStrategyFactory implements
		ICompletionStrategyFactory {

	@Override
	public ICompletionStrategy[] create(ICompletionContext[] contexts) {
		List<ICompletionStrategy> result = new LinkedList<ICompletionStrategy>();
		for (ICompletionContext context : contexts) {
			if (context.getClass() == CompletionContext.class) {
				result.add(new CompletionStrategy(context));
			}
		}
		return (ICompletionStrategy[]) result
				.toArray(new ICompletionStrategy[result.size()]);
	}

}
