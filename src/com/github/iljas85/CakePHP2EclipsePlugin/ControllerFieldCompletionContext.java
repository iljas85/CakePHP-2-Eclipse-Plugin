package com.github.iljas85.CakePHP2EclipsePlugin;

import org.eclipse.dltk.core.CompletionRequestor;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.php.internal.core.codeassist.contexts.ClassMemberContext;

@SuppressWarnings("restriction")
public class ControllerFieldCompletionContext extends ClassMemberContext {
	public boolean isValid(ISourceModule sourceModule, int offset,
			CompletionRequestor requestor) {
		
		// Call to super to verify that cursor is in the class member call
		// context
		if (super.isValid(sourceModule, offset, requestor)) {
 
			// This context only supports "->" trigger type (not the "::")
			if (getTriggerType() == Trigger.OBJECT) {
 
				return true;
			}
		}
 
		return false;
	}
}