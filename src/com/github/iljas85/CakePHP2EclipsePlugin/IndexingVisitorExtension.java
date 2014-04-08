package com.github.iljas85.CakePHP2EclipsePlugin;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.declarations.TypeDeclaration;
import org.eclipse.dltk.ast.expressions.CallArgumentsList;
import org.eclipse.dltk.ast.expressions.Expression;
import org.eclipse.dltk.ast.references.VariableReference;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.SourceParserUtil;
import org.eclipse.dltk.ti.IContext;
import org.eclipse.dltk.ti.ISourceModuleContext;
import org.eclipse.dltk.ti.goals.ExpressionTypeGoal;
import org.eclipse.dltk.ti.types.IEvaluatedType;
import org.eclipse.php.core.index.PhpIndexingVisitorExtension;
import org.eclipse.php.internal.core.compiler.ast.nodes.ClassDeclaration;
import org.eclipse.php.internal.core.compiler.ast.nodes.ExpressionStatement;
import org.eclipse.php.internal.core.compiler.ast.nodes.PHPCallExpression;
import org.eclipse.php.internal.core.compiler.ast.nodes.PHPMethodDeclaration;
import org.eclipse.php.internal.core.compiler.ast.nodes.Scalar;
import org.eclipse.php.internal.core.compiler.ast.parser.ASTUtils;
import org.eclipse.php.internal.core.typeinference.PHPTypeInferenceUtils;
import org.eclipse.php.internal.core.typeinference.PHPTypeInferencer;

import com.github.iljas85.CakePHP2EclipsePlugin.index.CakePHP2Indexer;

@SuppressWarnings("restriction")
public class IndexingVisitorExtension extends PhpIndexingVisitorExtension {
	private ClassDeclaration currentClass;
	private String fileName = "";
	private boolean isController;
	private String currentMethod = "";
	
	public boolean visit(TypeDeclaration s) throws Exception {
		if (s instanceof ClassDeclaration) {
			currentClass = (ClassDeclaration) s;
			IResource resource = sourceModule.getCorrespondingResource();
			if (resource instanceof IFile) {
				fileName = ((IFile) resource).getFullPath().toPortableString();
			}
			isController = currentClass.getName().endsWith("Controller");
			if (isController) {
				cleanTemplateVariables();
			}
		}
		return true;
	}
	
	/**
	 * Removes exported variables of controller methods
	 * collected before
	 * @throws Exception
	 */
	private void cleanTemplateVariables() throws Exception {
		CakePHP2Indexer indexer = CakePHP2Indexer.getInstance();
		indexer.removeVariables(fileName, currentClass.getName());
	}
	
	public boolean endvisit(TypeDeclaration s) throws Exception {
		currentClass = null;
		fileName = "";
		
		return true;
	}
	
	public boolean visit(PHPMethodDeclaration s) throws Exception {
		currentMethod = s.getName();
		
		return true;
	}
	
	public boolean visit(ExpressionStatement st) throws Exception {
		Expression expr = st.getExpr();
		if (expr instanceof PHPCallExpression) {
			collectVariables((PHPCallExpression) expr);
		}
		
		return true;
	}
	
	/**
	 * Looks for $this->set('var', $var);
	 * @param s
	 */
	private void collectVariables(PHPCallExpression s) throws Exception {
		String name = s.getName();
		ASTNode r = s.getReceiver();
		if (r instanceof VariableReference) {
			VariableReference receiver = (VariableReference) r;
			if (isController 
					&& !currentMethod.isEmpty()
					&& receiver.getName().equals("$this") 
					&& name.equals("set")) {
				String varName = "";
				CallArgumentsList list = s.getArgs();
				List args = list.getChilds();
				if (args.size() == 2) {
					Object arg1 = args.get(0);
					if (arg1 instanceof Scalar) {
						varName = ASTUtils.stripQuotes(((Scalar) arg1).getValue());
					}
					Object arg2 = args.get(1);
					if (arg2 instanceof Expression) {
						ArrayList<String> types = new ArrayList<String>();
						types.add("");
						
						collectTypes((Expression) arg2, types);
						
						CakePHP2Indexer indexer = CakePHP2Indexer.getInstance();
						for (String type: types) {
							indexer.addVariable(fileName, currentClass.getName(), currentMethod,
									varName, type);
						}
					}
				}
			}
		}
	}

	private void collectTypes(Expression expr, ArrayList<String> types) {
		ModuleDeclaration moduleDeclaration = SourceParserUtil
				.getModuleDeclaration(sourceModule, null);
		IContext context = ASTUtils.findContext(sourceModule,
				moduleDeclaration, expr.sourceStart());
		if (context != null) {
			ExpressionTypeGoal goal = new ExpressionTypeGoal(context,
					expr);
			PHPTypeInferencer typeInferencer = new PHPTypeInferencer();
			IEvaluatedType evaluatedType = typeInferencer.evaluateType(goal);
		
			IType[] modelElements = PHPTypeInferenceUtils.getModelElements(
					 evaluatedType, (ISourceModuleContext) context, expr.sourceStart());
			
			for (IType type: modelElements) {
				types.add(type.getFullyQualifiedName());
			}
		}
	}

	public boolean endvisit(PHPMethodDeclaration node) {
		currentMethod = "";
		
		return true;
	}
	
	
	public boolean visitGeneral(ASTNode node) throws Exception {
		if (node instanceof PHPMethodDeclaration) {
			return visit((PHPMethodDeclaration)node);
		}
		if (node instanceof ExpressionStatement) {
			return visit((ExpressionStatement)node);
		}
		
		return super.visitGeneral(node);
	}
	
	public boolean endvisit(ASTNode node) throws Exception {
		if (node instanceof PHPMethodDeclaration) {
			return endvisit((PHPMethodDeclaration)node);
		}
		
		return super.endvisit(node);
	}
}
