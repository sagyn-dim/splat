package splat.parser.elements;

import splat.executor.Value;
import splat.executor.ValueString;
import splat.lexer.Token;

import java.util.Map;

public class ExpressionStringLiteral extends Expression{
    private String strLit;
    private Type type;

    public ExpressionStringLiteral(Token tok){
        super(tok);
        this.type = new Type("String", tok);
        this.strLit = tok.getValue();
    }

    @Override
    public Type analyzeAndGetType(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) {
        return type;
    }

    @Override
    public Value evaluate(Map<String, FunctionDecl> funcMap, Map<String, Value> varAndParamMap) {
        return new ValueString(strLit);
    }

    @Override
    public String toString() {
        return strLit;
    }
}
