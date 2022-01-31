package splat.parser.elements;

import splat.executor.*;
import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;

import java.util.Map;

public class StatementPrint extends Statement{
    Expression expr;
    String type;

    public StatementPrint(Token tok, Expression expr){
        super(tok);
        this.expr = expr;
    }

    @Override
    public String toString() {
        return "print " + expr + " ;";
    }

    @Override
    public void analyze(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) throws SemanticAnalysisException {
        type = expr.analyzeAndGetType(funcMap, varAndParamMap).getValue();
    }

    @Override
    public void execute(Map<String, FunctionDecl> funcMap, Map<String, Value> varAndParamMap)
            throws ReturnFromCall, ExecutionException {
        if (type.equals("Integer"))
            System.out.print(((ValueInt)expr.evaluate(funcMap, varAndParamMap)).getValue());
        else if (type.equals("Boolean"))
            System.out.print(((ValueBool)expr.evaluate(funcMap, varAndParamMap)).getValue());
        else {
            String result = ((ValueString) expr.evaluate(funcMap, varAndParamMap)).getValue();
            System.out.print(result.substring(1, result.length() - 1));
        }
    }
}
