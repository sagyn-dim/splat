package splat.parser.elements;

import splat.executor.ExecutionException;
import splat.executor.ReturnFromCall;
import splat.executor.Value;
import splat.executor.ValueBool;
import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;

import java.util.List;
import java.util.Map;

public class StatementIf extends Statement{
    Expression expr;
    List <Statement> stmts;

    public StatementIf (Token tok, Expression expr, List<Statement> stmts){
        super(tok);
        this.expr = expr;
        this.stmts = stmts;
    }

    @Override
    public String toString() {
        String stIf = "if " + expr + " then\n";
        for (Statement st : stmts){
            stIf = stIf + st + "\n";
        }
        stIf = stIf + "end if ;";

        return stIf;
    }

    @Override
    public void analyze(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap)
            throws SemanticAnalysisException {

        //First make sure that expression is boolean
        Type exprType = this.expr.analyzeAndGetType(funcMap, varAndParamMap);
        if (!exprType.getValue().equals("Boolean"))
            throw new SemanticAnalysisException("Expression after if must be boolean", getLine(), getColumn());

        //Analyze all statements
        for (Statement stmt : stmts){
            stmt.analyze(funcMap, varAndParamMap);
        }
    }

    @Override
    public void execute(Map<String, FunctionDecl> funcMap, Map<String, Value> varAndParamMap)
            throws ReturnFromCall, ExecutionException {
        //Evaluate the expression first
        boolean toExecute = ((ValueBool)expr.evaluate(funcMap, varAndParamMap)).getValue();

        //Execute if true
        if (toExecute){
            for (Statement stmt : stmts){
                stmt.execute(funcMap, varAndParamMap);
            }
        }
    }

    public List<Statement> getStmts() {
        return stmts;
    }
}
