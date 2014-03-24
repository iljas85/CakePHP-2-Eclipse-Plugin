package com.github.iljas85.CakePHP2EclipsePlugin.ControllerField;

import org.eclipse.php.core.codeassist.ICompletionContext;
import org.eclipse.php.core.codeassist.ICompletionContextResolver;
import org.eclipse.php.internal.core.codeassist.contexts.CompletionContextResolver;

@SuppressWarnings("restriction")
public class ContextResolver extends CompletionContextResolver 
	implements ICompletionContextResolver {

	public ICompletionContext[] createContexts() {
        return new ICompletionContext[] { new CompletionContext() };
    }
}
