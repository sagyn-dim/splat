package splat.parser.elements;

import splat.executor.ExecutionException;
import splat.executor.Value;
import splat.executor.ValueBool;
import splat.executor.ValueInt;
import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;

import java.util.Map;

public class ExpressionUnaryOp extends Expression {
    private String unOp;
    private Expression expr;

    public ExpressionUnaryOp(Token tok, Token unOp, Expression expr){
        super(tok);
        this.unOp = unOp.getValue();
        this.expr = expr;
    }

    @Override
    public String toString() {
        return "(" + unOp + expr + ")";
    }

    @Override
    public Type analyzeAndGetType(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap)
            throws SemanticAnalysisException {
        Type type = expr.analyzeAndGetType(funcMap, varAndParamMap);
        //In unary expression the type is either bool or int
        if (unOp.equals("not")){
            //Check if the underlying subexpression is bool
            if (type.getValue().equals("Boolean"))
                return type;
            else
                throw new SemanticAnalysisException("After \"not\" the expression must be boolean",
                        getLine(), getColumn());
        }
        else{
            if (type.getValue().equals("Integer"))
                return type;
            else
                throw new SemanticAnalysisException("After - the expression must be integer",
                        getLine(), getColumn());
        }
    }

    @Override
    public Value evaluate(Map<String, FunctionDecl> funcMap, Map<String, Value> varAndParamMap)
    throws ExecutionException {
        //The case when the unary operator is "not"
        if (unOp.equals("not")){
            ValueBool valBool = new ValueBool();
            valBool.setValue(!((ValueBool)expr.evaluate(funcMap, varAndParamMap)).getValue());
            return valBool;
        }
        //If the unary op is "-"
        else{
            ValueInt valInt = (ValueInt)expr.evaluate(funcMap, varAndParamMap);
            valInt.setValue((-1) * valInt.getValue());
            return valInt;
        }
    }
}
