package splat.parser;

import java.util.ArrayList;
import java.util.List;

import splat.lexer.Token;
import splat.parser.elements.*;

import javax.swing.plaf.nimbus.State;

public class Parser {

	private List<Token> tokens;
	
	public Parser(List<Token> tokens) {
		this.tokens = tokens;
	}

	/**
	 * Compares the next token to an expected value, and throws
	 * an exception if they don't match.  This removes the front-most
	 * (next) token  
	 * 
	 * @param expected value of the next token
	 * @throws ParseException if the actual token doesn't match what 
	 * 			was expected
	 */
	private void checkNext(String expected) throws ParseException {

		Token tok = tokens.remove(0);
		
		if (!tok.getValue().equals(expected)) {
			throw new ParseException("Expected '"+ expected + "', got '" 
					+ tok.getValue()+ "'.", tok);
		}
	}
	
	/**
	 * Returns a boolean indicating whether or not the next token matches
	 * the expected String value.  This does not remove the token from the
	 * token list.
	 * 
	 * @param expected value of the next token
	 * @return true iff the token value matches the expected string
	 */
	private boolean peekNext(String expected) {
		return tokens.get(0).getValue().equals(expected);
	}
	
	/**
	 * Returns a boolean indicating whether or not the token directly after
	 * the front most token matches the expected String value.  This does 
	 * not remove any tokens from the token list.
	 * 
	 * @param expected value of the token directly after the next token
	 * @return true iff the value matches the expected string
	 */
	private boolean peekTwoAhead(String expected) {
		return tokens.get(1).getValue().equals(expected);
	}
	
	
	/*
	 *  <program> ::= program <decls> begin <stmts> end ;
	 */
	public ProgramAST parse() throws ParseException {
		
		try {
			// Needed for 'program' token position info
			Token startTok = tokens.get(0);
			
			checkNext("program");
			
			List<Declaration> decls = parseDecls();
			
			checkNext("begin");
			
			List<Statement> stmts = parseStmts();
			
			checkNext("end");
			checkNext(";");
	
			return new ProgramAST(decls, stmts, startTok);
			
		// This might happen if we do a tokens.get(), and nothing is there!
		} catch (IndexOutOfBoundsException ex) {
			
			throw new ParseException("Unexpectedly reached the end of file.", -1, -1);
		}
	}
	
	/*
	 *  <decls> ::= (  <decl>  )*
	 */
	private List<Declaration> parseDecls() throws ParseException {
		
		List<Declaration> decls = new ArrayList<Declaration>();
		
		while (!peekNext("begin")) {
			Declaration decl = parseDecl();
			decls.add(decl);
		}
		
		return decls;
	}
	
	/*
	 * <decl> ::= <var-decl> | <func-decl>
	 */
	private Declaration parseDecl() throws ParseException {

		if (peekTwoAhead(":")) {
			return parseVarDecl();
		} else if (peekTwoAhead("(")) {
			return parseFuncDecl();
		} else {
			Token tok = tokens.get(0);
			throw new ParseException("Declaration expected", tok);
		}
	}
	
	/*
	 * <func-decl> ::= <label> ( <params> ) : <ret-type> is 
	 * 						<loc-var-decls> begin <stmts> end ;
	 */
	private FunctionDecl parseFuncDecl() throws ParseException {
		//Parse label
		Token startTok = tokens.remove(0);
		Label label = new Label(startTok);

		//Get rid of (
		tokens.remove(0);

		//Parse params
		List<Param> params = new ArrayList<Param>();
		params = parseParams();

		//Next : is expected
		checkNext(":");

		//Parse ret type
		RetType retType = new RetType(tokens.remove(0));

		//Next "is" expected
		checkNext("is");

		//Parse local variables
		List<VariableDecl> locVarDecls = new ArrayList<VariableDecl>();
		locVarDecls = parseLocVarDecls();

		//Next begin is expected
		checkNext("begin");

		//Parse stmts
		List<Statement> stmts = new ArrayList<Statement>();
		stmts = parseStmts();

		//"End" and ; are expected
		checkNext("end");
		checkNext(";");

		//Finally create FunclDecl object
		FunctionDecl funcDecl = new FunctionDecl(startTok, label, params, retType, locVarDecls, stmts);

		return funcDecl;
	}

	/*
	 * <params> ::= <param> ( , <param> )*
	 *	| ɛ
	 */
	private List <Param> parseParams() throws ParseException{

		List <Param> params = new ArrayList<>();

		//First param
		if (!peekNext(")"))
			params.add(parseParam());

		//All the other params
		while (!peekNext(")")) {
			checkNext(",");
			params.add(parseParam());
		}

		//Get rid of )
		tokens.remove(0);

		return params;
	}

	/*
	 * <param> ::= <label> : <type>
	 */
	private Param parseParam() throws ParseException{

		Param param;

		if(peekTwoAhead(":")) {
			//Parse token
			Token startTok = tokens.remove(0);
			Label label = new Label(startTok);

			//Next : is expected but no need to check
			tokens.remove(0);

			//Parse type
			Type type = new Type(tokens.remove(0));

			//The validity of the elements will be checked the subclasses
			param = new Param(startTok, label, type);
		}
		else
			throw new ParseException("Parameter declaration was expected", tokens.get(0).getLine(),
					tokens.get(0).getColumn());

		return param;
	}

	/*
	 * <var-decl> ::= <label> : <type> ;
	 */
	private VariableDecl parseVarDecl() throws ParseException {
		//Parse token
		Token startTok = tokens.remove(0);
		Label label = new Label(startTok);

		//Next : is expected but no need to check
		tokens.remove(0);

		//Parse type
		Type type = new Type(tokens.remove(0));

		//Variable decl has to end with ;
		checkNext(";");

		//The validity of the elements will be checked the subclasses
		VariableDecl varDec = new VariableDecl(startTok, label, type);

		return varDec;
	}

	/*
	 * <loc-var-decls> ::= ( <var-decl> )*
	 */
	private List <VariableDecl> parseLocVarDecls() throws ParseException{

		List <VariableDecl> locVarDecls = new ArrayList<VariableDecl>();

		while (!peekNext("begin")){
			locVarDecls.add(parseVarDecl());
		}

		return locVarDecls;
	}

	/*
	 * <stmts> ::= (  <stmt>  )*
	 */
	private List<Statement> parseStmts() throws ParseException {
		List <Statement> stmts = new ArrayList <Statement>();
		Statement stmt;
		while (!(peekNext("end") && peekTwoAhead(";"))) {
			// First we identify what kind of statement
			if (peekNext("while")) {
				stmt = parseWhile();
			}
			else if (peekNext("if")) {
				stmt = parseIf();
			}
			else if (peekTwoAhead(":=")){
				stmt = parseAssignment();
			}
			else if (peekNext("print")) {
				stmt = parsePrint();
			}
			else if (peekNext("print_line")) {
				stmt = parsePrintLine();
			}
			else if (peekNext("return")) {
				stmt = parseReturn();
			}
			else if (peekTwoAhead("(")) {
				stmt = parseFunctionCall();
			}
			else
				throw new ParseException("Exception! Expected a statement. ", tokens.get(0).getLine(),
						tokens.get(0).getColumn());

			stmts.add(stmt);
		};


		return stmts;
	}

	private Statement parseStmt() throws ParseException {
		Statement stmt;
		// First we identify what kind of statement
		if (peekNext("while")) {
			stmt = parseWhile();
		}
		else if (peekNext("if")) {
			stmt = parseIf();
		}
		else if (peekTwoAhead(":=")){
			stmt = parseAssignment();
		}
		else if (peekNext("print")) {
			stmt = parsePrint();
		}
		else if (peekNext("print_line")) {
			stmt = parsePrintLine();
		}
		else if (peekNext("return")) {
			stmt = parseReturn();
		}
		else if (peekTwoAhead("(")) {
			stmt = parseFunctionCall();
		}
		else
			throw new ParseException("Exception! Expected a statement. ", tokens.get(0).getLine(),
					tokens.get(0).getColumn());

		return stmt;
	}

	//while <expr> do <stmts> end while ;
	private StatementWhile parseWhile() throws ParseException{

		Token startTok = tokens.remove(0);

		//Get <expr>
		Expression expr = parseExpression();

		//check do
		checkNext("do");

		//Stmts
		List <Statement> stmts = new ArrayList<>();
		while (!peekNext("end")){
			stmts.add(parseStmt());
		}

		//end while
		checkNext("end");
		checkNext("while");
		checkNext(";");

		StatementWhile stmtWhile = new StatementWhile(startTok, expr, stmts);

		return stmtWhile;
	}
	/*if <expr> then <stmts> else <stmts> end if ;
	 *or
	 *if <expr> then <stmts> end if ;
	 */
	private StatementIf parseIf() throws ParseException{

		Token startTok = tokens.remove(0);

		//get <expr>
		Expression expr = parseExpression();

		//then
		checkNext("then");

		//stmts
		List<Statement> stmts = new ArrayList<>();

		while (!peekNext("end") && !peekNext("else") ) {
			stmts.add(parseStmt());
		}

		if (tokens.remove(0).getValue().equals("end")){
			tokens.remove(0); 	//Get rid of if
			checkNext(";");		//must finish with ;
			return new StatementIf(startTok, expr, stmts);
		}
		else{
			List <Statement> stmtsElse = new ArrayList<>();
			while (!peekNext("end"))
				stmtsElse.add(parseStmt());
			tokens.remove(0); //Get rid of end
			checkNext("if");
			checkNext(";"); //Must end with end if;
			return new StatementIfElse(startTok, expr, stmts, stmtsElse);
		}
	}

	/*
	<label> := <expr> ;
	*/
	private StatementAssignment parseAssignment() throws ParseException{
		Token startTok = tokens.get(0);

		//Label
		Label label = new Label(tokens.remove(0));

		//:=
		tokens.remove(0);

		//<expr>
		Expression expr = parseExpression();

		checkNext(";");

		return new StatementAssignment(startTok, label, expr);
	}

	/*<label> ( <args> ) ;
	*
	*/
	private StatementFCall parseFunctionCall() throws ParseException{
		Token sTok = tokens.get(0);

		Label label = new Label(tokens.remove(0));

		tokens.remove(0); //Pop (

		List <Expression> args = parseArgs();

		checkNext(";");

		return new StatementFCall(sTok, label, args);
	}

	/*
	* <args> ::= <expr> ( , <expr> )*
	| ɛ
	*/
	private List <Expression> parseArgs() throws ParseException{
		List <Expression> exprs = new ArrayList<Expression>();

		Token sTok = tokens.get(0);

		//First param
		if (!peekNext(")"))
			exprs.add(parseExpression());

		//All the other params
		while (!peekNext(")")) {
			checkNext(",");
			exprs.add(parseExpression());
		}
		checkNext(")");

		return exprs;
	}

	/*
	* print <expr> ;
	 */
	private StatementPrint parsePrint() throws ParseException{
		Token sTok = tokens.remove(0); //first token is print

		Expression expr = parseExpression();

		checkNext(";");

		return new StatementPrint(sTok, expr);
	}

	/*
	print_line;
	 */
	private StatementPLine parsePrintLine() throws ParseException{
		Token sTok = tokens.remove(0);

		checkNext(";");

		return new StatementPLine(sTok);
	}

	/*
	return;
	 */
	private StatementReturn parseReturn() throws ParseException{
		Token sTok = tokens.remove(0);

		if (peekNext(";")) {
			tokens.remove(0);
			return new StatementReturn(sTok);
		}
		else {
			Expression expr = parseExpression();
			checkNext(";");
			return new StatementReturnExpr(sTok, expr);
		}
	}

	/*
	<expr> ::= ( <expr> <bin-op> <expr> )
			| ( <unary-op> <expr> )
			| <label> ( <args> )
			| <label>
			| <literal>
	 */
	private String[] unOps = {"not", "-"};
	private String[] binOps = {"and", "or", "<", ">", "==", ">=", "<=", "+", "-", "*", "/", "%"};
	private String numeric = "0123456789";

	private boolean contains (String[] arr, String item) {
		boolean contains = false;
		for (String str : arr) {
			if (str.equals(item)){
				contains = true;
				break;
			}
		}
		return contains;
	}

	private boolean checkForInt (Token tok){
		String val = tok.getValue();
		boolean isInt = true;

		for (int i = 0; i < val.length(); i++){
			if (!numeric.contains(String.valueOf(val.charAt(i)))){
				isInt = false;
				break;
			}
		}
		return isInt;
	}

	private boolean checkForBool(Token tok){
		String val = tok.getValue();
		boolean isBool = false;

		if (val.equals("true") || val.equals("false")){
			isBool = true;
		}
		return isBool;
	}

	private boolean checkForString(Token tok){
		String val = tok.getValue();
		boolean isString = false;

		if (val.charAt(0) == '\"')
			isString = true;

		return isString;
	}

	private Expression parseExpression() throws ParseException{
		Token sTok = tokens.get(0);

		if (peekNext("(")){
			tokens.remove(0); //pop (
			if (contains(unOps, tokens.get(0).getValue())){ //( <unary-op> <expr> )
				Token unOp = tokens.remove(0);
				Expression expr = parseExpression();
				checkNext(")");
				return new ExpressionUnaryOp(sTok, unOp, expr);
			}
			else{
				Expression expr1 = parseExpression();
				if (contains(binOps, tokens.get(0).getValue())){ //( <expr> <bin-op> <expr> )
					Token binOp = tokens.remove(0);
					Expression expr2 = parseExpression();
					checkNext(")");
					return new ExpressionBinOp(sTok, expr1, binOp, expr2);
				}
				else
					throw new ParseException("Parse Exception, Binary Operator Expected!", tokens.get(0).getLine(),
							tokens.get(0).getColumn());
			}
		}
		//<label> ( <args> )
		else if (peekTwoAhead("(")){
			Label label = new Label(tokens.remove(0));

			tokens.remove(0); //pop (

			List <Expression> args = parseArgs();

			return new ExpressionFCall(sTok, label, args);
		}
		//bool literal
		else if (checkForBool(tokens.get(0))){
			return new ExpressionBoolLiteral(tokens.remove(0));
		}
		//int literal
		else if (checkForInt(tokens.get(0))){
			return new ExpressionIntLiteral(tokens.remove(0));
		}
		//string literal
		else if (checkForString(tokens.get(0))){
			return new ExpressionStringLiteral(tokens.remove(0));
		}
		else return new Label(tokens.remove(0));
	}
}

