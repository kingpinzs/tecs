/**
 * Jack language tokenizer token types.
 * 
 * @author John Ness
 */
public enum TokenType {

	// class, method, function, etc.
	KEYWORD,
	
	// '{', '}', ';', etc.
	SYMBOL,
	
	// Programmer-defined class and variable names.
	IDENTIFIER
}
