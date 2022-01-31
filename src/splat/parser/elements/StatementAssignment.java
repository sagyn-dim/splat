package splat.parser.elements;

import splat.executor.ExecutionException;
import splat.executor.ReturnFromCall;
import splat.executor.Value;
import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;

import java.util.Map;

public class StatementAssignment extends Statement{
    Label label;
    Expression expr;

    public StatementAssignment(Token tok, Label label, Expression expr){
        super(tok);
        this.label = label;
        this.expr = expr;
    }

    @Override
    public String toString() {
        return label + " := " + expr + " ;";
    }

    @Override
    public void analyze(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap)
            throws SemanticAnalysisException {

        //First check if the label was declared
        String label = this.label.getValue();

        if (varAndParamMap.containsKey(label)){
            String type = varAndParamMap.get(label).getValue();

            //Now check if the type of the expression match
            if (!type.equals(expr.analyzeAndGetType(funcMap, varAndParamMap).getValue()))
                throw new SemanticAnalysisException("The types of the expression and variable " +
                        "do not match", getLine(), getColumn());
        }
        else
            throw new SemanticAnalysisException("The variable was not declared", getLine(), getColumn());
    }

    @Override
    public void execute(Map<String, FunctionDecl> funcMap, Map<String, Value> varAndParamMap)
            throws ReturnFromCall, ExecutionException {
        //The evaluation of evaluate might throw a ReturnException
        varAndParamMap.put(label.getValue(), expr.evaluate(funcMap, varAndParamMap));
    }
}
