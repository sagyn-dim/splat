package splat.parser.elements;

import splat.executor.ExecutionException;
import splat.executor.ReturnFromCall;
import splat.executor.Value;
import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;

import javax.naming.ldap.ExtendedRequest;
import java.util.Map;

public class StatementReturnExpr extends StatementReturn{
    Expression expr;

    public StatementReturnExpr(Token tok, Expression expr){
        super(tok);
        this.expr = expr;
    }

    @Override
    public String toString() {
        return "return " + expr + " ;";
    }

    @Override
    public void analyze(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap)
            throws SemanticAnalysisException {
        Type retType = varAndParamMap.get("0return");

        //In the body of the program return statement is not valid so
        if (retType == null)
            throw new SemanticAnalysisException("The body cannot contain return statement", this);

        //The return type cannot be void
        if (retType.getValue().equals("void"))
            throw new SemanticAnalysisException("The function does not return anything", getLine(), getColumn());
        //Now check if the return type and the expression type match
        else if (!retType.getValue().equals(expr.analyzeAndGetType(funcMap, varAndParamMap).getValue()))
            throw new SemanticAnalysisException("The return type and expression type must match",
                    getLine(), getColumn());
    }

    @Override
    public void execute(Map<String, FunctionDecl> funcMap, Map<String, Value> varAndParamMap)
            throws ReturnFromCall, ExecutionException {
        throw new ReturnFromCall(expr.evaluate(funcMap, varAndParamMap));
    }
}
