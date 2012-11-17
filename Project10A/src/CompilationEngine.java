import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.HeaderTokenizer.Token;

/**
 * Compile a .jack file.
 * 
 * @author John Ness
 */
public class CompilationEngine {

	private static final int TAB_WIDTH = 2;
	private int indentLevel = 0;
	private final JackTokenizer tokenizer;
	private final PrintStream out;
	
	/**
	 * Compile a .jack file.  (Eventually this class will compile a .jack
	 * file.  This version just parses the file, creating an .xml file
	 * showing the resulting parse tree.)
	 * 
	 * @param inputFileName
	 * @param outputFileName
	 * @throws FileNotFoundException
	 */
	public CompilationEngine(String inputFileName, String outputFileName)
			throws FileNotFoundException, IOException {
		tokenizer = new JackTokenizer(inputFileName);
		out = new PrintStream(outputFileName);
		tokenizer.advance();
		compileClass();
		tokenizer.close();
		out.close();
	}
	
	/**
	 * Writes a line indented based on an indent level.
	 * 
	 * @param line Line to write indented
	 */
	private void writeIndentedLine(String line) {
		String s = "";
		
		for (int i = 0; i < indentLevel * TAB_WIDTH; i++)
			s += " ";
		
		out.println(s + line);
		System.out.println(s + line);    // Just for testing.
	}
	
	/**
	 * Compiles the Jack class.
	 */
	private void compileClass() {
		writeIndentedLine("<class>");
		indentLevel++;
		
		// 'class'
		writeIndentedLine("<keyword> class </keyword>");
		tokenizer.advance();
		
		// className
		// (Although there's a grammar rule for className, the .xml files
		// from the textbook website don't include a set of tags for it,
		// so we won't either.)
		writeIndentedLine("<identifier> " + tokenizer.identifier() +
				" </identifier>");
		tokenizer.advance();
		
		// '{'
		writeIndentedLine("<symbol> { </symbol>");
		tokenizer.advance();
		
		// classVarDec*
		while (tokenizer.tokenType() == TokenType.KEYWORD &&
				(tokenizer.keyWord() == KeyWord.STATIC ||
				 tokenizer.keyWord() == KeyWord.FIELD))
		compileClassVarDec();
		
		// subroutineDec*
		while (tokenizer.tokenType() == TokenType.KEYWORD &&
				(tokenizer.keyWord() == KeyWord.CONSTRUCTOR || 
				tokenizer.keyWord() == KeyWord.METHOD || 
				tokenizer.keyWord() == KeyWord.FUNCTION))
		compileSubroutine();
		
		// '}'
		writeIndentedLine("<symbol> } </symbol>");
		tokenizer.advance();
		
		indentLevel--;
		writeIndentedLine("</class>");
	}
	
	/**
	 * Compiles the Jack class variables.
	 */
	private void compileClassVarDec() {
		writeIndentedLine("<classVarDec>");
		indentLevel++;
		
		// ('static' | 'field')
		if (tokenizer.keyWord() == KeyWord.STATIC) {
			writeIndentedLine("<keyword> static </keyword>");
			tokenizer.advance();
			
		} else if (tokenizer.keyWord() == KeyWord.FIELD) {
			writeIndentedLine("<keyword> field </keyword>");
			tokenizer.advance();			
		
		}
		
		// type
		compileType();
		
		// varName
		writeIndentedLine("<identifier> " + tokenizer.identifier() +
				" </identifier>");
		tokenizer.advance();
		
		// (',' varName)
		while (tokenizer.symbol() == ',') {
			writeIndentedLine("<symbol> , </symbol>");
			tokenizer.advance();
			
			writeIndentedLine("<identifier> " + tokenizer.identifier() +
					" </identifier>");
			tokenizer.advance();
		}
		
		// ;
		writeIndentedLine("<symbol> ; </symbol>");
		tokenizer.advance();
		
		indentLevel--;
		writeIndentedLine("</classVarDec>");
	}
	
	/**
	 * Compiles the data-type.
	 */
	private void compileType() {
		
		// ('int' | 'char' | 'boolean' | className)
		if (tokenizer.tokenType() == TokenType.KEYWORD) {
			
			if (tokenizer.keyWord() == KeyWord.INT) {
				writeIndentedLine("<keyword> int </keyword>");
				tokenizer.advance();
				
			} else if (tokenizer.keyWord() == KeyWord.CHAR) {
				writeIndentedLine("<keyword> char </keyword>");
				tokenizer.advance();
				
			} else if(tokenizer.keyWord() == KeyWord.VAR) {
				writeIndentedLine("<keyword> var </keyword>");
				tokenizer.advance();
				
			} else if(tokenizer.keyWord() == KeyWord.VOID) {
				writeIndentedLine("<keyword> void </keyword>");
				tokenizer.advance();
				
			} else {
				writeIndentedLine("<keyword> boolean </keyword>");
				tokenizer.advance();
			}
			
		} else {
			writeIndentedLine("<identifier> " + tokenizer.identifier() +
					" </identifier>");
			tokenizer.advance();
		}
	}
	
	/**
	 * Compiles a Jack subroutine within a class.
	 */
	private void compileSubroutine() {
		
		writeIndentedLine("<subroutineDec>");
		indentLevel++;
		
		if(tokenizer.keyWord() == KeyWord.CONSTRUCTOR)
			writeIndentedLine("<keyword> constructor </keyword>");			
		else if(tokenizer.keyWord() == KeyWord.FUNCTION)
			writeIndentedLine("<keyword> function </keyword>");
		else if(tokenizer.keyWord() == KeyWord.METHOD)
			writeIndentedLine("<keyword> method </keyword>");
		
		//Return type
		tokenizer.advance();
		if(tokenizer.tokenType() == TokenType.KEYWORD)
			writeIndentedLine("<keyword> " + tokenizer.identifier() + " </keyword>");
		else
			writeIndentedLine("<identifier> " + tokenizer.identifier() + " </identifier>");
		
		//Subroutine name
		tokenizer.advance();
		if(tokenizer.tokenType() == TokenType.KEYWORD)
			writeIndentedLine("<keyword> " + tokenizer.identifier() + " </keyword>");
		else
			writeIndentedLine("<identifier> " + tokenizer.identifier() + " </identifier>");
		
		//Parameter list
		compileParameterList();
		
		//Subroutine body
		writeIndentedLine("<subroutineBody>");		
		indentLevel++;
		
		//Subroutine opening symbol
		tokenizer.advance();
		writeIndentedLine("<symbol> " + tokenizer.identifier() +" </symbol>");
		
		//Check for subroutine vars
		tokenizer.advance();
		if(tokenizer.keyWord() == KeyWord.VAR)
			compileVar();

		writeIndentedLine("<statements>");
		indentLevel++;
		
		//Compile inner statements
		while(tokenizer.keyWord() != KeyWord.RETURN) {
			
			compileStatement();			
			tokenizer.advance();
		}
		
		if(tokenizer.keyWord() == KeyWord.RETURN) {
			
			writeIndentedLine("<returnStatement>");
			indentLevel++;
			
			//Write return
			writeIndentedLine("<keyword> " + tokenizer.identifier() + " </keyword>");
			
			if(tokenizer.tokenType() != TokenType.SYMBOL) {
				
				tokenizer.advance();
				if(tokenizer.tokenType() == TokenType.KEYWORD || 
						tokenizer.tokenType() == TokenType.IDENTIFIER) {

					writeIndentedLine("<expression>");
					indentLevel++;
				
					writeIndentedLine("<term>");
					indentLevel++;
					
					if(tokenizer.tokenType() == TokenType.KEYWORD)
						writeIndentedLine("<keyword> " + tokenizer.identifier() + " </keyword>");
					else
						writeIndentedLine("<identifier> " + tokenizer.identifier() + " </identifier>");
					
					indentLevel--;
					writeIndentedLine("</term>");
				
					indentLevel--;
					writeIndentedLine("</expression>");
					tokenizer.advance();
				}	
					
			} else {
				writeIndentedLine("<symbol> " + tokenizer.symbol() + " </symbol>");
			}
			
			tokenizer.advance();
			writeIndentedLine("<symbol> ; </symbol>");
			
			indentLevel--;
			writeIndentedLine("</returnStatement>");
		}
		
		
		indentLevel--;
		writeIndentedLine("</statements>");
		
		//Subroutine closing symbol
		writeIndentedLine("<symbol> } </symbol>");
		
		tokenizer.advance();
		
		indentLevel--;
		writeIndentedLine("</subroutineBody>");
		
		indentLevel--;
		writeIndentedLine("</subroutineDec>");
	}

	/**
	 * Compiles a parameter list.
	 */
	private void compileParameterList() {
		
		//Parameter list opening symbol
		tokenizer.advance();
		writeIndentedLine("<symbol> ( </symbol>");
		
		//Parameter list
		writeIndentedLine("<parameterList>");
		
		tokenizer.advance();
		
		if(tokenizer.tokenType() == TokenType.KEYWORD) {
			
			indentLevel++;
			compileType();
			writeIndentedLine("<identifier> " + tokenizer.identifier() + " </identifier>");
			
			tokenizer.advance();
			// (',' varName)
			while (tokenizer.symbol() == ',') {
				
				writeIndentedLine("<symbol> , </symbol>");
				tokenizer.advance();
				
				writeIndentedLine("<keyword> " + tokenizer.identifier() +
						" </keyword>");
				tokenizer.advance();
				
				writeIndentedLine("<identifier> " + tokenizer.identifier() +
						" </identifier>");
				tokenizer.advance();
			}
			
			indentLevel--;			
		}
		
		writeIndentedLine("</parameterList>");		
		
		//Parameter list closing symbol
		writeIndentedLine("<symbol> ) </symbol>");		
	}

	/**
	 * Compiles variables for a Jack subroutine.
	 */
	private void compileVar() {
		
		writeIndentedLine("<varDec>");
		indentLevel++;
		
		while(tokenizer.tokenType() == TokenType.KEYWORD &&
				tokenizer.keyWord() == KeyWord.VAR) {
			
			
			writeIndentedLine("<keyword> var </keyword>");
			tokenizer.advance();
			
			//Type
			compileType();
			
			// varName
			writeIndentedLine("<identifier> " + tokenizer.identifier() +
					" </identifier>");
			tokenizer.advance();
			
			// (',' varName)
			while (tokenizer.symbol() == ',') {
				
				writeIndentedLine("<symbol> , </symbol>");
				tokenizer.advance();
				
				writeIndentedLine("<identifier> " + tokenizer.identifier() +
						" </identifier>");
				tokenizer.advance();
			}
			
			// ;
			writeIndentedLine("<symbol> ; </symbol>");
			tokenizer.advance();
		}
		
		indentLevel--;
		writeIndentedLine("</varDec>");
	}
	
	/**
	 * Compiles a statement.
	 */
	private void compileStatement() {
		
		//Let statement
		if(tokenizer.keyWord() == KeyWord.LET) {
			compileLet();
		
		//While loop
		} else if(tokenizer.keyWord() == KeyWord.WHILE) {
			compileWhile();
		
		//If statement
		} else if(tokenizer.keyWord() == KeyWord.IF) {
			compileIf();
		}		
	}
	
	/**
	 * Compiles a let statement.
	 */
	private void compileLet() {
		
		writeIndentedLine("<letStatement>");
		indentLevel++;
		
		//Write keyword
		writeIndentedLine("<keyword> let </keyword>");
		tokenizer.advance();
		
		//Write identifier
		writeIndentedLine("<identifier> " + tokenizer.identifier() + " </identifier>");
		
		//Write operator
		tokenizer.advance();
		if(tokenizer.tokenType() == TokenType.SYMBOL)
			writeIndentedLine("<symbol> " + tokenizer.symbol() + " </symbol>");
		
		//Write identifier
		tokenizer.advance();
		writeIndentedLine("<expression>");
		indentLevel++;
		
		if(tokenizer.tokenType() == TokenType.IDENTIFIER) {
			
			writeIndentedLine("<term>");
			indentLevel++;			
			writeIndentedLine("<identifier> " + tokenizer.identifier() + " </identifier>");
			indentLevel--;
			writeIndentedLine("</term>");
		}
		
		indentLevel--;
		writeIndentedLine("</expression>");
		
		// ;
		tokenizer.advance();
		writeIndentedLine("<symbol> ; </symbol>");
		
		indentLevel--;
		writeIndentedLine("</letStatement>");
	}
	
	/**
	 * Compiles a while loop.
	 */
	private void compileWhile() {
		
		writeIndentedLine("<whileStatement>");
		indentLevel++;
		
		//While
		writeIndentedLine("<keyword> while </keyword>");

		tokenizer.advance();
		if(tokenizer.tokenType() == TokenType.SYMBOL)
			writeIndentedLine("<symbol> " + tokenizer.symbol() + " </symbol>");
		
		tokenizer.advance();
		writeIndentedLine("<expression>");
		indentLevel++;
		
		if(tokenizer.tokenType() == TokenType.IDENTIFIER) {
			
			writeIndentedLine("<term>");
			indentLevel++;			
			writeIndentedLine("<identifier> " + tokenizer.identifier() + " </identifier>");
			indentLevel--;
			writeIndentedLine("</term>");		
		}
		
		indentLevel--;
		writeIndentedLine("</expression>");
		
		tokenizer.advance();
		if(tokenizer.tokenType() == TokenType.SYMBOL)
			writeIndentedLine("<symbol> " + tokenizer.symbol() + " </symbol>");
		
		tokenizer.advance();
		if(tokenizer.tokenType() == TokenType.SYMBOL)
			writeIndentedLine("<symbol> " + tokenizer.symbol() + " </symbol>");
		
		writeIndentedLine("<statements>");
		indentLevel++;
		
		//Compile inner statements
		tokenizer.advance();
		compileStatement();
		
		indentLevel--;
		writeIndentedLine("</statements>");			
		
		tokenizer.advance();
		if(tokenizer.tokenType() == TokenType.SYMBOL)
			writeIndentedLine("<symbol> " + tokenizer.symbol() + " </symbol>");		
		
		indentLevel--;
		writeIndentedLine("</whileStatement>");
	}

	/**
	 * Compiles an if statement.
	 */
	private void compileIf() {
		
		writeIndentedLine("<ifStatement>");
		indentLevel++;
		
		//Write keyword
		writeIndentedLine("<keyword> if </keyword>");		
		
		tokenizer.advance();
		if(tokenizer.tokenType() == TokenType.SYMBOL)
			writeIndentedLine("<symbol> " + tokenizer.symbol() + " </symbol>");
		
		tokenizer.advance();
		writeIndentedLine("<expression>");
		indentLevel++;
		
		if(tokenizer.tokenType() == TokenType.IDENTIFIER) {
			
			writeIndentedLine("<term>");
			indentLevel++;			
			writeIndentedLine("<identifier> " + tokenizer.identifier() + " </identifier>");
			indentLevel--;
			writeIndentedLine("</term>");
		}		
		
		indentLevel--;
		writeIndentedLine("</expression>");
		
		tokenizer.advance();
		if(tokenizer.tokenType() == TokenType.SYMBOL)
			writeIndentedLine("<symbol> " + tokenizer.symbol() + " </symbol>");
		
		tokenizer.advance();
		if(tokenizer.tokenType() == TokenType.SYMBOL)
			writeIndentedLine("<symbol> " + tokenizer.symbol() + " </symbol>");
		
		writeIndentedLine("<statements>");
		indentLevel++;
		
		//Compile inner statements
		tokenizer.advance();
		compileStatement();
		
		indentLevel--;
		writeIndentedLine("</statements>");			
		
		tokenizer.advance();
		if(tokenizer.tokenType() == TokenType.SYMBOL)
			writeIndentedLine("<symbol> " + tokenizer.symbol() + " </symbol>");
		
		indentLevel--;
		writeIndentedLine("</ifStatement>");		
	}
	
}
