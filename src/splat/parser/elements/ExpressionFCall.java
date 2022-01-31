package splat.parser.elements;

import splat.executor.*;
import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;

import javax.swing.plaf.nimbus.State;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpressionFCall extends Expression{
    private Label label;
    private List<Expression> args;

    public ExpressionFCall(Token tok, Label label, List <Expression> args){
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
        fCall = fCall + ")";

        return fCall;
    }

    @Override
    public Type analyzeAndGetType(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap)
    throws SemanticAnalysisException{
        String label = this.label.toString();
        //Check if such function exists
        if (funcMap.containsKey(label)){
            FunctionDecl funcDecl = funcMap.get(label);
            //Check if the number of parameters match
            if (args.size() == funcDecl.getParams().size()){
                //Check if each parameter pair have the same type
                boolean thereMisMatch = false;
                for (int i = 0; i < args.size(); i++){
                    if (!args.get(i).analyzeAndGetType(funcMap, varAndParamMap).getValue().equals(funcDecl.getParams().get(i).getType().getValue())){
                        thereMisMatch = true;
                        break;
                    }
                }
                if (thereMisMatch)
                    throw new SemanticAnalysisException("The types of at least one parameter pair do not match",
                            getLine(), getColumn());

                if (funcDecl.getRetType().getValue().equals("void"))
                    throw new SemanticAnalysisException("Function call expression cannot have return type void",
                            this);

                //Finally return the return type of the called function
                return funcDecl.getRetType();

            }
            else
                throw new SemanticAnalysisException("Number of parameters must match", getLine(), getColumn());
        }
        else
            throw new SemanticAnalysisException("Called function was not declared", getLine(), getLine());
    }

    @Override
    public Value evaluate(Map<String, FunctionDecl> funcMap, Map<String, Value> varAndParamMap) throws
            ExecutionException {

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
        try {
            for (Statement stmt : funcDecl.getStmts())
                stmt.execute(funcMap, localVarAndParam);
        }
        catch (ReturnFromCall ex){
            return ex.getReturnVal();
        }
        return null;
    }
}
