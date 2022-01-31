package splat.parser.elements;

import splat.lexer.Token;
import splat.parser.ParseException;

public class VariableDecl extends Declaration {

	// Fields to represent components <label> and <type>
	Label label;
	Type type;
	
	// Need to add extra arguments for setting fields in the constructor 
	public VariableDecl(Token tok, Label label, Type type) throws ParseException {
		super(tok);
		this.label = label;
		this.type = type;
	}

	public Label getLabel() {
		return label;
	}

	public Type getType() {
		return type;
	}

	@Override
	public String toString() {
		return label + ":" + type + ";";
	}
}
