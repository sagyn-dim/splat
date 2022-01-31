package splat.parser.elements;

import splat.executor.Value;
import splat.lexer.Token;
import splat.parser.ParseException;
import splat.semanticanalyzer.SemanticAnalysisException;

import java.util.Arrays;
import java.util.Map;

public class Label extends Expression {
    private String value;

    public Label(Token tok) throws ParseException{
        super(tok);
        //First we check if the label is legal i.e. contains letters or underscore, otherwise it is a number
        String label = tok.getValue();
        boolean hasAlphaUnder = false;
        boolean startWithQuot = true;
        boolean notKeyWord =    false;
        boolean legalAll;

        //Label has to contain alphanumeric or underscore, it must not be only digits
        String alphaUnder = "QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm_";
        for (int i = 0; i < label.length(); i++){
            if (alphaUnder.contains(String.valueOf(label.charAt(i)))){
                hasAlphaUnder = true;
                break;
            }
        }
        //Label cannot be a key word
        String[] keyWords = {"while", "end", "if", "else", "program", "begin", "is", "do", "then",
                "print", "print_line", "return", "and", "or", "not", "void", "Integer", "Boolean",
                "String", "true", "false"};
        notKeyWord = !Arrays.stream(keyWords).anyMatch(label::equals);

        //Now check if it is not a string i.e. does not start with "
        legalAll = !label.startsWith("\"") && notKeyWord && hasAlphaUnder;

        //If legal assign the fields
        if(legalAll)
            this.value = label;
        else //Throw exception
            throw new ParseException("Illegal Label!", tok.getLine(), tok.getColumn());

    }

    @Override
    public Type analyzeAndGetType(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) throws SemanticAnalysisException {
        if (!varAndParamMap.containsKey(value))
            throw new SemanticAnalysisException("Cannot call variable that was not declared", this);
        else
            return varAndParamMap.get(value);
    }

    @Override
    public Value evaluate(Map<String, FunctionDecl> funcMap, Map<String, Value> varAndParamMap) {
        return varAndParamMap.get(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
