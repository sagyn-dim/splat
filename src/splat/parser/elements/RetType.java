package splat.parser.elements;

import splat.lexer.Token;
import splat.parser.ParseException;

public class RetType extends Type{
    private String value;

    public RetType(Token tok) throws ParseException {
        super(tok.getLine(), tok.getColumn());

        //First we check if the type is legal
        String type = tok.getValue();
        boolean legalType = false;

        //Type must match specified three
        if (type.equals("Integer") || type.equals("Boolean") || type.equals("String") || type.equals("void"))
            legalType = true;

        if(legalType)
            value = type;
        else
            throw new ParseException("Illegal type!", tok.getLine(), tok.getColumn());
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
