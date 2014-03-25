package com.github.iljas85.CakePHP2EclipsePlugin.view;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.IField;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.php.core.codeassist.ICompletionContext;
import org.eclipse.php.core.codeassist.ICompletionStrategy;
import org.eclipse.php.internal.core.codeassist.ICompletionReporter;
import org.eclipse.php.internal.core.codeassist.contexts.AbstractCompletionContext;
import org.eclipse.php.internal.core.codeassist.strategies.GlobalVariablesStrategy;
import org.eclipse.php.internal.core.typeinference.FakeField;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.dltk.core.CompletionRequestor;
import org.eclipse.dltk.core.index2.search.ISearchEngine.MatchRule;
import org.eclipse.dltk.internal.core.ModelElement;
import org.eclipse.dltk.internal.core.SourceRange;

import com.github.iljas85.CakePHP2EclipsePlugin.PathUtils;
import com.github.iljas85.CakePHP2EclipsePlugin.index.CakePHP2Indexer;

/**
 * code complete for the variables exported from controller methods by $this->set('var', $var); statement
 */
@SuppressWarnings("restriction")
public class CompletionStrategy extends GlobalVariablesStrategy
		implements ICompletionStrategy {

	public CompletionStrategy(ICompletionContext context) {
		super(context);
	}

	@Override
	public void apply(ICompletionReporter reporter) throws BadLocationException {
		
		ICompletionContext context = getContext();
		AbstractCompletionContext abstractContext = (AbstractCompletionContext) context;
		String prefix = abstractContext.getPrefix();

		if (prefix.length() > 0 && !prefix.startsWith("$")) { //$NON-NLS-1$
			return;
		}
		
		if (!isViewFile(abstractContext)) {
			return;
		}
		
		ArrayList<IField> fields = collectVariables(abstractContext, prefix);
		
		SourceRange replaceRange = getReplacementRange(context);
		for (IModelElement var : fields) {
			reporter.reportField((IField) var, "", replaceRange, false); //$NON-NLS-1$
		}
	}

	/**
	 * variables from controller method
	 * @param abstractContext
	 * @param prefix
	 * @return
	 */
	private ArrayList<IField> collectVariables(
			AbstractCompletionContext abstractContext, String prefix) {
		CompletionRequestor requestor = abstractContext
				.getCompletionRequestor();
		
		MatchRule matchRule = MatchRule.PREFIX;
		if (requestor.isContextInformationMode()) {
			matchRule = MatchRule.EXACT;
		}
		
		String controllerName = getControllerName(abstractContext);
		String methodName = getMethodName(abstractContext);
		
		CakePHP2Indexer indexer;
		List<String> variables = null;
		try {
			indexer = CakePHP2Indexer.getInstance();
			variables = indexer.getVariables(controllerName, methodName);
		} catch (Exception e) {
			//Loger.log();
		}
		
		ArrayList<IField> fields = new ArrayList<IField>();
		for (String var: variables) {
			if (isProper("$" + var, prefix, matchRule)) {
				fields.add(
					new FakeField((ModelElement) abstractContext
							.getSourceModule(), "$" + var, 0, 0)
				);
			}
		}
		return fields;
	}
	
	/**
	 * is the variable matches prefix 
	 * @param var
	 * @param prefix
	 * @param matchRule
	 * @return
	 */
	private boolean isProper(String var, String prefix, MatchRule matchRule) {
		if (matchRule == MatchRule.EXACT) {
			return var.equalsIgnoreCase(prefix);
		} else if (matchRule == MatchRule.PREFIX) {
			return var.toUpperCase().startsWith(prefix.toUpperCase());
		}
		return false;
	}

	private boolean isViewFile(AbstractCompletionContext abstractContext) {
		IPath path = abstractContext.getSourceModule().getPath();
		PathUtils utils = new PathUtils();
		
		return utils.isViewPath(path);
	}
	
	private String getControllerName(AbstractCompletionContext abstractContext) {
		IPath path = abstractContext.getSourceModule().getPath();
		PathUtils utils = new PathUtils();
		
		return utils.getControllerName(path);
	}
	
	private String getMethodName(AbstractCompletionContext abstractContext) {
		IPath path = abstractContext.getSourceModule().getPath();
		PathUtils utils = new PathUtils();
		
		return utils.getMethodName(path);
	}
}
