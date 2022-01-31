package splat.semanticanalyzer;

import java.util.*;

import splat.parser.elements.*;

public class SemanticAnalyzer {

	private ProgramAST progAST;
	
	private Map<String, FunctionDecl> funcMap = new HashMap<>();
	private Map<String, Type> progVarMap = new HashMap<>();
	
	public SemanticAnalyzer(ProgramAST progAST) {
		this.progAST = progAST;
	}

	public void analyze() throws SemanticAnalysisException {
		
		// Checks to make sure we don't use the same labels more than once
		// for our program functions and variables 
		checkNoDuplicateProgLabels();
		
		// This sets the maps that will be needed later when we need to
		// typecheck variable references and function calls in the 
		// program body
		setProgVarAndFuncMaps();
		
		// Perform semantic analysis on the functions
		for (FunctionDecl funcDecl : funcMap.values()) {	
			analyzeFuncDecl(funcDecl);
		}
		
		// Perform semantic analysis on the program body
		for (Statement stmt : progAST.getStmts()) {
			stmt.analyze(funcMap, progVarMap);
		}
		
	}

	private void analyzeFuncDecl(FunctionDecl funcDecl) throws SemanticAnalysisException {
		
		// Checks to make sure we don't use the same labels more than once
		// among our function parameters, local variables, and function names
		checkNoDuplicateFuncLabels(funcDecl);
		
		// Get the types of the parameters and local variables
		Map<String, Type> varAndParamMap = getVarAndParamMap(funcDecl);

		//Check for the presence of return expr stmt when function is non-void
		Statement lastStmt = funcDecl.getStmts().get(funcDecl.getStmts().size() - 1);
		if (!funcDecl.getRetType().getValue().equals("void")) {
			if (!(lastStmt instanceof StatementReturnExpr)){
				if (lastStmt instanceof StatementIfElse){
					if (!returnTerminatedIfElse((StatementIfElse) lastStmt))
						throw new SemanticAnalysisException("Return statement not found", lastStmt);
				}
				else
					throw new SemanticAnalysisException("Return statement not found", lastStmt);
			}
		}

		// Perform semantic analysis on the function body
		for (Statement stmt : funcDecl.getStmts()) {
			stmt.analyze(funcMap, varAndParamMap);
		}
	}
	
	private boolean returnTerminatedIfElse(StatementIfElse ifElseStmt) throws SemanticAnalysisException{

		boolean terminated1 = false;
		boolean terminated2 = false;

		for (Statement stmt : ifElseStmt.getStmts()){
			if (stmt instanceof StatementReturnExpr){
				terminated1 = true;
				break;
			}
		}
		if (!terminated1){
			for (Statement stmt : ifElseStmt.getStmts()){
				if (stmt instanceof StatementIfElse)
					terminated1 = terminated1 || returnTerminatedIfElse((StatementIfElse)stmt);
			}
		}

		for (Statement stmt : ifElseStmt.getStmtsElse()){
			if (stmt instanceof StatementReturnExpr){
				terminated2 = true;
				break;
			}
		}
		if (!terminated2){
			for (Statement stmt : ifElseStmt.getStmtsElse()){
				if (stmt instanceof StatementIfElse)
					terminated2 = terminated2 || returnTerminatedIfElse((StatementIfElse)stmt);
			}
		}
		if (!(terminated1 && terminated2))
			throw new SemanticAnalysisException("Return statement not found", ifElseStmt);

		return terminated1 && terminated2;
	}

	private Map<String, Type> getVarAndParamMap(FunctionDecl funcDecl) {

		Map <String, Type> varAndParamMap = new HashMap<>();

		//First add variables
		for (VariableDecl varDecl : funcDecl.getLocalVarDecls()) {

			String label = varDecl.getLabel().toString();

			varAndParamMap.put(label, varDecl.getType());

		}
		//Now parameters
		for (Param param : funcDecl.getParams()){

			String label = param.getLabel().toString();

			varAndParamMap.put(label, param.getType());
		}
		//Add return type
		Type retType = funcDecl.getRetType();
		varAndParamMap.put("0return", retType);

		return varAndParamMap;
	}

	private void checkNoDuplicateFuncLabels(FunctionDecl funcDecl) 
									throws SemanticAnalysisException {
		
		Set <String> labels = new HashSet<>();

		//First check variables
		for (Declaration decl : funcDecl.getLocalVarDecls()){
			String label = decl.getLabel().toString();

			if (labels.contains(label) || funcMap.containsKey(label)){
				throw new SemanticAnalysisException("Cannot have duplicate label '"
						+ label + "' in function ", decl);
			}
			else
				labels.add(label);
		}

		//Now parameters
		for (Param param : funcDecl.getParams()){
			String label = param.getLabel().toString();

			if (labels.contains(label) || funcMap.containsKey(label)){
				throw new SemanticAnalysisException("Cannot have duplicate label '"
						+ label + "' in function ", param);
			}
			else
				labels.add(label);
		}
	}
	
	private void checkNoDuplicateProgLabels() throws SemanticAnalysisException {

		Set<String> labels = new HashSet<String>();
		
 		for (Declaration decl : progAST.getDecls()) {
 			String label = decl.getLabel().toString();
 			
			if (labels.contains(label)) {
				throw new SemanticAnalysisException("Cannot have duplicate label '"
						+ label + "' in program", decl);
			} else {
				labels.add(label);
			}
			
		}
	}
	
	private void setProgVarAndFuncMaps() {
		
		for (Declaration decl : progAST.getDecls()) {

			String label = decl.getLabel().toString();
			
			if (decl instanceof FunctionDecl) {
				FunctionDecl funcDecl = (FunctionDecl)decl;
				funcMap.put(label, funcDecl);
				
			} else if (decl instanceof VariableDecl) {
				VariableDecl varDecl = (VariableDecl)decl;
				progVarMap.put(label, varDecl.getType());
			}
		}
	}
}
