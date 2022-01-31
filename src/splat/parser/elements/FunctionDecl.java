package splat.parser.elements;

import splat.lexer.Token;

import java.util.List;
import java.util.ArrayList;

//<func-decl> ::= <label> ( <params> ) : <ret-type> is <loc-var-decls> begin <stmts> end ;
public class FunctionDecl extends Declaration {

	//All elements
	Label label;
	List<Param> params;
	RetType retType;
	List <VariableDecl> localVarDecls = new ArrayList <VariableDecl>();
	List <Statement> stmts = new ArrayList<Statement>();
	
	// Constructor
	public FunctionDecl(Token tok, Label label, List<Param> params, RetType retType,
						List <VariableDecl> localVarDecls, List <Statement> stmts) {
		super(tok);
		this.label = label;
		this.params = params;
		this.retType = retType;
		this.localVarDecls = localVarDecls;
		this.stmts = stmts;
	}

	// Getters?
	
	// Fix this as well
	public String toString() {
		String fDecl = label.getValue() + "(";
		for (int i = 0; i < params.size(); i++){
			if (i == 0)
				fDecl = fDecl + params.get(i);
			else
				fDecl = fDecl + "," + params.get(i);
		}
		fDecl = fDecl + ") :"  + retType + " is\n";

		for (VariableDecl varDecl : localVarDecls){
			fDecl = fDecl + varDecl + "\n";
		}
		fDecl = fDecl + "begin\n";

		for (Statement st : stmts){
			fDecl = fDecl + st + "\n";
		}
		fDecl = fDecl + "end ;";

		return fDecl;
	}

	@Override
	public Label getLabel() {
		return label;
	}

	public List <VariableDecl> getLocalVarDecls(){
		return localVarDecls;
	}

	public List<Param> getParams() {
		return params;
	}

	public List<Statement> getStmts() {
		return stmts;
	}

	public RetType getRetType() {
		return retType;
	}
}
