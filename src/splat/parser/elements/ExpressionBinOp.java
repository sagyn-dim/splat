package splat.parser.elements;

import splat.executor.*;
import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;
import splat.semanticanalyzer.SemanticAnalyzerTester;

import java.util.Map;

public class ExpressionBinOp extends Expression{
    private Expression expr1, expr2;
    private String binOp;
    Token tok;
    String type;

    public ExpressionBinOp (Token tok, Expression expr1, Token binOp, Expression expr2){
        super(tok);
        this.tok = tok;
        this.expr1 = expr1;
        this.expr2 = expr2;
        this.binOp = binOp.getValue();
    }

    @Override
    public String toString() {
        return "(" + expr1 + binOp + expr2 + ")";
    }

    @Override
    public Type analyzeAndGetType(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap)
            throws SemanticAnalysisException {
        Type type1 = expr1.analyzeAndGetType(funcMap, varAndParamMap);
        Type type2 = expr2.analyzeAndGetType(funcMap, varAndParamMap);
        this.type = type1.getValue();

        //See if the type of two expression match
        if (type1.getValue().equals(type2.getValue())){
            String type = type1.getValue();

            //Check the operator based on the derived type
            if (type.equals("Boolean")){
                if (binOp.equals("and") || binOp.equals("or") || binOp.equals("=="))
                    return type1;
                else
                    throw new SemanticAnalysisException("Illegal operator for these expressions",
                            getLine(), getColumn());
            }
            else if (type.equals("Integer")){
                if (binOp.equals(">") || binOp.equals("<") ||binOp.equals(">=") ||binOp.equals("<=") ||
                        binOp.equals("=="))
                    return new Type("Boolean", tok);
                else if (binOp.equals("+") || binOp.equals("-") || binOp.equals("*") || binOp.equals("/") ||
                        binOp.equals("%"))
                    return type1;
                else
                    throw new SemanticAnalysisException("Illegal operator for these expressions",
                            getLine(), getColumn());
            }
            else if (type.equals("String")){
                if (binOp.equals("=="))
                    return new Type("Boolean", tok);
                else if (binOp.equals("+"))
                    return type1;
                else
                    throw new SemanticAnalysisException("Illegal operator for these expressions", this);
            }
            else
                throw new SemanticAnalysisException("Uknown types of the expression", this);
        }
        else
            throw new SemanticAnalysisException("The types of subexpression do not match",
                    getLine(), getColumn());
    }

    @Override
    public Value evaluate(Map<String, FunctionDecl> funcMap, Map<String, Value> varAndParamMap) throws
            ExecutionException{
        //Evaluate the two expression
        Value val1 = expr1.evaluate(funcMap, varAndParamMap);
        Value val2 = expr2.evaluate(funcMap, varAndParamMap);

        //First we have to know the type of the expression and since the types must match only check one
        if (type.equals("Integer")){
            int valInt1 = ((ValueInt)val1).getValue();
            int valInt2 = ((ValueInt)val2).getValue();
            switch (binOp){
                case "+":
                    return new ValueInt(valInt1 + valInt2);
                case "-":
                    return new ValueInt(valInt1- valInt2);
                case "*":
                    return new ValueInt(valInt1 * valInt2);
                case "/":
                    if (valInt2 == 0)
                        throw new ExecutionException("Execution exception! Division by zero", this);
                    return new ValueInt(valInt1 / valInt2);
                case "%":
                    return new ValueInt(valInt1 % valInt2);
                case "<":
                    return new ValueBool(valInt1 < valInt2);
                case ">":
                    return new ValueBool(valInt1 > valInt2);
                case "<=":
                    return new ValueBool(valInt1 <= valInt2);
                case ">=":
                    return new ValueBool(valInt1 >= valInt2);
                case "==":
                    return new ValueBool(valInt1 == valInt2);
            }
        }
        else if (type.equals("Boolean")){
            boolean bool1 = ((ValueBool)val1).getValue();
            boolean bool2 = ((ValueBool)val2).getValue();

            switch (binOp){
                case "and":
                    return new ValueBool(bool1 && bool2);
                case "or":
                    return new ValueBool(bool1 || bool2);
                case "==":
                    return new ValueBool(bool1 == bool2);
            }
        }
        //Else it is string
        else{
            String str1 = ((ValueString)val1).getValue();
            String str2 = ((ValueString)val2).getValue();

            switch (binOp){
                case "==":
                    return new ValueBool(str1.equals(str2));
                case "+":
                    return new ValueString(str1 + str2);
            }
        }
        return null;
    }
}
