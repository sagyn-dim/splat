package splat.parser.elements;

import splat.lexer.Token;

public class Param extends ASTElement{
    Label label;
    Type type;

    public Param (Token tok, Label label, Type type){
        super(tok);
        this.label = label;
        this.type = type;
    }

    @Override
    public String toString() {
        return label + ":" + type;
    }

    public Label getLabel() {
        return label;
    }

    public Type getType() {
        return type;
    }
}
