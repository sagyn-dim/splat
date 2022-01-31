package splat.lexer;


import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Lexer{
	BufferedReader reader;

	public Lexer(File progFile) {
		try {
			//We start creating BufferedReader object
			this.reader = new BufferedReader(new FileReader(progFile)); //Reader Created
		}
		catch (IOException ex){}
	}

	public List<Token> tokenize() throws LexException{
		//Here we start the tokenization
		List<Token> tokens = new ArrayList<Token>();

		//Initialize base values
		String token = "";
		int ch = -1;
		int line = 1;
		int col = 0;
		String alphabet = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM_";
		String numeric = "1234567890";

		//This is a boolean to keep track of double quotes
		boolean quoteOpen = false;

		//Start reading
		do
		{
			//Keep reading
			try {
				ch = reader.read();
			}
			catch (IOException exception) {
				System.out.println(exception.getMessage());
			}

			//Increment column and line numbers
			if (ch == '\n')//New line
			{
				line++;
				col = 0;
			}
			else {
				if (ch == '\t') //1 tab = 4 spaces
					col = col + 4;
				else
					col++;
			}

			//Deal with quotation
			if (ch == '"' )
			{
				quoteOpen = !quoteOpen;			//Each new quot mark either closes or opens quote
				if (quoteOpen == false) { 		//If the new one closes quote - save the whole token
					tokens.add(new Token(token + (char) ch, line, col - token.length()));
					token = "";
				}
				else{ 							//If the new one opens, save the prev token, keep track of new
					if (token.length() > 0)
						tokens.add(new Token(token, line, col - token.length()));
					token = "" + (char)ch;
				}
			}

			//Deal with the end of the text
			else if(ch == -1)
			{
				//If there is open quote mark left throw exception
				if (quoteOpen == true)
					throw new LexException("Exception: Unfinished String!", line, col - token.length());
				else if (token.length() > 0)
					tokens.add(new Token(token, line, col - token.length()));
			}

			//If the quotation is open keep increasing the token
			else if(quoteOpen == true)
				token = token + (char)ch;

			//Deal with basic deliminators
			else if (ch == ' ' || ch == '\n' || ch == '\r' || ch == '\t')
			{
//				System.out.println("Token: " + token);
				if (token.length() > 0) {
					tokens.add(new Token(token, line, col - token.length()));
					token = "";
				}
			}
			//Deal with special symbols like :, ; and etc. which are STANDALONE special symbols
			//STANDALONE means that this symbols is certainly not followed by another symbol to make
			//a larger special symbol
			else if (ch == ';' || ch == '(' || ch == ')' || ch == '+' || ch == '-' || ch == '*' || ch == '/'
					||ch == '%' || ch == ',')
			{
				if (token.length() > 0) {
					tokens.add(new Token(token, line, col - token.length()));
					token = "";
				}
				String stdAlone = "" + (char)ch;
				tokens.add(new Token(stdAlone, line, col));
			}

			//Deal with tokens that can be a bigger token Phase 2: <, <=, >, >=, :, :=, ==,

			//Special case of == if there is symbol = with no prior :, it has to be ==, otherwise throw exception
			else if(token.equals("=")) {
				if (ch != '=')
					throw new LexException("Exception! Illegal token: =", line, col - 1);
				else
					tokens.add(new Token(token + (char)ch, line, col - 1));
					token = "";
			}

			//Case of <= or >=
			else if(token.equals("<")  || token.equals(">"))
			{
				if (ch == '=') //If can be bigger make it bigger
				{
					token = token + (char)ch;
					tokens.add(new Token(token, line, col - 1));
					token = "";
				}
				else//if not store the previous token and move on
				{
					tokens.add(new Token(token, line, col - 1));
					token = "" + (char) ch;
				}
			}

			//Case of : or :=
			else if (token.equals(":"))
			{
				if (ch == '=')
				{
					tokens.add(new Token(token + (char)ch, line, col - 1));
					token = "";
				}
				else
				{
					tokens.add(new Token(token, line, col - 1));
					token = "" + (char)ch;
				}
			}

			//Dealing with tokens that can be a bigger token: Phase 1 see if it can be bigger
			else if(ch == '<' || ch == '>' || ch == ':' || ch == '=')
			{
				//Add the previous token first
				if (token.length() > 0)
					tokens.add(new Token(token, line, col - token.length()));
				//Then update the token
				token = "";
				token = token + (char)ch;
			}

			//Dealing with illegal chars
			else if(!alphabet.contains(String.valueOf((char)ch)) && !numeric.contains(String.valueOf((char)ch)))
			{
				throw new LexException("Exception! Illegal symbol: " + (char)ch, line, col);
			}

			//Dealing with labels that start with
			else if (token.length() > 0)//If the token is not empty
			{
				if (numeric.contains(String.valueOf(token.charAt(0)))) //If the token starts with a digit
				{
					if (alphabet.contains(String.valueOf((char)ch))) //If the char is letter or underscore
						throw new LexException("Exception! Label cannot start with a digit.", line, col - token.length());
					//Otherwise just increase the token
					else token = token + (char)ch;
				}
				else token = token + (char)ch;
			}
			else
				token = token + (char)ch;


		}while (ch != -1);

		return tokens;
	}

}
