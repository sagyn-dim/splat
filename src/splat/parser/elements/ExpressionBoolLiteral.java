package splat.parser.elements;

import splat.executor.Value;
import splat.executor.ValueBool;
import splat.lexer.Token;

import java.util.Map;

public class ExpressionBoolLiteral extends Expression{
    private String bool;
    private Type type;

    public ExpressionBoolLiteral(Token tok){
        super(tok);
        this.type = new Type("Boolean", tok);
        this.bool = tok.getValue();
    }

    @Override
    public Type analyzeAndGetType(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) {
        return type;
    }

    @Override
    public Value evaluate(Map<String, FunctionDecl> funcMap, Map<String, Value> varAndParamMap) {
        return new ValueBool(Boolean.valueOf(bool));
    }

    @Override
    public String toString() {
        return bool;
    }
}
