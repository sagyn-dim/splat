package splat.parser.elements;

import splat.executor.ExecutionException;
import splat.executor.ReturnFromCall;
import splat.executor.Value;
import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;

import java.util.Map;

public class StatementReturn extends Statement{
    public StatementReturn(Token tok){
        super(tok);
    }

    @Override
    public void analyze(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap)
            throws SemanticAnalysisException {
        Type retType = varAndParamMap.get("0return");

        //In the body of the program return statement is not valid so
        if (retType == null)
            throw new SemanticAnalysisException("The body cannot contain return statement", this);

        //To use return statement the return type must be void
        if (!retType.getValue().equals("void"))
            throw new SemanticAnalysisException("Void function does not return a value", getLine(), getColumn());
    }

    @Override
    public void execute(Map<String, FunctionDecl> funcMap, Map<String, Value> varAndParamMap)
            throws ReturnFromCall, ExecutionException {
        throw new ReturnFromCall();
    }

    @Override
    public String toString() {
        return "return ;";
    }
}
