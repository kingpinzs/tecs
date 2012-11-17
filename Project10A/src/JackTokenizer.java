import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Remove comments and tokenize a .jack file.
 * 
 * @author John Ness
 */
public class JackTokenizer {

	private final FileReader input;
	private char nextCharacter;
	private TokenType tokenType;
	private KeyWord keyWord;
	private String currentToken;
	
	/**
	 * Create tokenizer (to remove comments and tokenize a .jack file).
	 * After creating tokenizer instance, call advance before calling
	 * other public methods to check first token.
	 * 
	 * @param inputFileName Input file name.
	 * @throws FileNotFoundException If input file can't be found.
	 */
	public JackTokenizer(String inputFileName) throws FileNotFoundException {
		input = new FileReader(inputFileName);
		readNextCharacter(); // Prepare for first call to advance.
	}
	
	private void readNextCharacter() {
		
		try {
			nextCharacter = (char) input.read();
			
		} catch (IOException e) {
			System.err.println("Problem reading file :(");
			System.exit(0);
		}
	}
	
	/**
	 * Do we have more tokens in the input?
	 * 
	 * @return true if we do, false otherwise.
	 */
	public boolean hasMoreTokens() {
		return (nextCharacter != 65535 /* EOF */);
	}
	
	/**advance
	 * Gets the next token from the input and makes it the current token.
	 * This method should be called only if hasMoreTokens returns true.
	 * Initially there is no current token.
	 */
	public void advance() {
		
		if (nextCharacter == '/') {
			readNextCharacter();
			commentOrDivisionOperator();
			
		} else if (Character.isWhitespace(nextCharacter)) {
			readNextCharacter();
			advance();  // Skip ahead to next token after whitespace.
			
		} else if (nextCharacter == '_' ||
				Character.isLetter(nextCharacter)) {
			currentToken = "" + nextCharacter;
			readNextCharacter();
			keywordOrIdentifier();
			
		} else if (nextCharacter == '{' || nextCharacter == '}' ||
				nextCharacter == ';' || nextCharacter == ',' ||
				nextCharacter == '(' || nextCharacter == ')' ||
				nextCharacter == '[' || nextCharacter == ']' ||
				nextCharacter == '.' || nextCharacter == '+' ||
				nextCharacter == '-' || nextCharacter == '*' ||
				nextCharacter == '&' || nextCharacter == '|' ||
				nextCharacter == '<' || nextCharacter == '>' ||
				nextCharacter == '=' || nextCharacter == '~') {
			tokenType = TokenType.SYMBOL;
			currentToken = "" + nextCharacter;
			readNextCharacter();  // Prepare for next call to advance.
		}
	}
	
	private void commentOrDivisionOperator() {
		
		if (nextCharacter == '/') {
			readNextCharacter();
			singleLineComment();
			
		} else if(nextCharacter == '*') {
			readNextCharacter();
			multiLineComment();
			
		} else {
			tokenType = TokenType.SYMBOL;
			currentToken = "/";
			// Don't need to call readNextCharacter to prepare for advance,
			// since the character after the / has already been read.
		}
	}
	
	private void singleLineComment() {

		if (nextCharacter == '\n') {
			readNextCharacter();
			advance();  // Skip ahead to next token after comment.
		
		} else {
			readNextCharacter();
			singleLineComment();
		}
	}
	
	private void multiLineComment() {
		
		if(nextCharacter == '*') {
			readNextCharacter();
			multiLineCommentAfterStar();
		
		} else {
			readNextCharacter();
			multiLineComment();
		}
	}
	
	private void multiLineCommentAfterStar() {
		
		if(nextCharacter == '/') {
			readNextCharacter();
			advance();
		
		} else if(nextCharacter == '*') {
			readNextCharacter();
			multiLineCommentAfterStar();
		
		} else {
			readNextCharacter();
			multiLineComment();
		}
	}
	
	private void keywordOrIdentifier() {
		
		if (nextCharacter == '_' ||
				Character.isLetterOrDigit(nextCharacter)) {
			currentToken += nextCharacter;
			readNextCharacter();
			keywordOrIdentifier();
			
		} else if (currentToken.equals("class")) {
			tokenType = TokenType.KEYWORD;
			keyWord = KeyWord.CLASS;
			// Don't need to call readNextCharacter to prepare for advance,
			// since the character after the keyword has already been read.
		} else if(currentToken.equals("static")) {
			tokenType = TokenType.KEYWORD;
			keyWord = KeyWord.STATIC;
				
		} else if(currentToken.equals("field")) {
			tokenType = TokenType.KEYWORD;
			keyWord = KeyWord.FIELD;
		
		} else if(currentToken.equals("constructor")) {
			tokenType = TokenType.KEYWORD;
			keyWord = KeyWord.CONSTRUCTOR;
		
		} else if(currentToken.equals("function")) {
			tokenType = TokenType.KEYWORD;
			keyWord = KeyWord.FUNCTION;
		
		} else if(currentToken.equals("method")) {
			tokenType = TokenType.KEYWORD;
			keyWord = KeyWord.METHOD;
			
		} else if(currentToken.equals("var")) {
			tokenType = TokenType.KEYWORD;
			keyWord = KeyWord.VAR;
			
		} else if(currentToken.equals("void")) {
			tokenType = TokenType.KEYWORD;
			keyWord = KeyWord.VOID;
			
		} else if(currentToken.equals("true")) {
			tokenType = TokenType.KEYWORD;
			keyWord = KeyWord.TRUE;
			
		} else if(currentToken.equals("false")) {
			tokenType = TokenType.KEYWORD;
			keyWord = KeyWord.FALSE;
			
		} else if(currentToken.equals("null")) {
			tokenType = TokenType.KEYWORD;
			keyWord = KeyWord.NULL;
			
		} else if(currentToken.equals("this")) {
			tokenType = TokenType.KEYWORD;
			keyWord = KeyWord.THIS;
			
		} else if(currentToken.equals("let")) {
			tokenType = TokenType.KEYWORD;
			keyWord = KeyWord.LET;
			
		} else if(currentToken.equals("return")) {
			tokenType = TokenType.KEYWORD;
			keyWord = KeyWord.RETURN;
			
		} else if(currentToken.equals("while")) {
			tokenType = TokenType.KEYWORD;
			keyWord = KeyWord.WHILE;
			
		} else if(currentToken.equals("if")) {
			tokenType = TokenType.KEYWORD;
			keyWord = KeyWord.IF;
			
		} else if(currentToken.equals("else")) {
			tokenType = TokenType.KEYWORD;
			keyWord = KeyWord.ELSE;
			
		} else if(currentToken.equals("int")) {
			tokenType = TokenType.KEYWORD;
			keyWord = KeyWord.INT;
			
		} else if(currentToken.equals("boolean")) {
			tokenType = TokenType.KEYWORD;
			keyWord = KeyWord.BOOLEAN;
			
		} else if(currentToken.equals("char")) {
			tokenType = TokenType.KEYWORD;
			keyWord = KeyWord.CHAR;
			
		} else {
			tokenType = TokenType.IDENTIFIER;
			// Don't need to call readNextCharacter to prepare for advance,
			// since the character after the identifier has already been read.
		}
	}
	
	/**
	 * Returns the type of the current token.
	 * 
	 * @return Type of current token (e.g., TokenType.KEYWORD).
	 */
	public TokenType tokenType() {
		return tokenType;
	}
	
	/**
	 * Returns the keyword which is the current token.  Should be called
	 * only when tokenType returns TokenType.KEYWORD.
	 * 
	 * @return The current token (e.g., KeyWord.CLASS).
	 */
	public KeyWord keyWord() {
		return keyWord;
	}
	
	/**
	 * Returns the character which is the current token.  Should be called
	 * only when tokenType returns TokenType.SYMBOL.
	 * 
	 * @return The current token (a single-character symbol, e.g., '{').
	 */
	public char symbol() {
		return currentToken.charAt(0);
	}
	
	/**
	 * Returns the identifier which is the current token.  Should be called
	 * only when tokenType returns TokenType.IDENTIFIER.
	 * 
	 * @return The current token (an identifier).
	 */
	public String identifier() {
		return currentToken;
	}

	/**
	 * Close the input file.
	 * 
	 * @throws IOException If there's a problem closing the input file.
	 */
	public void close() throws IOException {
		input.close();
	}
}
