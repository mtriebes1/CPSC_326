/**
 * Author: Matthew Triebes
 * Assign: 2
 *
 * The lexer implementation tokenizes a given input stream. The lexer
 * implements a pull-based model via the nextToken function such that
 * each call to nextToken advances the lexer to the next token (which
 * is returned by nextToken). The file has been completed read when
 * nextToken returns the EOS token. Lexical errors in the source file
 * result in the nextToken function throwing a MyPL Exception.
 */

//import java.util.*;
import java.io.*;


public class Lexer 
{

  private BufferedReader buffer; // handle to input stream
  private int line;
  private int column;
  
  /** 
   */
  public Lexer(InputStream instream) {
    buffer = new BufferedReader(new InputStreamReader(instream));
    this.line = 1;
    this.column = 0;
  }

  /**
   * Returns next character in the stream. Returns -1 if end of file.
   */
  private int read() throws MyPLException {
    try {
      int ch = buffer.read();
      return ch;
    } catch(IOException e) {
      error("read error", line, column + 1);
    }
    return -1;
  }

  /** 
   * Returns next character without removing it from the stream.
   */
  private int peek() throws MyPLException {
    int ch = -1;
    try {
      buffer.mark(1);
      ch = read();
      buffer.reset();
    } catch(IOException e) {
      error("read error", line, column + 1);
    }
    return ch;
  }


  /**
   * Print an error message and exit the program.
   */
  private void error(String msg, int line, int column) throws MyPLException {
    throw new MyPLException("Lexer", msg, line, column);
  }

  /**
 * @return 
   */
  public Token nextToken() throws MyPLException {
  
  TokenType temptype = TokenType.EOS; 
  String lex = "";
  int temp = read();

  if (temp == -1) // End of File
  { 
    //line++;
    temptype = TokenType.EOS;
    column = 0;
    lex = "";
  }
  else if ((char)temp == '\n') { // for new line
    //read()
    line++;
    column = 0;
    return nextToken();
  }
  else if (temp == '#') //for comments
  {
    while (peek() != '\n') { 
      read();
    }
    return nextToken();
  }
  else if (Character.isWhitespace((char)temp)) // Whitespace
  { 
    column++;
    return nextToken();
  }
  
  // ------------------------------------------------------------ basic symbols

  else if (temp == ',') { // Comma
    temptype = TokenType.COMMA;
    column++;
    lex = ",";
  }
  else if (temp == '.') { // Period
    temptype = TokenType.DOT;
    lex = ".";
    column++;
  }
  else if (temp == '+') { // plus
    temptype = TokenType.PLUS;
    lex = "+";
    column++;
  }
  else if (temp == '-') // minus
  { 
    temptype = TokenType.MINUS;
    lex = "-";
    column++;
  }
  else if (temp == '*') // multiply
  { 
    temptype = TokenType.MULTIPLY;
    lex = "*";
    column++;
  }
  else if (temp == '/') // divide
  { 
    temptype = TokenType.DIVIDE;
    lex = "/";
    column++;
  }
  else if (temp == '%') // modulo
  { 
    temptype = TokenType.MODULO;
    lex = "%";
    column++;
  }
  else if (temp == '=') // equal
  { 
    temptype = TokenType.EQUAL;
    lex = "=";
    column++;
  }
  else if (temp == '>') // greater than
  { 
    temptype = TokenType.GREATER_THAN;
    lex = ">";
    column++;
    if (peek() == '=')
    {
      read();
      lex = ">=";
      column++;
      temptype = TokenType.GREATER_THAN_EQUAL;
    }

  }
  else if (temp == '<') // less than
  { 
    temptype = TokenType.LESS_THAN;
    lex = "<";
    column++;
    if (peek() == '=')
    {
      read();
      lex = "<=";
      column++;
      temptype = TokenType.LESS_THAN_EQUAL;
    }
  }
  else if (temp == '!') // not equal
  { 
    column++;
    temp = read();
    if (temp != '=') 
    {
      error("Expected '=' after '!' ", line, column);
    }
    lex = "!=";
    temptype = TokenType.NOT_EQUAL;
  }
  else if (temp == '(') // left parenthesis
  { 
    temptype = TokenType.LPAREN;
    lex = "(";
    column++;
  }
  else if (temp == ')') // right parenthesis
  { 
    temptype = TokenType.RPAREN;
    lex = ")";
    column++;
  }
  else if (temp == ':') // assignment
  { 
    temptype = TokenType.ASSIGN;
    column++;
    if (peek() != '=') {
      error("Expected '=' after ':' ", line, column);
    }
    read();
    lex = ":=";
    column++;
  }
  
  // ------------------------------------------------------------ data types
  
  else if (temp == '"') // string val
  { 
    column++;
    temp = read();
    while (temp != '"') {
      lex = lex + (char)temp;
      column++;
      if ((char)temp == '\n'){
        error("Newline found within string", line, column);
      }
      temp = read();
    }
    temptype = TokenType.STRING_VAL;
  }

  else if (temp > 47 & temp < 58) // int or double val
  { 
    column++;
    lex = "" + (char)temp;
    if (temp == 48 & (peek() > 47 & peek() < 58)) {
      error("Leading 0 in " + (lex + (char)peek()), line, column);
    }
    if (!(peek() > 47 & peek() < 58) & peek() != '.' & !Character.isWhitespace((char)peek()) & peek() != '+' & peek() != '-' & peek() != '*' & peek() != '/' & peek() != '(' & peek() != ')') {
      column++;
      error("Unexpected value " + (char)peek() + " in integer", line, column);
    }
    while (peek() > 47 & peek() < 58) 
    {
      temp = read();
      lex = lex + (char)temp;
      column++;
      if (!(peek() > 47 & peek() < 58) & peek() != '.' & !Character.isWhitespace((char)peek()) & peek() != '+' & peek() != '-' & peek() != '*' & peek() != '/' & peek() != '(' & peek() != ')') {
        column++;
        error("Unexpected value " + (char)peek() + " in integer", line, column);
      }
    }
    if (peek() == '.') // if val is a double
    {
      temp = read();
      lex = lex + (char)temp;
      column++;
      if(!(peek() > 47 & peek() < 58))
      {
        error("Missing Digit in Float: " + lex, line, column);
      }
      temptype = TokenType.DOUBLE_VAL;
      while (peek() > 47 & peek() < 58) 
      {
        lex = lex + (char)read();
        column++;
      }
    }
    else 
      temptype = TokenType.INT_VAL;
  }
  
  else if (temp == '\'') //char val
  {
    column++;
    temp = read();
    lex = lex + (char)temp;
    column++;
    read();
    //column++;
    temptype = TokenType.CHAR_VAL;
  }

  // ------------------------------------------------------------ Reserved words, identifiers, bool value
  
  else 
  {
    if (temp == 95 | (temp > 47 & temp < 58)) {
      column++;
      error("leading '" + (char)temp + "'" + " not allowed in variable name" , line, column);
    }
    
    while ((temp > 96 & temp < 123) | (temp > 64 & temp < 91) | (temp == 95) | (temp > 47 & temp < 58)) 
    {
      lex = lex + (char)temp;
      column++;
      if (peek() == '#' | peek() == '?' | peek() == ';' | peek() == '.' | peek() == ',') {
        error("unexpected value '" + (char)peek() + "'" + " in '" + lex + "'", line, column);
      }
      if ((char)peek() == '\n' | Character.isWhitespace((char)peek()) | (peek() == ',') | (peek() == '.') | (peek() == '+') | (peek() == '-') | (peek() == '*') | (peek() == '/') | (peek() == '%') | (peek() == '=') | (peek() == '>') | (peek() == '<') | (peek() == '!') | (peek() == '(') | (peek() == ')') | (peek() == ':')) {
        break;
      }
      else
        temp = read();
    }
    if (lex.equals("int")) //int_type
      temptype = TokenType.INT_TYPE;
    else if (lex.equals("bool")) //bool_type
      temptype = TokenType.BOOL_TYPE;
    else if (lex.equals("double")) //double_type
      temptype = TokenType.DOUBLE_TYPE;
    else if (lex.equals("char")) //char_type
      temptype = TokenType.CHAR_TYPE;
    else if (lex.equals("string")) //string_type
      temptype = TokenType.STRING_TYPE;
    else if (lex.equals("type")) //type
      temptype = TokenType.TYPE;
    else if (lex.equals("and")) //and
      temptype = TokenType.AND;
    else if (lex.equals("or")) //or
      temptype = TokenType.OR;
    else if (lex.equals("not")) //not
      temptype = TokenType.NOT;
    else if (lex.equals("neg")) //negative
      temptype = TokenType.NEG;
    else if (lex.equals("while")) //while
      temptype = TokenType.WHILE;
    else if (lex.equals("for")) //for
      temptype = TokenType.FOR;
    else if (lex.equals("to")) //to
      temptype = TokenType.TO;
    else if (lex.equals("do")) //do
      temptype = TokenType.DO;
    else if (lex.equals("if")) //if
      temptype = TokenType.IF;
    else if (lex.equals("then")) //then
      temptype = TokenType.THEN;
    else if (lex.equals("else")) //else
      temptype = TokenType.ELSE;
    else if (lex.equals("elif")) //elif
      temptype = TokenType.ELIF;
    else if (lex.equals("end")) //end
      temptype = TokenType.END;
    else if (lex.equals("fun")) //fun
      temptype = TokenType.FUN;
    else if (lex.equals("var")) //var
      temptype = TokenType.VAR;
    else if (lex.equals("set")) //set
      temptype = TokenType.SET;
    else if (lex.equals("return")) //return
      temptype = TokenType.RETURN;
    else if (lex.equals("new")) //new
      temptype = TokenType.NEW;
    else if (lex.equals("nil")) //nil
      temptype = TokenType.NIL;
    else if (lex.equals("true")) //bool val - true
      temptype = TokenType.BOOL_VAL;
    else if (lex.equals("false")) // bool val - false
      temptype = TokenType.BOOL_VAL;
    else  
      temptype = TokenType.ID;
    }
  return new Token(temptype, lex, line, column);
} 
  
}
