package splat.parser.elements;

import splat.lexer.Token;
import splat.parser.ParseException;

public class Type extends ASTElement{
    private String value;

    public Type(Token tok) throws ParseException {
        super(tok);

        //First we check if the type is legal
        String type = tok.getValue();
        boolean legalType = false;

        //Type must match specified three
        if (type.equals("Integer") || type.equals("Boolean") || type.equals("String"))
            legalType = true;

        if(legalType)
            value = type;
        else
            throw new ParseException("Illegal type!", tok.getLine(), tok.getColumn());
    }
    public Type (String type, Token tok){
        super(tok);
        this.value = type;
    }
    public Type (int line, int col){
        super(line, col);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
