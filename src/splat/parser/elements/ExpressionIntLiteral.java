package splat.parser.elements;

import splat.executor.Value;
import splat.executor.ValueInt;
import splat.lexer.Token;

import java.util.Map;

public class ExpressionIntLiteral extends Expression{
    private String intLiteral;
    private Type type;

    public ExpressionIntLiteral(Token tok){
        super(tok);
        this.type = new Type("Integer", tok);
        this.intLiteral = tok.getValue();
    }

    @Override
    public Type analyzeAndGetType(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) {
        return type;
    }

    @Override
    public Value evaluate(Map<String, FunctionDecl> funcMap, Map<String, Value> varAndParamMap) {
        return new ValueInt(Integer.valueOf(intLiteral));
    }

    @Override
    public String toString() {
        return intLiteral;
    }
}
