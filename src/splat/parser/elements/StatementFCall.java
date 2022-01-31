package splat.parser.elements;

import splat.executor.*;
import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;

import java.lang.reflect.Executable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatementFCall extends Statement{
    Label label;
    List <Expression> args;

    public StatementFCall(Token tok, Label label, List<Expression> args){
        super(tok);
        this.label = label;
        this.args = args;
    }

    @Override
    public String toString() {
        String fCall = label + "(";
        for (int i = 0; i < args.size(); i++) {
            if (i == 0)
                fCall = fCall + args.get(i);
            else
                fCall = fCall + "," + args.get(i);
        }
        fCall = fCall + ") ;";

        return fCall;
    }


    @Override
    public void analyze (Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap)
            throws SemanticAnalysisException{
        String label = this.label.toString();

        if (funcMap.containsKey(label)){
            FunctionDecl funcDecl = funcMap.get(label);
            if (!funcDecl.getRetType().getValue().equals("void"))
                throw new SemanticAnalysisException("Function call as a statement must have void return type",
                        funcDecl.getLine(), funcDecl.getColumn());
            if (!(args.size() == funcDecl.getParams().size()))
                throw new SemanticAnalysisException("Different number of paramters and arguments",
                        funcDecl.getLine(), funcDecl.getColumn());
            //Now check the correspondence between the arguments and the expected params
            //Check if each parameter pair have the same type
            boolean thereMisMatch = false;
            for (int i = 0; i < args.size(); i++){
                if (!args.get(i).analyzeAndGetType(funcMap, varAndParamMap).getValue().equals(funcDecl.getParams().get(i).getType().getValue())){
                    thereMisMatch = true;
                    break;
                }
            }
            if (thereMisMatch)
                throw new SemanticAnalysisException("Unexpected function argument type", this);
        }
        else {
            throw new SemanticAnalysisException("Call for function that was not declared",
                    getLine(), getColumn());
        }
    }

    @Override
    public void execute(Map<String, FunctionDecl> funcMap, Map<String, Value> varAndParamMap)
            throws ReturnFromCall, ExecutionException {

        String label = this.label.getValue();
        FunctionDecl funcDecl = funcMap.get(label);

        //First create local var and param map
        Map<String, Value> localVarAndParam = new HashMap<>();

        //Add params
        for (int i = 0; i < args.size(); i++){
            localVarAndParam.put(funcDecl.getParams().get(i).getLabel().getValue(),
                    args.get(i).evaluate(funcMap, varAndParamMap));
        }

        //Add variables
        for (VariableDecl var : funcDecl.getLocalVarDecls()){
            String type = var.getType().getValue();
            Value value;
            if (type.equals("Integer"))
                value = new ValueInt();
            else if (type.equals("String"))
                value = new ValueString();
            else
                value = new ValueBool();
            localVarAndParam.put(var.getLabel().getValue(), value);
        }

        //Now execute the stmts of the function
        try{
            for (Statement stmt : funcDecl.getStmts())
                stmt.execute(funcMap, localVarAndParam);
        }
            catch (ReturnFromCall ex){}
    }
}
